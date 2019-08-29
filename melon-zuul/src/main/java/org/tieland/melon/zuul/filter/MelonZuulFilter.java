package org.tieland.melon.zuul.filter;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.tieland.melon.common.*;
import org.tieland.melon.core.*;
import org.tieland.melon.ribbon.MelonContextMesh;
import org.tieland.melon.ribbon.MelonContextMeshHolder;
import org.tieland.melon.zuul.common.ZuulConstants;
import org.tieland.melon.zuul.common.MelonService;
import org.tieland.melon.zuul.support.ZuulGrayRequestContext;

import java.util.*;

/**
 * Zuul filter 中 Melon总控逻辑
 * @author zhouxiang
 * @date 2019/8/27 14:05
 */
@Slf4j
public class MelonZuulFilter extends ZuulFilter {

    private MelonService melonService;

    private MelonConfig melonConfig;

    public MelonZuulFilter(MelonService melonService, MelonConfig melonConfig){
        this.melonService = melonService;
        this.melonConfig = melonConfig;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //MUST > 5 can get serviceId
        return ZuulConstants.GRAY_ZUUL_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        MelonContext melonContext = buildMelonContext();
        MelonInstance melonInstance = MelonInstanceFactory.get(melonConfig);
        log.debug(" melonContext:{}, melonInstance:{} ", melonContext, melonInstance);
        MelonContextMeshHolder.set(new MelonContextMesh(melonContext, melonInstance));
        String melonContextHeader = JSONObject.toJSONString(melonContext);
        log.debug(" melonContextHeader:{} ", melonContextHeader);
        RequestContext.getCurrentContext().addZuulRequestHeader(MelonConstants.MS_HEADER_MELON_CONTEXT, melonContextHeader);
        return null;
    }

    /**
     * 构建Melon上下文
     * @return
     */
    private MelonContext buildMelonContext(){
        MelonSettings melonSettings = melonService.getSettings();
        log.debug(" melonSettings:{} ", melonSettings);
        MelonContext.Builder builder = new MelonContext.Builder();
        builder.origin(MelonOrigin.GATEWAY);

        //不存在Melon配置时，任意选择
        if(melonSettings == null){
            log.debug(" no melon settings exist. ");
            return builder.grayOn(Boolean.FALSE).build();
        }

        //Melon Mode=NORMAL时，如果有指定常规primaryGroups配置时，流量走相应组
        //当没有指定时，则任意选择
        if(melonSettings.getMode() == MelonMode.NORMAL){
            log.debug(" melon settings exist. mode is normal. ");
            builder.grayOn(Boolean.FALSE);
            buildWithPrimaryGroups(builder, melonSettings);
            return builder.build();
        }

        //Melon Mode=GRAY，如果有应该灰度规则匹配，则流量走相应组
        //如果没有匹配:
        //   1.流量导向常规组，有primaryGroups配置，则走相应组
        //   2.如果没有primaryGroups配置，则导向排除所有灰度组和黑名单组之后的相应组
        if(melonSettings.getMode() == MelonMode.GRAY){
            log.debug(" melon settings exist. mode is gray. ");
            builder.grayOn(Boolean.TRUE);
            buildWithGraySettings(builder, melonSettings);
            return builder.build();
        }

        log.warn(" melonSettings is error. ");
        return null;
    }

    /**
     * 根据常规组primaryGroups配置，选择相应组
     * @param builder
     * @param melonSettings
     */
    private void buildWithPrimaryGroups(MelonContext.Builder builder, MelonSettings melonSettings){
        if(ArrayUtils.isNotEmpty(melonSettings.getPrimaryGroups())){
            String[] groups = melonSettings.getPrimaryGroups();
            String group = melonSettings.getPrimaryGroups()[0];
            String[] reserveGroups = ArrayUtils.subarray(groups, 1, groups.length);
            log.debug(" group:{}, reserveGroups:{} ", group, ArrayUtils.toString(reserveGroups));
            builder.group(group).reserveGroups(reserveGroups);
        }
    }

    /**
     * 根据灰度规则匹配，选择相应组
     * @param builder
     * @param melonSettings
     */
    private void buildWithGraySettings(MelonContext.Builder builder, MelonSettings melonSettings){
        List<GraySettings> graySettingsList = melonSettings.getGraySettingsList();
        Set<String> blackGroupList = new HashSet<>();
        boolean matched = false;
        //判断是否有灰度匹配
        if(CollectionUtils.isNotEmpty(graySettingsList)){
            for(GraySettings graySettings:graySettingsList){
                if(ArrayUtils.isEmpty(graySettings.getGroups())){
                    continue;
                }

                Arrays.stream(graySettings.getGroups()).forEach(group->blackGroupList.add(group));

                if(matched){
                    continue;
                }

                GrayRule rule = GrayRuleHub.get(graySettings.getRule());
                if(rule == null){
                    log.warn(" rule:{} is not found. ", graySettings.getRule());
                    continue;
                }

                ZuulGrayRequestContext grayRequestContext = new ZuulGrayRequestContext(RequestContext.getCurrentContext());
                if(rule.apply(grayRequestContext, JSONObject.parseObject(graySettings.getJson(), graySettings.getConditionClass()))){
                    String group = graySettings.getGroups()[0];
                    String[] reserveGroups = ArrayUtils.subarray(graySettings.getGroups(), 1, graySettings.getGroups().length);
                    builder.group(group).reserveGroups(reserveGroups).build();
                    matched = true;
                }
            }
        }

        //未匹配灰度
        if(!matched){
            log.debug(" not match any gray settings. it will select primary group.");
            buildWithPrimaryGroups(builder, melonSettings);
        }

        if(ArrayUtils.isNotEmpty(melonSettings.getForbiddenGroups())){
            Arrays.stream(melonSettings.getForbiddenGroups()).forEach(group->blackGroupList.add(group));
        }

        if(CollectionUtils.isNotEmpty(blackGroupList)){
            String[] blackGroups = new String[blackGroupList.size()];
            blackGroupList.toArray(blackGroups);
            builder.blackGroups(blackGroups);
        }

    }

}
