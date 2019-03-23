package com.tensquare.spit.controller;

import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.spit.pojo.Spit;
import com.tensquare.spit.service.SpitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.ConfigFile;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/spit")
public class SpitController {


    @Autowired
    private SpitService spitService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value="/search/{page}/{size}",method=RequestMethod.POST)
    public Result findSearch(@PathVariable int page, @PathVariable int size){
        Page<Spit> pageList = spitService.findSearch(page, size);
        return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<Spit>(pageList.getTotalElements(), pageList.getContent()) );
    }

    /**
     * 查询所有
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll(){
        return new Result(true, StatusCode.OK,"查询成功",spitService.findAll());
    }

    /**
     *  通过Id查询信息
     * @param spitId
     * @return
     */
    @RequestMapping(value = "/spit/{spitId}",method = RequestMethod.GET)
    public Result findById(@PathVariable String spitId){
        return new Result(true, StatusCode.OK,"查询成功",spitService.findById(spitId));
    }

    /**
     * 添加吐槽
     * @param spit
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Spit spit){
        spitService.save(spit);
        return new Result(true, StatusCode.OK,"查询成功");
    }

    @RequestMapping(value = "/{spitId}",method = RequestMethod.PUT)
    public Result update(@PathVariable String spitId,@RequestBody Spit spit){
        spit.set_id(spitId);
        spitService.update(spit);
        return new Result(true, StatusCode.OK,"修改成功");
    }

    @RequestMapping(value = "/{spitId}",method = RequestMethod.DELETE)
    public Result delete(@PathVariable String spitId){
        spitService.deleteById(spitId);
        return new Result(true, StatusCode.OK,"删除成功");
    }

    @RequestMapping(value = "/comment/{parentid}/{page}/{size}",method = RequestMethod.GET)
    public Result findByParentId(@PathVariable String parentid,@PathVariable int page,@PathVariable int size){
        Page<Spit> spitPage = spitService.findbyParentId(parentid, page, size);
        return new Result(true, StatusCode.OK,"查询成功",new PageResult<Spit>(spitPage.getTotalElements(),spitPage.getContent()));
    }

    /**
     * 点赞
     * @param id
     * @return
     */
    @RequestMapping(value="/thumbup/{spitId}",method=RequestMethod.PUT)
    public Result updateThumbup(@PathVariable String spitId){
        //判断当前用户是否已经点赞，暂时人工赋值userid
        String userid = "111";
        //判断当前用户是都已经点赞
        if (redisTemplate.opsForValue().get("thumbup_"+userid)!=null){
          return new Result(false,StatusCode.REPERROR,"不能重复点赞");
        }
        redisTemplate.opsForValue().set("thumbup_"+userid,1);
        spitService.updateThumbup(spitId);
        return new Result(true,StatusCode.OK,"点赞成功");
    }

    /**
     * 通过Id查询评论列表
     * @param spitId
     * @return
     */
    @RequestMapping(value = "/commentlist/{spitId}",method = RequestMethod.GET)
    public Result commentlistById(@PathVariable String spitId){
        return new Result(true, StatusCode.OK,"查询成功",spitService.commentlistById(spitId));
    }


}
