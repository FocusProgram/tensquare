package com.tensquare.qa.service;

import com.tensquare.entity.Result;
import com.tensquare.qa.client.UserClient;
import com.tensquare.qa.dao.CommentDao;
import com.tensquare.qa.pojo.Comment;
import com.tensquare.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private UserClient userClient;

    /**
     * 添加评论
     *
     * @param comment
     */
    public void addComment(Comment comment) {
        comment.set_id(idWorker.nextId()+"");
        if (comment.getReplyid()==null){
            comment.setReplyid("");
        }
        comment.setPublishtime(new Date());
        commentDao.save(comment);
    }

    /**
     * 查询评论列表
     *
     * @param parentid
     * @param replyid
     * @return
     */
    public List<Comment> queryComment(Map<String,String> searchMap) {
        Comment comment = new Comment();
        comment.setParentid(searchMap.get("problemid"));
        comment.setReplyid(searchMap.get("replyid"));
        Example<Comment> commentExample = Example.of(comment);
        List<Comment> commentList = commentDao.findAll(commentExample);
        for (Comment commentInfo : commentList) {
            String userid = commentInfo.getUserid();
            Result result = userClient.findById(userid);
            Object data = result.getData();
            LinkedHashMap<String, String> hashMap = (LinkedHashMap<String, String>) data;
            commentInfo.setNickname(hashMap.get("nickname"));
            commentInfo.setAvatar(hashMap.get("avatar"));
        }
        return commentList;
    }

    /**
     * 根据用户id查询回答评论列表
     * @param userid
     * @param page
     * @param size
     * @return
     */
    public Page<Comment> queryCommentById(String userid, int page, int size) {
        Comment comment = new Comment();
        comment.setUserid(userid);
        Example<Comment> replyExample = Example.of(comment);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Comment> replyPage = commentDao.findAll(replyExample, pageRequest);
        return replyPage;
    }
}
