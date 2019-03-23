package com.tensquare.recruit.service;

import com.tensquare.recruit.dao.CommentDao;
import com.tensquare.recruit.pojo.Comment;
import com.tensquare.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 添加文章评论
     * @param comment
     */
    public void addComment(Comment comment){
        comment.set_id(idWorker.nextId()+"");
        commentDao.save(comment);
    }

    /**
     * 通过articleId查找评论集合
     * @param articleId
     * @return
     */
    public List<Comment> findByArticleid(String articleId){
        return commentDao.findByArticleid(articleId);
    }

}
