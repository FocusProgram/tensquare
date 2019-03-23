package com.tensquare.article.dao;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tensquare.article.pojo.Article;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface ArticleDao extends JpaRepository<Article,String>,JpaSpecificationExecutor<Article>{

    /**
     * 审核
     * @param id
     */
    @Modifying
    @Query(value = "UPDATE tb_article SET state=1 WHERE id=?1",nativeQuery = true)
    public void updateState(String id);

    /**
     * 点赞
     */
    @Modifying
    @Query(value = "UPDATE Article a SET thumbup=thumbup+1 WHERE id=?1",nativeQuery = true)
    public void updateThumbup(String id);

    /**
     * 评论
     */
    @Modifying
    @Query(value = "UPDATE tb_article SET comment=comment+1 WHERE id=?1",nativeQuery = true)
    public void updateComment(String id);



}
