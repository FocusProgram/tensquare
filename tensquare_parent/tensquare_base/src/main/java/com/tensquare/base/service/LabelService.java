package com.tensquare.base.service;

import com.tensquare.base.dao.LabelDao;
import com.tensquare.base.pojo.Label;
import com.tensquare.util.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 标签业务逻辑类
 */
@Service
public class LabelService {

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private IdWorker idWorker;
    /**
     * 查询全部标签
     * @return
     */
    public List<Label> findAll(){
        return labelDao.findAll();
    }

    /**
     * 根据ID查询标签
     * @return
     */
    public Label findById(String id){
        try {
            return labelDao.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 增加标签
     * @param label
     */
    public void add(Label label){
        label.setId( idWorker.nextId()+"" );//设置ID
        labelDao.save(label);
    }
    /**
     * 修改标签
     * @param label
     */
    public void update(Label label){
        labelDao.save(label);
    }
    /**
     * 删除标签
     * @param id
     */
    public void deleteById(String id){
        labelDao.deleteById(id);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Specification<Label> createSpecification(Map searchMap){
        return new Specification<Label>() {
            /**
             * @param root 根节点,查询条件封装到某个对象中，例如label.getId();
             * @param criteriaQuery 查询条件，封装查询关键字，例如group by,order by
             * @param cb 用来封装条件对象，如果直接返回null,则表示不需要任何条件
             * @return
             */
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?>
                    criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicateList=new ArrayList<>();
                if(!StringUtils.isEmpty((String)searchMap.get("labelname"))){
                    predicateList.add(cb.like(
                            root.get("labelname").as(String.class), "%"+
                                    (String)searchMap.get("labelname")+"%" ) );
                }
                if(!StringUtils.isEmpty((String)searchMap.get("state"))){
                    predicateList.add(cb.equal(
                            root.get("state").as(String.class), (String)searchMap.get("state") ) );
                }
                if(!StringUtils.isEmpty((String)searchMap.get("recommend"))){
                    predicateList.add(cb.equal(
                            root.get("recommend").as(String.class),
                            (String)searchMap.get("recommend") ) );
                }
                return cb.and( predicateList.toArray( new
                        Predicate[predicateList.size()]) );
            }
        };
    }

    /**
     *
     * 条件查询
     * @return
     */
    public List<Label> findSearch(Map searchMap){
        Specification<Label> specification = createSpecification(searchMap);
        return labelDao.findAll(specification);

    }

    /**
     * 分页条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public Page<Label> findSearch(Map searchMap, int page, int size){
        Specification specification= createSpecification(searchMap);
        PageRequest pageRequest= PageRequest.of(page-1,size);
        return labelDao.findAll( specification ,pageRequest);
    }
}
