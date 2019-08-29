package org.tieland.melon.common;

import lombok.ToString;

/**
 * @author zhouxiang
 * @date 2019/8/27 9:11
 */
@ToString
public final class MelonInstance {

    /**
     * 服务所在组
     */
    private String group;

    /**
     * 是否网关
     */
    private boolean gateway;

    public MelonInstance(String group, boolean gateway){
        this.group = group;
        this.gateway = gateway;
    }

    public String getGroup() {
        return group;
    }

    public boolean isGateway() {
        return gateway;
    }
}
