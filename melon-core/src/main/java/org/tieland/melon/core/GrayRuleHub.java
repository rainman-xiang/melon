package org.tieland.melon.core;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 灰度规则总线，可根据SPI扩展
 * @author zhouxiang
 * @date 2019/8/28 9:24
 */
public final class GrayRuleHub {

    private static final Map<String, GrayRule> RULE_MAPPINGS = new ConcurrentHashMap<>();

    static{
        ServiceLoader<GrayRule> serviceLoader = ServiceLoader.load(GrayRule.class);
        if(serviceLoader == null){
            throw new RuntimeException();
        }

        Iterator<GrayRule> iterator = serviceLoader.iterator();
        List<GrayRule> rules = IteratorUtils.toList(iterator);
        if(CollectionUtils.isEmpty(rules)){
            throw new RuntimeException();
        }

        rules.forEach(rule->{
            if(StringUtils.isBlank(rule.name())){
                throw new RuntimeException();
            }

            RULE_MAPPINGS.put(rule.name(), rule);
        });
    }

    private GrayRuleHub(){
        //
    }

    public static GrayRule get(String key){
        return RULE_MAPPINGS.get(key);
    }

}
