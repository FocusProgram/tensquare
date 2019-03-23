package com.tensquare.article.controller;
import java.util.List;
import java.util.Map;

import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tensquare.article.pojo.Article;
import com.tensquare.article.service.ArticleService;

/**
 * 控制器层
 * @author Administrator
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/article")
public class ArticleController {

	@Autowired
	private ArticleService articleService;

	/**
	 * 审核
	 * @param articleId
	 * @return
	 */
	@RequestMapping(value = "/examine/{articleId}",method = RequestMethod.GET)
	public Result updateState(@PathVariable String articleId){
		articleService.updateState(articleId);
		return new Result(true,StatusCode.OK,"审核成功");

	}

	/**
	 * 点赞
	 * @param articleId
	 * @return
	 */
	@RequestMapping(value = "/thumbup/{articleId}",method = RequestMethod.GET)
	public Result updateThumbup(@PathVariable String articleId){
		articleService.updateThumbup(articleId);
		return new Result(true,StatusCode.OK,"点赞成功");
	}
	
	/**
	 * 查询全部数据
	 * @return
	 */
	@RequestMapping(method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true, StatusCode.OK,"查询成功",articleService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 * @return
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.GET)
	public Result findById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",articleService.findById(id));
	}

	/**
	 * 根据用户id查询
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/userid/{userid}/{page}/{size}",method= RequestMethod.GET)
	public Result findByUserId(@PathVariable String userid,@PathVariable int page,@PathVariable int size){
		Page<Article> articlePage = articleService.findByUserId(userid, page, size);
		return new Result(true,StatusCode.OK,"查询成功",new PageResult<>(articlePage.getTotalElements(),articlePage.getContent()));
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
		Page<Article> pageList = articleService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMap
     * @return
     */
    @RequestMapping(value="/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",articleService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param article
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Result add(@RequestBody Article article  ){
		articleService.add(article);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param article
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.PUT)
	public Result update(@RequestBody Article article, @PathVariable String id ){
		article.setId(id);
		articleService.update(article);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param id
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String id ){
		articleService.deleteById(id);
		return new Result(true,StatusCode.OK,"删除成功");
	}

	/**
	 * 根据频道分类分页查询
	 * @param article
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(value = "/find/{page}/{size}",method = RequestMethod.POST)
	public Result queryPage(@RequestBody Article article,@PathVariable int page,@PathVariable int size){
		Page<Article> articlePageResult = articleService.queryPage(article,page,size);
		return new Result(true,StatusCode.OK,"查询成功",new PageResult<>(articlePageResult.getTotalElements(),articlePageResult.getContent()));
	}
	
}
