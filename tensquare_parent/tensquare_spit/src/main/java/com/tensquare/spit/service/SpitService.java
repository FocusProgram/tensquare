package com.tensquare.spit.service;

import com.tensquare.spit.dao.SpitDao;
import com.tensquare.spit.pojo.Spit;
import com.tensquare.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SpitService {

    @Autowired
    private SpitDao spitDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Spit> findAll() {
        return spitDao.findAll();
    }

    public Spit findById(String id) {
        return spitDao.findById(id).get();
    }

    public void save(Spit spit) {
        spit.set_id(idWorker.nextId() + "");
        spit.setPublishtime(new Date());//发布日期
        spit.setVisits(0);//浏览量
        spit.setShare(0);//分享数
        spit.setThumbup(0);//点赞数
        spit.setComment(0);//回复数
        spit.setState("1");//状态
        if (spit.getParentid()==null){
            spit.setParentid("");//父类id
        }
        //如果当前添加的吐槽存在父节点，那么父节点的吐槽回复数要加一
        if (spit.getParentid() != null && !"".equals(spit.getParentid())) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(spit.getParentid()));
            Update update = new Update();
            update.inc("comment", 1);
            mongoTemplate.updateFirst(query, update, "spit");
        }
        spitDao.save(spit);
    }

    public void update(Spit spit) {
        spitDao.save(spit);
    }

    public void deleteById(String id) {
        spitDao.deleteById(id);
    }

    public Page<Spit> findbyParentId(String parentId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return spitDao.findByParentid(parentId, pageable);
    }

    /**
     * 点赞
     *
     * @param spitId
     */
    public void updateThumbup(String spitId) {
        //方式一效率有问题
//        Spit spit = spitDao.findById(spitId).get();
//        spit.setThumbup((spit.getThumbup()==null?0:spit.getThumbup())+1);
//        spitDao.save(spit);

        //方式二：原生mongo自增
        //制定条件
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(spitId));
        //更新数据
        Update update = new Update();
        update.inc("thumbup", 1);
        mongoTemplate.updateFirst(query, update, "spit");
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    public Page<Spit> findSearch(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Spit spit = new Spit();
        spit.setParentid("");
        Example<Spit> spitExample = Example.of(spit);
        Page<Spit> spitPage  = spitDao.findAll(spitExample,pageRequest);
        return spitPage;
    }

    /**
     * 通过Id查询评论列表
     * @param spitId
     * @return
     */
    public List<Spit> commentlistById(String spitId) {
        return  null;
    }
}
