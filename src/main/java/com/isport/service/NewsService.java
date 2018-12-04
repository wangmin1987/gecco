package com.isport.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.isport.bean.NewsInfoBean;

@Service
public interface NewsService {
	public boolean save(NewsInfoBean newsInfoBean);

	public boolean batchSave(List<NewsInfoBean> newsInfoBeans);

	public boolean update(NewsInfoBean newsInfoBean);

	boolean updateStatus(NewsInfoBean newsInfoBean);

	boolean checkUrl(String href);

	/**
	 * 获取新闻的最后更新时间
	 * 
	 * @author wangyuanxi
	 * @param paramKey
	 * @return
	 */
	public String getLastUpdateTime(String paramKey);

	/**
	 * 设置新闻的最后更新时间
	 * 
	 * @author wangyuanxi
	 * @param paramKey
	 * @param paramValue
	 * @return
	 */
	public boolean setLastUpdateTime(String paramKey, String paramValue);
}
