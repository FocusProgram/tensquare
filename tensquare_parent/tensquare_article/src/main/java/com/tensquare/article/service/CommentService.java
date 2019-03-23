package com.tensquare.article.service;

import com.tensquare.article.client.UserClient;
import com.tensquare.article.dao.ArticleDao;
import com.tensquare.article.dao.CommentDao;
import com.tensquare.article.pojo.Comment;
import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.sound.midi.Soundbank;
import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ArticleDao articleDao;



    /**
     * 添加文章评论
     */
    public void addComment(Comment comment) {
        System.out.println("parentid"+comment.getParentid());
        if (comment.getParentid().equals("0")){
            System.out.println("进入");
            articleDao.updateComment(comment.getArticleid());
        }
        comment.set_id(idWorker.nextId()+"");
        comment.setPublishdate(new Date());
        commentDao.save(comment);
    }

    /**
     * 根据文章id查询评论
     */
    public Page<Comment> queryComment(String articleid,int page,int size) {
        Comment comment = new Comment();
        comment.setArticleid(articleid);
        comment.setParentid("0");
        Example<Comment> commentExample = Example.of(comment);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Comment> commentPage = commentDao.findAll(commentExample, pageRequest);
        List<Comment> commentList = commentPage.getContent();
        System.out.println("文章评论"+commentList);
        for (Comment articleComment : commentList) {
            // 添加昵称和头像
            String userid = articleComment.getUserid();
            Result userResult = userClient.findById(userid);
            Object data = userResult.getData();
            LinkedHashMap hashMap = (LinkedHashMap<String,String>) data;
            articleComment.setNickname(hashMap.get("nickname").toString());
            articleComment.setAvatar(hashMap.get("avatar").toString());
            // 查询文章子级评论以及昵称和头像
            String parentid = articleComment.get_id();
            List<Comment> commentlistByParentid = queryCommentlistByParentid(articleid, parentid);
            articleComment.setCommentList(commentlistByParentid);
            for (Comment childrenComment : commentlistByParentid) {
                Result childrenrResult = userClient.findById(childrenComment.getUserid());
                Object childrenData = childrenrResult.getData();
                LinkedHashMap<String, String> childrenHashMap = (LinkedHashMap<String, String>) childrenData;
                childrenComment.setNickname(childrenHashMap.get("nickname").toString());
                childrenComment.setAvatar(childrenHashMap.get("avatar").toString());
            }

        }
        return commentPage;
    }


    /**
     * 根据评论id查询子级评论
     * @param articleid
     * @param parentid
     * @param page
     * @param size
     * @return
     */
    public Page<Comment> queryCommentByParentid(String articleid, String parentid, int page, int size) {
        Comment comment = new Comment();
        comment.setArticleid(articleid);
        comment.setParentid(parentid);
        Example<Comment> commentExample = Example.of(comment);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Comment> commentPage = commentDao.findAll(commentExample, pageRequest);
        return commentPage;
    }

    public List<Comment> queryCommentlistByParentid(String articleid, String parentid) {
        Comment comment = new Comment();
        comment.setArticleid(articleid);
        comment.setParentid(parentid);
        Example<Comment> commentExample = Example.of(comment);
        List<Comment> commentList = commentDao.findAll(commentExample);
        return commentList;
    }
}
