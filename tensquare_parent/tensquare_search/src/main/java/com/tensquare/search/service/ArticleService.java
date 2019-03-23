package com.tensquare.search.service;

import com.tensquare.search.dao.ArticleDao;
import com.tensquare.search.pojo.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    @Autowired
    private ArticleDao articleDao;

    /**
     * 增加文章
     * @param article
     */
    public void save(Article article){
        articleDao.save(article);
    }

    /**
     * 搜索查询
     * @param title
     * @param content
     * @param page
     * @param size
     * @return
     */
    public Page<Article> findByTitleOrContentLike(String keywords, int page,int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return articleDao.findByTitleOrContentLike(keywords,keywords,pageRequest);
    }

}
