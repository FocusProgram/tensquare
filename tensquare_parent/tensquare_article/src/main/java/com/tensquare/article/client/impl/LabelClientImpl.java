package com.tensquare.article.client.impl;

import com.tensquare.article.client.LabelClient;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import org.springframework.stereotype.Component;

@Component
public class LabelClientImpl implements LabelClient {
    @Override
    public Result findById(String labelid) {
        return new Result(true, StatusCode.OK,"远程调用接口失败");
    }

    @Override
    public Result findAll() {
        return new Result(true, StatusCode.OK,"远程调用接口失败");
    }
}
