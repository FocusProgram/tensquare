package com.tensquare.recruit.controller;

import com.sun.org.apache.xml.internal.resolver.readers.TR9401CatalogReader;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.recruit.pojo.Comment;
import com.tensquare.recruit.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
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
        return new Result(true, StatusCode.OK,"添加文章评论成功");
    }

    /**
     * 通过articleId查找评论集合
     * @param articleId
     * @return
     */
    @RequestMapping(value="/article/{articleid}",method = RequestMethod.GET)
    public Result findByArticleid(@PathVariable String articleid){
        return new Result(true,StatusCode.OK,"查询成功",commentService.findByArticleid(articleid));
    }

}
