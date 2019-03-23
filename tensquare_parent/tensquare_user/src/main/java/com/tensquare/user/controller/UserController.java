package com.tensquare.user.controller;


import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.regexp.internal.RE;
import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.context.StandardReactiveWebEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.tensquare.user.pojo.User;
import com.tensquare.user.service.UserService;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.Soundbank;


/**
 * 控制器层
 *
 * @author Administrator
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 更新当前用户的关注数量和好友的粉丝数量
     *
     * @param userid
     * @param friendid
     */
    @RequestMapping(value = "/{userid}/{friendid}/count", method = RequestMethod.PUT)
    public void updateFanscountAndFollowcount(@PathVariable int count, @PathVariable String userid, @PathVariable String friendid) {
        userService.updateFanscountAndFollowcount(count, userid, friendid);
    }

    /**
     * 用户登陆
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestBody User user) {
        User userLogin = userService.login(user);
        if (userLogin == null) {
            return new Result(false, StatusCode.LOGINERROR, "登陆失败");
        }
        //生成token
        String token = jwtUtil.createJWT(userLogin.getId(), userLogin.getMobile(), "user");
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("roles", "user");
        map.put("user", userLogin);
        return new Result(true, StatusCode.OK, "登陆成功", map);
    }

    /**
     * 发送短信
     *
     * @return
     */
    @RequestMapping(value = "/sendsms/{mobile}", method = RequestMethod.POST)
    public Result sendSms(@PathVariable String mobile) {
        userService.sendSms(mobile);
        return new Result(true, StatusCode.OK, "发送成功");
    }

    /**
     * 修改密码验证短信
     */
    @RequestMapping(value = "/sendsms/forget/{mobile}", method = RequestMethod.GET)
    public Result forgetSendSms(@PathVariable String mobile) {
        User user = userService.findUserByMobile(mobile);
        if (user == null) {
            return new Result(false, StatusCode.ERROR,"用户手机号不存在");
        } else {
            userService.sendSms(mobile);
            return new Result(true, StatusCode.OK, "发送成功");
        }
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/updatePwd/{code}",method = RequestMethod.POST)
    public Result updatePwd(@RequestBody User user,@PathVariable String code){
        //得到缓存中的验证码
        String checkcodeRedis = (String) redisTemplate.opsForValue().get("checkcode_" + user.getMobile());

        //判断验证是否存在
        if (checkcodeRedis == null || checkcodeRedis.isEmpty()) {
            return new Result(false, StatusCode.ERROR, "请先获取手机验证码");
        }

        //验证验证码是否正确
        if (!checkcodeRedis.equals(code)) {
            return new Result(false, StatusCode.ERROR, "请输入正确的验证码");
        }
        User userByMobile = userService.findUserByMobile(user.getMobile());
        user.setId(userByMobile.getId());
        userService.updatePwd(user);
        return new Result(true,StatusCode.OK,"修改密码成功");
    }


    /**
     * 用户注册
     *
     * @param user
     */
    @RequestMapping(value = "/register/{code}", method = RequestMethod.POST)
    public Result register(@RequestBody User user, @PathVariable String
            code) {
        //得到缓存中的验证码
        String checkcodeRedis = (String) redisTemplate.opsForValue().get("checkcode_" + user.getMobile());

        //判断验证是否存在
        if (checkcodeRedis == null || checkcodeRedis.isEmpty()) {
            return new Result(false, StatusCode.ERROR, "请先获取手机验证码");
        }

        //验证验证码是否正确
        if (!checkcodeRedis.equals(code)) {
            return new Result(false, StatusCode.ERROR, "请输入正确的验证码");
        }

        userService.add(user);
        return new Result(true, StatusCode.OK, "注册成功");
    }


    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", userService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", userService.findById(id));
    }


    /**
     * 分页+多条件查询
     *
     * @param searchMap 查询条件封装
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @RequestMapping(value = "/search/{page}/{size}", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<User> pageList = userService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<User>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", userService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param user
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody User user) {
        userService.add(user);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param user
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody User user, @PathVariable String id) {
        user.setId(id);
        userService.update(user);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        userService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

}
