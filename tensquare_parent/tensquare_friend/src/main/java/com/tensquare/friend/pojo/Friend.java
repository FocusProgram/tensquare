package com.tensquare.friend.pojo;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="tb_friend")
@IdClass(Friend.class)
public class Friend implements Serializable {

    @Id
    private String userid; //用户id

    @Id
    private String friendid; //好友id

    private String islike;//0,单项喜欢，1，双向喜欢

    public Friend() {
    }

    public Friend(String userid, String friendid, String islike) {
        this.userid = userid;
        this.friendid = friendid;
        this.islike = islike;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFriendid() {
        return friendid;
    }

    public void setFriendid(String friendid) {
        this.friendid = friendid;
    }

    public String getIslike() {
        return islike;
    }

    public void setIslike(String islike) {
        this.islike = islike;
    }
}
