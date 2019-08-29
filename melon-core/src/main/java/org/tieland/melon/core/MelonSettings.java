package org.tieland.melon.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Melon配置
 * @author zhouxiang
 * @date 2019/8/27 14:15
 */
@Data
@ToString
public class MelonSettings {

    /**
     * mode
     */
    private MelonMode mode;

    /**
     * 常规组
     */
    private String[] primaryGroups;

    /**
     * 禁用组(无流量)
     */
    private String[] forbiddenGroups;

    /**
     * 灰度规则list
     */
    private List<GraySettings> graySettingsList;

}
