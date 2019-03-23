package com.tensquare.qa.controller;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.qa.client.LabelClient;
import com.tensquare.qa.dao.ReplyDao;
import com.tensquare.qa.pojo.Reply;
import com.tensquare.util.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tensquare.qa.pojo.Problem;
import com.tensquare.qa.service.ProblemService;

import javax.servlet.http.HttpServletRequest;

/**
 * 控制器层
 * @author Administrator
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/problem")
public class ProblemController {

	@Autowired
	private ProblemService problemService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private ReplyDao replyDao;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private LabelClient labelClient;

	/**
	 * Fegin调用tensquare_base
	 */
	@RequestMapping(value = "/label/{labelid}",method = RequestMethod.GET)
	public Result findLabelById(@PathVariable("labelid") String labelid){
		Result result = labelClient.findById(labelid);
		return result;
	}

	/**
	 * Fegin调用tensquare_base
	 */
	@RequestMapping(value = "/label",method = RequestMethod.GET)
	public Result topList(){
		return  labelClient.findAll();
	}

	/**
	 * 最新问答列表
	 * @param labelid
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(value = "/newlist/{label}/{page}/{size}",method = RequestMethod.GET)
	public Result newList(@PathVariable String label,@PathVariable int page,@PathVariable int size){
		Page<Problem> problemPage = null;
		//判断手否是首页
		if (label.equals("0")){
			problemPage = problemService.homeNewList(page,size);
		}else{
			problemPage = problemService.newList(label, page, size);
		}

		return new Result(true,StatusCode.OK,"查询成功",new PageResult<>(problemPage.getTotalElements(),problemPage.getContent()));
	}

	/**
	 * 热门问答列表
	 * @param labelid
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(value = "/hotlist/{label}/{page}/{size}",method = RequestMethod.GET)
	public Result hostList(@PathVariable String label,@PathVariable int page,@PathVariable int size){
		Page<Problem> problemPage = null;
		//判断手否是首页
		if (label.equals("0")){
			problemPage = problemService.homeHostList(page,size);
		}else{
			problemPage = problemService.hostList(label, page, size);
		}

		return new Result(true,StatusCode.OK,"查询成功",new PageResult<>(problemPage.getTotalElements(),problemPage.getContent()));
	}

	/**
	 * 等待问答列表
	 * @param labelid
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(value = "/waitlist/{label}/{page}/{size}",method = RequestMethod.GET)
	public Result waitList(@PathVariable String label,@PathVariable int page,@PathVariable int size){
		Page<Problem> problemPage = null;
		//判断手否是首页
		if (label.equals("0")){
			problemPage = problemService.homeWaitList(page, size);
		}else{
			problemPage = problemService.waitList(label, page, size);
		}

		return new Result(true,StatusCode.OK,"查询成功",new PageResult<>(problemPage.getTotalElements(),problemPage.getContent()));
	}
	
	
	/**
	 * 查询全部数据
	 * @return
	 */
	@RequestMapping(method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true,StatusCode.OK,"查询成功",problemService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 * @return
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.GET)
	public Result findById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",problemService.findById(id));
	}


	/**
	 * 分页+多条件查询
	 * @param searchMap 查询条件封装
	 * @param page 页码
	 * @param size 页大小
	 * @return 分页结果
	 */
	@RequestMapping(value="/search/{page}/{size}",method=RequestMethod.POST)
	public Result findSearch(@RequestBody Map searchMap , @PathVariable int page, @PathVariable int size){
		Page<Problem> pageList = problemService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMapfindById
     * @return
     */
    @RequestMapping(value="/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",problemService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param problem
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Result add(@RequestBody Problem problem  ){
//		String token = (String) httpServletRequest.getAttribute("clamis_user");
//		System.out.println("结果为"+token);
//		if (StringUtils.isEmpty(token)){
//			return new Result(false,StatusCode.ACCESSERROR,"权限不足");
//		}
		problemService.add(problem);
		return new Result(true,StatusCode.OK,"增加成功");
	}

	/**
	 * 添加评论
	 * @param reply
	 * @return
	 */
	@RequestMapping(value = "/reply",method = RequestMethod.POST)
	public Result addReply(@RequestBody Reply reply){
		problemService.addReply(reply);
		return new Result(true,StatusCode.OK,"增加评论成功");
	}

	/**
	 * 根据问题id查询回复列表
	 */
	@RequestMapping(value = "/reply/{problemid}",method = RequestMethod.GET)
	public Result queryReplyList(@PathVariable("problemid") String problemid){
		return new Result(true,StatusCode.OK,"查询成功",problemService.queryReplyList(problemid) );
	}

	@RequestMapping(value = "/top3/{problemid}",method = RequestMethod.GET)
	public Result queryTop3ProblemList(@PathVariable("problemid") String problemid){
		return new Result(true,StatusCode.OK,"查询成功",problemService.queryTop3ProblemList(problemid) );
	}
	
	/**
	 * 修改
	 * @param problem
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.PUT)
	public Result update(@RequestBody Problem problem, @PathVariable String id ){
		problem.setId(id);
		problemService.update(problem);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param id
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String id ){
		problemService.deleteById(id);
		return new Result(true,StatusCode.OK,"删除成功");
	}

	/**
	 * 通过用户id查询问题列表
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/{userId}/{page}/{size}",method = RequestMethod.GET)
	public Result queryProblemByUserId(@PathVariable String userId,@PathVariable int page ,@PathVariable int size){
		Page<Problem> problemPage = problemService.queryProblemByUserId(userId,page,size);
		return new Result(true,StatusCode.OK,"查询成功",new PageResult<>(problemPage.getTotalElements(),problemPage.getContent()));
	}
	
}
