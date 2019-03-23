package com.tensquare.article.service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.tensquare.util.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tensquare.article.dao.ArticleDao;
import com.tensquare.article.pojo.Article;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class ArticleService {

	@Autowired
	private ArticleDao articleDao;
	
	@Autowired
	private IdWorker idWorker;

	@Autowired
	RedisTemplate redisTemplate;

	/**
	 * 审核
	 * @param id
	 */
	public void updateState(String id){
		articleDao.updateState(id);
	}

	/**
	 * 点赞
	 * @param id
	 */
	public void updateThumbup(String id){
		articleDao.updateThumbup(id);
	}

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Article> findAll() {
		return articleDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Article> findSearch(Map whereMap, int page, int size) {
		Specification<Article> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return articleDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Article> findSearch(Map whereMap) {
		Specification<Article> specification = createSpecification(whereMap);
		return articleDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public Article findById(String id) {
		//1.先从缓存中查询当前对象
		Article article = (Article) redisTemplate.opsForValue().get("article_" + id);
		//2.如果没有从缓存中查询到对象
		if (article==null){
			//3.从数据库中查询
			Optional<Article> optional = articleDao.findById(id);
			if (optional!=null){
				 article = optional.get();
			}
			//4.存入到缓存中
			redisTemplate.opsForValue().set("article_"+id,article,10, TimeUnit.SECONDS);
		}
		return article;
	}

	/**
	 * 增加
	 * @param article
	 */
	public void add(Article article) {
		article.setId( idWorker.nextId()+"" );
		article.setColumnid(""); //专栏id
//		article.setImage(""); //文章封面
		article.setCreatetime(new Date()); //发表日期
		article.setUpdatetime(new Date()); //修改日期
		article.setIspublic("1"); //是否公开
		article.setIstop("0"); //是否置顶
		article.setVisits(0); //浏览量
		article.setThumbup(0); //点赞数
		article.setComment(0); //评论数
		article.setState("1"); //审核状态
		article.setUrl(""); //URL地址
		article.setType("0"); // 文章类型
		articleDao.save(article);
	}

	/**
	 * 修改
	 * @param article
	 */
	public void update(Article article) {
		//清除缓存
		redisTemplate.delete("article_" + article);
		articleDao.save(article);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		//清除缓存
		redisTemplate.delete("article_" + id);
		articleDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<Article> createSpecification(Map searchMap) {

		return new Specification<Article>() {

			@Override
			public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                	predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 专栏ID
                if (searchMap.get("columnid")!=null && !"".equals(searchMap.get("columnid"))) {
                	predicateList.add(cb.like(root.get("columnid").as(String.class), "%"+(String)searchMap.get("columnid")+"%"));
                }
                // 用户ID
                if (searchMap.get("userid")!=null && !"".equals(searchMap.get("userid"))) {
                	predicateList.add(cb.like(root.get("userid").as(String.class), "%"+(String)searchMap.get("userid")+"%"));
                }
                // 标题
                if (searchMap.get("title")!=null && !"".equals(searchMap.get("title"))) {
                	predicateList.add(cb.like(root.get("title").as(String.class), "%"+(String)searchMap.get("title")+"%"));
                }
                // 文章正文
                if (searchMap.get("content")!=null && !"".equals(searchMap.get("content"))) {
                	predicateList.add(cb.like(root.get("content").as(String.class), "%"+(String)searchMap.get("content")+"%"));
                }
                // 文章封面
                if (searchMap.get("image")!=null && !"".equals(searchMap.get("image"))) {
                	predicateList.add(cb.like(root.get("image").as(String.class), "%"+(String)searchMap.get("image")+"%"));
                }
                // 是否公开
                if (searchMap.get("ispublic")!=null && !"".equals(searchMap.get("ispublic"))) {
                	predicateList.add(cb.like(root.get("ispublic").as(String.class), "%"+(String)searchMap.get("ispublic")+"%"));
                }
                // 是否置顶
                if (searchMap.get("istop")!=null && !"".equals(searchMap.get("istop"))) {
                	predicateList.add(cb.like(root.get("istop").as(String.class), "%"+(String)searchMap.get("istop")+"%"));
                }
                // 审核状态
                if (searchMap.get("state")!=null && !"".equals(searchMap.get("state"))) {
                	predicateList.add(cb.like(root.get("state").as(String.class), "%"+(String)searchMap.get("state")+"%"));
                }
                // 所属频道
                if (searchMap.get("channelid")!=null && !"".equals(searchMap.get("channelid"))) {
                	predicateList.add(cb.like(root.get("channelid").as(String.class), "%"+(String)searchMap.get("channelid")+"%"));
                }
                // URL
                if (searchMap.get("url")!=null && !"".equals(searchMap.get("url"))) {
                	predicateList.add(cb.like(root.get("url").as(String.class), "%"+(String)searchMap.get("url")+"%"));
                }
                // 类型
                if (searchMap.get("type")!=null && !"".equals(searchMap.get("type"))) {
                	predicateList.add(cb.like(root.get("type").as(String.class), "%"+(String)searchMap.get("type")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

	/**
	 * 根据频道分类分页查询
	 * @param article
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Article> queryPage(Article article, int page, int size) {
		Example<Article> articleExample = null;
		Pageable pageable = null;
		//判断热门
		if(article.getChannelid().equals("0")){
			Article hotArticle = new Article();
			hotArticle.setIspublic(article.getIspublic());
			hotArticle.setState(article.getState());
			articleExample = Example.of(hotArticle);
			Sort sort = new Sort(Sort.Direction.DESC,"comment");
			pageable = PageRequest.of(page-1,size,sort);
		}else{
			articleExample = Example.of(article);
			pageable = PageRequest.of(page-1, size);
		}
		Page<Article> articlePage = articleDao.findAll(articleExample, pageable);
		return articlePage;
	}

	/**
	 * 根据用户id查询
	 * @param userid
	 * @return
	 */
	public Page<Article> findByUserId(String userid,int page,int size) {
		PageRequest pageRequest = PageRequest.of(page - 1, size);
		Article article = new Article();
		article.setUserid(userid);
		Example<Article> articleExample = Example.of(article);
		Page<Article> articlePage = articleDao.findAll(articleExample, pageRequest);
		return articlePage;
	}
}
