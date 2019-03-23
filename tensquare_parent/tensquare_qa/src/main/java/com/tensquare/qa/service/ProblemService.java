package com.tensquare.qa.service;

import java.util.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.qa.client.UserClient;
import com.tensquare.qa.dao.PlDao;
import com.tensquare.qa.dao.ReplyDao;
import com.tensquare.qa.pojo.Comment;
import com.tensquare.qa.pojo.Pl;
import com.tensquare.qa.pojo.Reply;
import com.tensquare.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;



import com.tensquare.qa.dao.ProblemDao;
import com.tensquare.qa.pojo.Problem;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class ProblemService {

	@Autowired
	private ProblemDao problemDao;

	@Autowired
	private PlDao plDao;

	@Autowired
	private ReplyDao replyDao;
	
	@Autowired
	private IdWorker idWorker;

	@Autowired
	private CommentService commentService;

	@Autowired
	private UserClient userClient;

	/**
	 * 最新问答列表
	 * @param labelid
	 * @param page
	 * @param rows
	 * @return
	 */
	public Page<Problem> newList(String labelid, int page,int rows){
		Pageable pageRequest = PageRequest.of(page - 1, rows);
		return problemDao.newList(labelid,pageRequest);
	}

	/**
	 * 热门问答列表
	 * @param labelid
	 * @param pageable
	 * @return
	 */
	public Page<Problem> hostList(String labelid, int page,int rows){
		PageRequest pageRequest = PageRequest.of(page - 1, rows);
		return problemDao.hostList(labelid,pageRequest);
	}

	/**
	 * 等待问答列表
	 * @param labelid
	 * @param page
	 * @param rows
	 * @return
	 */
	public Page<Problem> waitList(String labelid, int page,int rows){
		PageRequest pageRequest = PageRequest.of(page - 1, rows);
		return problemDao.waitList(labelid,pageRequest);
	}

	/**
	 * 最新问答列表
	 * @param labelid
	 * @param page
	 * @param rows
	 * @return
	 */
	public Page<Problem> homeNewList(int page,int rows){
		Pageable pageRequest = PageRequest.of(page - 1, rows);
		return problemDao.homeNewList(pageRequest);
	}

	/**
	 * 热门问答列表
	 * @param labelid
	 * @param pageable
	 * @return
	 */
	public Page<Problem> homeHostList(int page,int rows){
		PageRequest pageRequest = PageRequest.of(page - 1, rows);
		return problemDao.homeHostList(pageRequest);
	}

	/**
	 * 等待问答列表
	 * @param labelid
	 * @param page
	 * @param rows
	 * @return
	 */
	public Page<Problem> homeWaitList(int page,int rows){
		PageRequest pageRequest = PageRequest.of(page - 1, rows);
		return problemDao.homeWaitList(pageRequest);
	}

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Problem> findAll() {
		return problemDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Problem> findSearch(Map whereMap, int page, int size) {
		Specification<Problem> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return problemDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Problem> findSearch(Map whereMap) {
		Specification<Problem> specification = createSpecification(whereMap);
		return problemDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public Problem findById(String id) {
		Problem problem = problemDao.findById(id).get();
        String problemId = problem.getId();
        Pl pl = plDao.findById(problemId).get();
        problem.setLabelId(pl.getLabelid());
        return problem;
	}

	/**
	 * 增加
	 * @param problem
	 */
	public void add(Problem problem) {
		Long promblemId = idWorker.nextId();
		//添加到tb_problem表
		problem.setId( promblemId+"" );
		problem.setCreatetime(new Date());
		problem.setUpdatetime(new Date());
		problem.setVisits(0l);
		problem.setThumbup(0l);
		problem.setReply(0l);
		problem.setSolve("0");
		problem.setReplyname("");
		problemDao.save(problem);
		//添加到tb_pl表
		Pl pl = new Pl();
		pl.setProblemid(promblemId+"");
		pl.setLabelid(problem.getLabelId());
		plDao.save(pl);
	}

	/**
	 * 添加评论
	 * @param reply
	 * @return
	 */
	public void addReply(Reply reply){
		/**
		 * 更新问题状态
		 */
		Problem problem = problemDao.findById(reply.getProblemid()).get();
		problem.setSolve("1");
		problemDao.save(problem);
		reply.setId(idWorker.nextId()+"");
		reply.setCreatetime(new Date());
		reply.setUpdatetime(new Date());
		replyDao.save(reply);
	}

	/**
	 * 修改
	 * @param problem
	 */
	public void update(Problem problem) {
		problemDao.save(problem);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		problemDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<Problem> createSpecification(Map searchMap) {

		return new Specification<Problem>() {

			@Override
			public Predicate toPredicate(Root<Problem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                	predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 标题
                if (searchMap.get("title")!=null && !"".equals(searchMap.get("title"))) {
                	predicateList.add(cb.like(root.get("title").as(String.class), "%"+(String)searchMap.get("title")+"%"));
                }
                // 内容
                if (searchMap.get("content")!=null && !"".equals(searchMap.get("content"))) {
                	predicateList.add(cb.like(root.get("content").as(String.class), "%"+(String)searchMap.get("content")+"%"));
                }
                // 用户ID
                if (searchMap.get("userid")!=null && !"".equals(searchMap.get("userid"))) {
                	predicateList.add(cb.like(root.get("userid").as(String.class), "%"+(String)searchMap.get("userid")+"%"));
                }
                // 昵称
                if (searchMap.get("nickname")!=null && !"".equals(searchMap.get("nickname"))) {
                	predicateList.add(cb.like(root.get("nickname").as(String.class), "%"+(String)searchMap.get("nickname")+"%"));
                }
                // 是否解决
                if (searchMap.get("solve")!=null && !"".equals(searchMap.get("solve"))) {
                	predicateList.add(cb.like(root.get("solve").as(String.class), "%"+(String)searchMap.get("solve")+"%"));
                }
                // 回复人昵称
                if (searchMap.get("replyname")!=null && !"".equals(searchMap.get("replyname"))) {
                	predicateList.add(cb.like(root.get("replyname").as(String.class), "%"+(String)searchMap.get("replyname")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

	/**
	 * 根据问题id查询回复列表
	 * @param problemid
	 * @return
	 */
	public List<Reply> queryReplyList(String problemid) {
		Reply reply = new Reply();
		reply.setProblemid(problemid);
		Example<Reply> replyExample = Example.of(reply);
		List<Reply> replyList = replyDao.findAll(replyExample);
		for (Reply replyInfo : replyList) {
			System.out.println(replyInfo.getUserid());
			//查询用户昵称和头像
			Result result = userClient.findById(replyInfo.getUserid());
			Object data = result.getData();
			LinkedHashMap<String, String> linkhashMap = (LinkedHashMap<String, String>) data;
			System.out.println(result.toString());
			replyInfo.setNickname(linkhashMap.get("nickname"));
			replyInfo.setAvatar(linkhashMap.get("avatar"));
			//根据问题id和评论查询子级评论
			String replyId = replyInfo.getId();
			HashMap<String, String> hashMap = new HashMap<>();
			hashMap.put("problemid",problemid);
			hashMap.put("replyid",replyId);
			List<Comment> commentList = commentService.queryComment(hashMap);
			for (Comment comment : commentList) {
				//查询子级评论的用户昵称和头像
				Result commentResult = userClient.findById(comment.getUserid());
				Object commentResultData = commentResult.getData();
				LinkedHashMap<String, String> commmentLinkHashMap = (LinkedHashMap<String, String>) commentResultData;
				comment.setNickname(commmentLinkHashMap.get("nickname"));
				comment.setAvatar(commmentLinkHashMap.get("avatar"));
			}
			replyInfo.setCommentList(commentList);
		}
		return replyList;
	}

	/**
	 *
	 * @param problemid
	 * @return
	 */
    public List<Problem> queryTop3ProblemList(String problemid) {
		PageRequest pageRequest = PageRequest.of(0, 3);
		Page<Problem> problemPage = problemDao.findAll(pageRequest);
		List<Problem> problemList = problemPage.getContent();
		ArrayList<Problem> problemArrayList = new ArrayList<>(problemList);
		Problem problem = problemDao.findById(problemid).get();
		problemArrayList.remove(problem);
		for (Problem problems : problemArrayList) {
			Reply reply = new Reply();
			reply.setProblemid(problems.getId());
			Example<Reply> replyExample = Example.of(reply);
			List<Reply> replyList = replyDao.findAll(replyExample);
			problems.setReplyCount(replyList.size());
		}
		return problemArrayList;
	}

	/**
	 * 通过用户id查询问题列表
	 * @param userId
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Problem> queryProblemByUserId(String userId, int page, int size) {
		Problem problem = new Problem();
		problem.setUserid(userId);
		Example<Problem> problemExample = Example.of(problem);
		PageRequest pageRequest = PageRequest.of(page - 1, size);
		Page<Problem> problemPage = problemDao.findAll(problemExample, pageRequest);
		return problemPage;
	}
}
