package org.tieland.melon.web.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.tieland.melon.common.MelonConfig;
import org.tieland.melon.ribbon.MelonZoneAvoidanceRule;
import org.tieland.melon.web.feign.MelonRequestInterceptor;
import org.tieland.melon.web.filter.MelonContextFilter;

/**
 * @author zhouxiang
 * @date 2019/8/27 13:34
 */
@Configuration
@EnableConfigurationProperties(value = {MelonConfig.class})
@ConditionalOnProperty(value = "melon.enabled", havingValue = "true")
@RibbonClients(defaultConfiguration = MelonZoneAvoidanceRule.class)
public class MelonWebAutoConfig {

    @Autowired
    private MelonConfig melonConfig;

    @Bean
    public MelonRequestInterceptor melonRequestInterceptor(){
        return new MelonRequestInterceptor();
    }

    @Bean
    public MelonContextFilter melonContextFilter(){
        return new MelonContextFilter(melonConfig);
    }

    @Autowired
    public void configEureka(EurekaInstanceConfigBean eurekaInstanceConfigBean){
        if(melonConfig == null || StringUtils.isBlank(melonConfig.getGroup())){
            throw new IllegalArgumentException(" Melon config error.group is null or empty. ");
        }
        melonConfig.setGateway(Boolean.FALSE);
        eurekaInstanceConfigBean.setAppGroupName(melonConfig.getGroup());
    }


}
