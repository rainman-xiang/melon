package org.tieland.melon.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhouxiang
 * @date 2019/8/27 11:54
 */
@Data
@ConfigurationProperties(prefix = "melon")
public class MelonConfig {

    private boolean enabled = true;

    /**
     * 必须指定组
     */
    private String group;

    /**
     * 不需要设置，系统自动判断
     */
    private boolean gateway = false;

}
