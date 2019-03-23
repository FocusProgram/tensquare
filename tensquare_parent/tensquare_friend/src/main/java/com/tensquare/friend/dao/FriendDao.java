package com.tensquare.friend.dao;

import com.tensquare.friend.pojo.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FriendDao extends JpaRepository<Friend,String> {

    /**
     * 判断用户是否已经添加好友
     * @param userId
     * @param friendId
     * @return
     */
    public Friend findByUseridAndFriendid(String userId,String friendId);

    /**
     * 互相更新为喜欢
     * @param isLike
     * @param userId
     * @param friendId
     */
    @Modifying
    @Query(value = "UPDATE tb_friend SET islike = ?1 WHERE userid=?2 AND friendid=?3",nativeQuery = true)
    public void updateIsLike(String isLike,String userId,String friendId);

    /**
     * 删除好友
     * @param userid
     * @param friendid
     */
    @Modifying
    @Query(value = "DELETE FROM tb_friend WHERE userid=?1 AND friendid=?2",nativeQuery = true)
    public void deleteFriend(String userid, String friendid);
}
