package com.isport.utils;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import com.isport.bean.InformationBean;
@Service
public interface MongoInterface extends MongoRepository<InformationBean, String> {
}
