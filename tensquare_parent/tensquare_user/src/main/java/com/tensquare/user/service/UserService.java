package com.tensquare.user.service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.tensquare.user.pojo.Admin;
import com.tensquare.util.IdWorker;
import com.tensquare.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import com.tensquare.user.dao.UserDao;
import com.tensquare.user.pojo.User;

/**
 * 服务层
 *
 * @author Administrator
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<User> findAll() {
        return userDao.findAll();
    }

    /**
     * 根据手机号查询用户信息
     */
    public User findUserByMobile(String mobile){
        return userDao.findByMobile(mobile);
    }

    /**
     * 修改密码
     */
    public void updatePwd(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userDao.updatePassword(user.getPassword(),user.getId());
    }

    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<User> findSearch(Map whereMap, int page, int size) {
        Specification<User> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return userDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<User> findSearch(Map whereMap) {
        Specification<User> specification = createSpecification(whereMap);
        return userDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public User findById(String id) {
        return userDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param user
     */
    public void add(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword())); //加密密码
        user.setId(idWorker.nextId() + "");
        user.setFollowcount(0);//关注数
        user.setFanscount(0);//粉丝数
        user.setOnline(0L);//在线时长
        user.setRegdate(new Date());//注册日期
        user.setUpdatedate(new Date());//更新日期
        user.setLastdate(new Date());//最后登陆日期
        user.setAvatar("http://localhost:3000/a245cfd671352a050865e6c7f213d83b.jpg"); //设置注册默认头像
        userDao.save(user);
    }

    /**
     * 修改
     *
     * @param user
     */
    public void update(User user) {
        userDao.save(user);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        String header = httpServletRequest.getHeader("Authorization");
//        //判断请求头是否为空
//        if (StringUtils.isEmpty(header)){
//            throw new RuntimeException("权限不足");
//
//        }
//        //判断请求头是以Beare 开头
//        if (!header.startsWith("Beare ")){
//            throw new RuntimeException("权限不足");
//
//        }
//        //得到token
//        String token = header.substring(6);
//
//        try {
//            Claims claims = jwtUtil.parseJWT(token);
//            String roles = (String) claims.get("roles");
//            if (roles == null || !roles.equals("admin")){
//                throw new RuntimeException("权限不足");
//
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("权限不足");
//
//        }
        String token = (String) httpServletRequest.getAttribute("clamis_admin");
        if (token == null){
            throw new RuntimeException("权限不足");
        }
        userDao.deleteById(id);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<User> createSpecification(Map searchMap) {

        return new Specification<User>() {

            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                    predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
                }
                // 手机号码
                if (searchMap.get("mobile") != null && !"".equals(searchMap.get("mobile"))) {
                    predicateList.add(cb.like(root.get("mobile").as(String.class), "%" + (String) searchMap.get("mobile") + "%"));
                }
                // 密码
                if (searchMap.get("password") != null && !"".equals(searchMap.get("password"))) {
                    predicateList.add(cb.like(root.get("password").as(String.class), "%" + (String) searchMap.get("password") + "%"));
                }
                // 昵称
                if (searchMap.get("nickname") != null && !"".equals(searchMap.get("nickname"))) {
                    predicateList.add(cb.like(root.get("nickname").as(String.class), "%" + (String) searchMap.get("nickname") + "%"));
                }
                // 性别
                if (searchMap.get("sex") != null && !"".equals(searchMap.get("sex"))) {
                    predicateList.add(cb.like(root.get("sex").as(String.class), "%" + (String) searchMap.get("sex") + "%"));
                }
                // 头像
                if (searchMap.get("avatar") != null && !"".equals(searchMap.get("avatar"))) {
                    predicateList.add(cb.like(root.get("avatar").as(String.class), "%" + (String) searchMap.get("avatar") + "%"));
                }
                // E-Mail
                if (searchMap.get("email") != null && !"".equals(searchMap.get("email"))) {
                    predicateList.add(cb.like(root.get("email").as(String.class), "%" + (String) searchMap.get("email") + "%"));
                }
                // 兴趣
                if (searchMap.get("interest") != null && !"".equals(searchMap.get("interest"))) {
                    predicateList.add(cb.like(root.get("interest").as(String.class), "%" + (String) searchMap.get("interest") + "%"));
                }
                // 个性
                if (searchMap.get("personality") != null && !"".equals(searchMap.get("personality"))) {
                    predicateList.add(cb.like(root.get("personality").as(String.class), "%" + (String) searchMap.get("personality") + "%"));
                }

                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

            }
        };

    }

    /**
     * 发送短信
     *
     * @param mobile
     */
    public void sendSms(String mobile) {

        //生成六位随机数
        String randomNumeric = RandomStringUtils.randomNumeric(6);

        //添加到缓存中
        redisTemplate.opsForValue().set("checkcode_" + mobile, randomNumeric, 6, TimeUnit.HOURS);

        //用户发送短信随机码
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("checkcode", randomNumeric);
        rabbitTemplate.convertAndSend("sms", map);

        //控制台显示
        System.out.println("短信验证码为:" + randomNumeric);

    }

    /**
     * 用户登陆
     * @param user
     * @return
     */
    public User login(User user) {
        //1.通过用户名查询用户信息
        User userLogin = userDao.findByMobile(user.getMobile());
        //2.验证用户密码是否匹配
        if (userLogin != null && bCryptPasswordEncoder.matches(user.getPassword(),userLogin.getPassword())){
            //登陆成功
            return userLogin;
        }
        //登陆失败
        return null;
    }

    /**
     * 更新当前用户的关注数量和好友的粉丝数量
     * @param userid
     * @param friendid
     */
    public void updateFanscountAndFollowcount(int count,String userid, String friendid) {
        //更新当前用户的关注数
        userDao.updateFollowcount(count,userid);
        //更新好友的粉丝数
        userDao.updateFanscount(count,friendid);
    }
}
