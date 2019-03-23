package com.tensquare.qa.dao;

import com.tensquare.qa.pojo.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentDao extends MongoRepository<Comment,String> {
}
