package com.tensquare.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tensquare.user.pojo.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface UserDao extends JpaRepository<User,String>,JpaSpecificationExecutor<User>{

    /**
     * 根据手机号查询用户信息
     * @param mobile
     * @return
     */
    public User findByMobile(String mobile);

    /**
     * 更新密码
     */
    @Modifying
    @Query(value = "UPDATE tb_user SET password=?1 WHERE id =?2",nativeQuery = true)
    public void updatePassword(String password,String id);

    /**
     * 更新当前用户的关注数
     * @param userid
     */
    @Modifying
    @Query(value = "UPDATE tb_user SET followcount=followcount+?1 WHERE id = ?2",nativeQuery = true)
    public void updateFollowcount(int count,String userid);

    /**
     * 更新好友的粉丝数
     * @param friendid
     */
    @Modifying
    @Query(value = "UPDATE tb_user SET fanscount=fanscount+?1 WHERE id = ?2",nativeQuery = true)
    public void updateFanscount(int count,String friendid);


}
