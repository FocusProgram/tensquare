package com.tensquare.qa.dao;

import com.tensquare.qa.pojo.Pl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlDao extends JpaRepository<Pl,String>, JpaSpecificationExecutor<Pl> {
}
