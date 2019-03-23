package com.tensquare.friend.controller;

import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.friend.client.UserClient;
import com.tensquare.friend.dao.NoFriendDao;
import com.tensquare.friend.service.FriendService;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/friend")
@Transactional
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserClient userClient;

    /**
     * 删除好友
     * @param friendid
     * @return
     */
    @RequestMapping(value="/{friendid}",method=RequestMethod.DELETE)
    public Result remove(@PathVariable String friendid){
        //从request中获取Claims,验证是都满足user权限
        Claims clamis_user = (Claims) request.getAttribute("clamis_user");
        if (clamis_user == null) {
            return new Result(false, StatusCode.ACCESSERROR, "无权限访问");
        }
        friendService.deleteFriend(clamis_user.getId(), friendid);
        userClient.updateFanscountAndFollowcount(-1,clamis_user.getId(),friendid);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 添加好友或者非好友
     * @param friendid
     * @param type
     * @return
     */
    @RequestMapping(value = "/like/{friendid}/{type}", method = RequestMethod.PUT)
    public Result addFriend(@PathVariable String friendid, @PathVariable String type) {
        //从request中获取Claims,验证是都满足user权限
        Claims clamis_user = (Claims) request.getAttribute("clamis_user");
        if (clamis_user == null) {
            return new Result(false, StatusCode.ACCESSERROR, "无权限访问");
        }
        //判断type的类型,1:喜欢 2：不喜欢
        if (!StringUtils.isEmpty(type)) {
            //喜欢
            if (type.equals("1")) {
                int result = friendService.addFriend(clamis_user.getId(), friendid);
                if (result == 0) {
                    return new Result(false, StatusCode.ERROR, "已经添加好友，不能重复添加");
                }else{
                    //更新当前用户的关注数和和好友的粉丝数
                    userClient.updateFanscountAndFollowcount(1,clamis_user.getId(),friendid);
                }
                //不喜欢
            } else if (type.equals("2")) {
                int result = friendService.addNoFriend(clamis_user.getId(), friendid);
                if (result == 0) {
                    return new Result(false, StatusCode.ERROR, "已经添加非好友，不能重复添加");
                }
            }
        } else {
            return new Result(false, StatusCode.ERROR, "参数异常");
        }
        return new Result(true, StatusCode.OK, "添加成功");
    }
}
