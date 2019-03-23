package com.tensquare.base.controller;

import com.tensquare.base.pojo.Label;
import com.tensquare.base.service.LabelService;
import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import jdk.management.resource.internal.inst.SocketOutputStreamRMHooks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping("/label")
@RefreshScope
public class LabelController {

    @Autowired
    private LabelService labelService;

    @Value("${ip}")
    private String ip;

    /**
     * 查询所有信息
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll(){
        System.out.println("测试SpringCloudBus更新配置文件自动更新ip:"+ip);
        return new Result(true,StatusCode.OK,"查询成功",labelService.findAll());
    }

    /**
     * 通过Id查询信息
     * @param labelId
     * @return
     */
    @RequestMapping(value = "/{labelId}",method = RequestMethod.GET)
    public Result findById(@PathVariable("labelId") String labelId){
        return new Result(true,StatusCode.OK,"查询成功",labelService.findById(labelId));
    }

    /**
     * 保存信息
     * @param label
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Label label){
        labelService.add(label);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /**
     * 删除信息
     * @param label
     * @return
     */
    @RequestMapping(value = "/{labelId}",method = RequestMethod.DELETE)
    public Result delById(@PathVariable("labelId")String labelId){
        labelService.deleteById(labelId);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 修改信息
     * @param label
     * @return
     */
    @RequestMapping(value = "/{labelId}",method = RequestMethod.PUT)
    public Result update(@PathVariable("labelId")String labelId,@RequestBody Label label){
        labelService.update(label);
        return new Result(true,StatusCode.OK,"更新成功");
    }

    /**
     * 条件查询
     * @return
     */
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",labelService.findSearch(searchMap));
    }

    /**
     * 条件分组查询
     * @return
     */
    @RequestMapping(value = "/search/{page}/{size}",method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap,@PathVariable("page") int page,@PathVariable("size") int size){
        Page<Label> search = labelService.findSearch(searchMap, page, size);
        return new Result(true,StatusCode.OK,"查询成功",new PageResult<Label>(search.getTotalElements(),search.getContent()));
    }

}
