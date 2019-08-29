package org.tieland.melon.web.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.tieland.melon.common.MelonContext;
import org.tieland.melon.common.MelonException;
import org.tieland.melon.common.MelonOrigin;

/**
 * @author zhouxiang
 * @date 2019/8/27 11:23
 */
public final class MelonContextFactory {

    private MelonContextFactory(){
        //
    }

    /**
     * 创建本地MelonContext
     * @return
     */
    public static MelonContext buildWithLocal(){
        MelonContext.Builder builder = new MelonContext.Builder();
        MelonContext melonContext = builder.origin(MelonOrigin.OTHER).build();
        return melonContext;
    }

    /**
     * 从header中还原MelonContext
     * @param melonContextHeader
     * @return
     */
    public static MelonContext buildWithHeader(String melonContextHeader){
        JSONObject jsonObject = JSONObject.parseObject(melonContextHeader);
        String group =jsonObject.getString("group");
        String origin =jsonObject.getString("origin");
        Boolean grayOn =jsonObject.getBoolean("grayOn");
        String[] reserveGroups = null;
        String[] blackGroups = null;
        JSONArray reserveGroupsJSONArray = jsonObject.getJSONArray("reserveGroups");
        JSONArray blackGroupsJSONArray = jsonObject.getJSONArray("blackGroups");

        if(StringUtils.isBlank(origin)
                ||MelonOrigin.valueOf(origin) == null){
            throw new MelonException("origin illegal");
        }

        if(reserveGroupsJSONArray != null){
            reserveGroups = new String[reserveGroupsJSONArray.size()];
            reserveGroupsJSONArray.toArray(reserveGroups);
        }

        if(blackGroupsJSONArray != null){
            blackGroups = new String[blackGroupsJSONArray.size()];
            blackGroupsJSONArray.toArray(blackGroups);
        }

        MelonContext.Builder builder = new MelonContext.Builder();
        return builder.group(group).origin(MelonOrigin.valueOf(origin))
                .grayOn(grayOn).reserveGroups(reserveGroups).blackGroups(blackGroups).build();
    }

}
