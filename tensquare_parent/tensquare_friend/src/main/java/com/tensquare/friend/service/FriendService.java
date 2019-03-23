package com.tensquare.friend.service;

import com.tensquare.friend.dao.FriendDao;
import com.tensquare.friend.dao.NoFriendDao;
import com.tensquare.friend.pojo.Friend;
import com.tensquare.friend.pojo.NoFriend;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@Transactional
public class FriendService {

    @Autowired
    private FriendDao friendDao;

    @Autowired
    private NoFriendDao noFriendDao;

    /**
     * 添加好友
     *
     * @param id
     * @param friendid
     */
    public int addFriend(String userId, String friendid) {
        //1.验证参数是否为空
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(friendid)) {
            return 0;
        }
        //2.判断是否已经添加好友
        Friend friend = friendDao.findByUseridAndFriendid(userId, friendid);
        if (friend != null) {
            return 0;
        }
        //3.如果没有添加好友则添加好友
        friend = new Friend();
        friend.setUserid(userId);
        friend.setFriendid(friendid);
        friend.setIslike("0");

        Friend saveFriend = friendDao.save(friend);
        if (saveFriend == null) {
            return 0;
        }
        //4.判断是都是双向好友
        friend = friendDao.findByUseridAndFriendid(friendid, userId);
        if (friend != null) {
            //5.如果是双向好友则更新islike=1,0，单项喜欢，1，互相喜欢
            friendDao.updateIsLike("1", userId, friendid);
            friendDao.updateIsLike("1", friendid, userId);
        }
        return 1;
    }

    /**
     * 添加非好友
     *
     * @param id
     * @param friendid
     */
    public int addNoFriend(String userId, String friendid) {
        //1.验证参数是否为空
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(friendid)) {
            return 0;
        }
        //2.判断是否已经添加非好友
        NoFriend noFriend = noFriendDao.findByUseridAndFriendid(userId, friendid);
        if (noFriend != null) {
            return 0;
        }
        //3.如果没有添加好友则添加好友
        noFriend = new NoFriend();
        noFriend.setUserid(userId);
        noFriend.setFriendid(friendid);
        NoFriend saveNoFriend = noFriendDao.save(noFriend);
        if (saveNoFriend == null) {
            return 0;
        }
        return 1;
    }

    /**
     * 删除好友
     * @param id
     * @param friendid
     */
    public void deleteFriend(String userid,String friendid){
        //从当前登陆用户中删除好友
        friendDao.deleteFriend(userid,friendid);
        //更新friendid到userid为单项喜欢0
        friendDao.updateIsLike("0",userid,friendid);
        //向不喜欢表中添加记录
        addNoFriend(userid,friendid);
    }
}
