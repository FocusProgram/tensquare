package com.tensquare.article.client.impl;

import com.tensquare.article.client.LabelClient;
import com.tensquare.article.client.UserClient;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import org.springframework.stereotype.Component;

import javax.sound.midi.Track;

@Component
public class UserClientImpl implements UserClient {

    @Override
    public Result findById(String id) {
        return new Result(true,StatusCode.OK,"远程调用接口失败");
    }
}
