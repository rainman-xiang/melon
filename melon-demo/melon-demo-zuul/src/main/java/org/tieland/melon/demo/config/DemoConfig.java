package org.tieland.melon.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.tieland.melon.common.MelonConfig;
import org.tieland.melon.zuul.common.CacheMelonService;
import org.tieland.melon.zuul.common.MelonCacheConfig;
import org.tieland.melon.zuul.common.MelonService;

/**
 * @author zhouxiang
 * @date 2019/8/29 15:28
 */
@Configuration
public class DemoConfig {

    @Bean
    public MelonService melonService(StringRedisTemplate redisTemplate, MelonCacheConfig melonCacheConfig){
        CacheMelonService cacheMelonService = new CacheMelonService(redisTemplate, melonCacheConfig);
        cacheMelonService.init();
        return cacheMelonService;
    }

}
