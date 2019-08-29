package org.tieland.melon.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tieland.melon.demo.feign.CFeign;

/**
 * @author zhouxiang
 * @date 2019/8/28 13:48
 */
@RestController
public class TestController {

    @Autowired
    private CFeign cFeign;

    @GetMapping("/test")
    public String test(){
        return cFeign.test();
    }

    @GetMapping("/test1")
    public String test1(){
        return "B";
    }
}
