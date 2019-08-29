package org.tieland.melon.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouxiang
 * @date 2019/8/28 13:48
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "a";
    }

    @GetMapping("/test1")
    public String test1(){
        return "a";
    }
}
