package com.tensquare.qa.controller;

import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.qa.pojo.Comment;
import com.tensquare.qa.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/problem")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论
     * @param comment
     * @return
     */
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Result addComment(@RequestBody Comment comment){
        commentService.addComment(comment);
        return  new Result(true, StatusCode.OK,"添加成功");
    }

    /**
     * 查询评论列表
     */
    @RequestMapping(value = "/comment/commentlist",method = RequestMethod.POST)
    public Result queryComment(@RequestBody Map<String,String> searchMap){
        return new Result(true,StatusCode.OK,"查询成功",commentService.queryComment(searchMap));
    }

    /**
     * 根据用户id查询回答评论列表
     */
    @RequestMapping(value = "/comment/{userid}/{page}/{size}",method = RequestMethod.GET)
    public Result queryCommentById(@PathVariable String userid,@PathVariable int page,@PathVariable int size){
        Page<Comment> commentPage = commentService.queryCommentById(userid,page,size);
        return new Result(true,StatusCode.OK,"查询成功",new PageResult<>(commentPage.getTotalElements(),commentPage.getContent()));
    }

}
