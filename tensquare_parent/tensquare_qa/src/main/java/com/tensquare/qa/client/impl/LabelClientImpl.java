package com.tensquare.qa.client.impl;

import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.qa.client.LabelClient;
import org.springframework.stereotype.Component;

@Component
public class LabelClientImpl implements LabelClient {
    @Override
    public Result findById(String labelid) {
        return new Result(true, StatusCode.OK,"熔断器开启");
    }

    @Override
    public Result findAll() {
        return new Result(true, StatusCode.OK,"熔断器开启");
    }
}
