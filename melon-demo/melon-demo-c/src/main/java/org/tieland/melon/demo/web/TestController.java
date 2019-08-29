package org.tieland.melon.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouxiang
 * @date 2019/8/29 17:17
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "C";
    }

}
