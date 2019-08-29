package org.tieland.melon.zuul.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tieland.melon.common.MelonConfig;
import org.tieland.melon.ribbon.MelonZoneAvoidanceRule;
import org.tieland.melon.zuul.common.DefaultMelonService;
import org.tieland.melon.zuul.common.MelonCacheConfig;
import org.tieland.melon.zuul.common.MelonService;
import org.tieland.melon.zuul.filter.MelonZuulFilter;
import org.tieland.melon.zuul.web.MelonHystrixFilter;

/**
 * Zuul关于Melon配置
 * @author zhouxiang
 * @date 2019/8/28 11:43
 */
@Configuration
@EnableConfigurationProperties(value = {MelonConfig.class, MelonCacheConfig.class})
@ConditionalOnProperty(value = "melon.enabled", havingValue = "true")
@RibbonClients(defaultConfiguration = MelonZoneAvoidanceRule.class)
public class MelonZuulAutoConfig {

    @Autowired
    private MelonConfig melonConfig;

    @Autowired
    public void configEureka(EurekaInstanceConfigBean eurekaInstanceConfigBean){
        if(melonConfig == null || StringUtils.isBlank(melonConfig.getGroup())){
            throw new IllegalArgumentException(" Melon config error.group is null or empty. ");
        }
        melonConfig.setGateway(Boolean.TRUE);
        eurekaInstanceConfigBean.setAppGroupName(melonConfig.getGroup());
    }

    @ConditionalOnMissingClass(value = {"org.springframework.data.redis.core.RedisTemplate",
                                        "org.springframework.data.redis.core.StringRedisTemplate"})
    @ConditionalOnMissingBean
    @Bean
    public MelonService melonService(){
        return new DefaultMelonService();
    }

    @Bean
    public MelonZuulFilter melonZuulFilter(){
        return new MelonZuulFilter(melonService(), melonConfig);
    }

    @Bean
    public MelonHystrixFilter melonHystrixFilter(){
        return new MelonHystrixFilter();
    }

}
