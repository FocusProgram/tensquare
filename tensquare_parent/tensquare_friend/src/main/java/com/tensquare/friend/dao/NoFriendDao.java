package com.tensquare.friend.dao;

import com.tensquare.friend.pojo.NoFriend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoFriendDao extends JpaRepository<NoFriend,String> {

    /**
     * 判断用户是否已经添加非好友
     * @param userId
     * @param friendId
     * @return
     */
    public NoFriend findByUseridAndFriendid(String userId, String friendId);


}
