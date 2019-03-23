package com.tensquare.article.client;

import com.tensquare.article.client.impl.UserClientImpl;
import com.tensquare.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @FeignClient注解用于指定从哪个服务中调用功能 ，注意 里面的名称与被调用的服务名保持一致，并且不能包含下划线
 *
 */
@Component
@FeignClient(value = "tensquare-user",fallback = UserClientImpl.class)
public interface UserClient {
    /**
     * @RequestMapping注解用于对被调用的微服务进行地址映射。注意 @PathVariable注解一定要指定参数名称，否则出错
     * @param id
     * @return
     */
    @RequestMapping(value="/user/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id);
}
