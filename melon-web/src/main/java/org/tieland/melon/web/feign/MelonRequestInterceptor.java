package org.tieland.melon.web.feign;

import com.alibaba.fastjson.JSONObject;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.tieland.melon.common.MelonConstants;
import org.tieland.melon.common.MelonException;
import org.tieland.melon.ribbon.MelonContextMesh;
import org.tieland.melon.ribbon.MelonContextMeshHolder;

/**
 * @author zhouxiang
 * @date 2019/8/27 13:19
 */
@Slf4j
public class MelonRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        MelonContextMesh contextMesh = MelonContextMeshHolder.get();
        if(contextMesh == null){
            throw new MelonException(" melon context is not exist. ");
        }
        String melonContextHeader = JSONObject.toJSONString(contextMesh.getContext());
        log.debug(" feign request interceptor melon context header:{} ", melonContextHeader);
        requestTemplate.header(MelonConstants.MS_HEADER_MELON_CONTEXT, melonContextHeader);
    }
}
