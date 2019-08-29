package org.tieland.melon.zuul.web;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 应对Hystrix异步线程变量传递
 * @author zhouxiang
 * @date 2019/8/27 15:26
 */
@Slf4j
public class MelonHystrixFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        if(!HystrixRequestContext.isCurrentThreadInitialized()){
            HystrixRequestContext.initializeContext();
        }

        try{
            filterChain.doFilter(servletRequest, servletResponse);
        }finally {
            if(HystrixRequestContext.getContextForCurrentThread() != null){
                HystrixRequestContext.getContextForCurrentThread().shutdown();
            }
        }
    }
}
