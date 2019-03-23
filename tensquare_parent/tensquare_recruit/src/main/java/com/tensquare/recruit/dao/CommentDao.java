package com.tensquare.recruit.dao;

import com.tensquare.recruit.pojo.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentDao extends MongoRepository<Comment,String> {

    /**
     * 通过articleId查找评论集合
     * @param articleId
     * @return
     */
    public List<Comment> findByArticleid(String articleId);

}
