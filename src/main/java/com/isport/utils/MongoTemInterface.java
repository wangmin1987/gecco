package com.isport.utils;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import com.isport.bean.NewsInfoBean;
@Service
public interface MongoTemInterface extends MongoRepository<NewsInfoBean, String> {
//	 Page<NewsInfoBean> find(Criteria criteria, Pageable pageable);
}
