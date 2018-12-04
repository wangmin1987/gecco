package com.isport.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.isport.bean.NewsInfoBean;

@Service
public class MongoTemUtil {
	@Autowired
	MongoTemInterface mti;
	@Autowired
	private MongoTemplate mongoTemplate;

	public void update(NewsInfoBean demoEntity) {
		Query query = new Query(Criteria.where("id").is(demoEntity.getId()));
		Update update = new Update();
		update.set("source", demoEntity.getSource());
		update.set("source_icon", demoEntity.getSource_icon());
		update.set("title", demoEntity.getTitle());
//		update.set("title_img", demoEntity.getTitle_img());
		update.set("summary", demoEntity.getSummary());
		update.set("index_url", demoEntity.getIndex_url());
		update.set("url", demoEntity.getUrl());
		update.set("pubDate", demoEntity.getPub_date());
//		update.set("block", demoEntity.getBlock());
		update.set("author", demoEntity.getAuthor());
		update.set("author_img", demoEntity.getAuthor_img());
		update.set("content", demoEntity.getContent());
		update.set("key_word", demoEntity.getKey_word());
//		update.set("comment_num", demoEntity.getComment_num());
		update.set("remark", demoEntity.getRemark());
		mongoTemplate.updateFirst(query, update, NewsInfoBean.class);
	}

	public NewsInfoBean findById(int id) {
		Query query = new Query(Criteria.where("id").is(id));
		NewsInfoBean demoEntity = mongoTemplate.findOne(query, NewsInfoBean.class);
		return demoEntity;
	}

	public List<NewsInfoBean> findAll() {
		List<NewsInfoBean> findAll = mti.findAll();
		return findAll;
	}

	public void save(NewsInfoBean demoEntity) {
		mti.insert(demoEntity);
	}

	public void saveList(List<NewsInfoBean> demoEntity) {
		mti.insert(demoEntity);
	}

	public void removeById(Long id) {
		mongoTemplate.remove(id);
	}

	public void removeByBean(NewsInfoBean nif) {
		mti.delete(nif);
	}

	public void removeByBeanList(List<NewsInfoBean> nif) {
		mti.deleteAll(nif);
	}

	public List<NewsInfoBean> findByCommand(int startIndex, int endIndex, String orderParam, Query query) {
		// 创建排序模板Sort
		Sort sort = new Sort(Sort.Direction.DESC, orderParam);
		// 创建分页模板Pageable
		Pageable pageable = PageRequest.of(startIndex, endIndex, sort);
		List<NewsInfoBean> find = mongoTemplate.find(query.with(pageable), NewsInfoBean.class);
		return find;
	}

}
