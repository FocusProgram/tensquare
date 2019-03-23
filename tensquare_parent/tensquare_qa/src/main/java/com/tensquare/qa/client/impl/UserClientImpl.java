package com.tensquare.qa.client.impl;

import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.qa.client.UserClient;
import org.springframework.stereotype.Component;

@Component
public class UserClientImpl implements UserClient {

    @Override
    public Result findById(String id) {
        return new Result(true,StatusCode.OK,"远程调用接口失败");
    }
}
