package com.tensquare.article.controller;


import com.tensquare.article.pojo.Comment;
import com.tensquare.article.service.CommentService;
import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加文章评论
     * @param comment
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result addComment(@RequestBody Comment comment){
        commentService.addComment(comment);
        return new Result(true, StatusCode.OK,"评论成功");
    }

    /**
     * 根据文章id查询评论
     * @param articleid
     * @return
     */
    @RequestMapping(value = "/article/{articleid}/{page}/{size}",method = RequestMethod.GET)
    public Result queryComment(@PathVariable String articleid,@PathVariable int page,@PathVariable int size){
        Page<Comment> commentPage = commentService.queryComment(articleid, page, size);
        return new Result(true,StatusCode.OK,"查询成功",new PageResult<>(commentPage.getTotalElements(),commentPage.getContent()));
    }

    /**
     * 根据评论id查询子级评论
     */
    @RequestMapping(value = "/article/{articleid}/{parentid}/{page}/{size}")
    public Result queryCommentByParentid(@PathVariable String articleid,@PathVariable String parentid,@PathVariable int page,@PathVariable int size){
        Page<Comment> commentPage = commentService.queryCommentByParentid(articleid,parentid ,page, size);
        return new Result(true,StatusCode.OK,"查询成功",new PageResult<>(commentPage.getTotalElements(),commentPage.getContent()));
    }

}
