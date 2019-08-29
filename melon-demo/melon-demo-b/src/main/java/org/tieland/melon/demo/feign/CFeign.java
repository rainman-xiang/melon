package org.tieland.melon.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhouxiang
 * @date 2019/8/29 17:18
 */
@FeignClient("c")
public interface CFeign {

    @GetMapping("/test")
    String test();

}
