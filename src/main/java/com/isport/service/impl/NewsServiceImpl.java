package com.isport.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.isport.bean.NewsInfoBean;
import com.isport.bean.sys_param_t;
import com.isport.service.NewsService;
import com.isport.utils.DateUtils;
import com.isport.utils.StringUtils;
import com.isport.utils.EsUtils;

@Service
public class NewsServiceImpl implements NewsService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	EsUtils esUtils;

	@Override
	public boolean save(NewsInfoBean newsInfoBean) {
		String sql = "REPLACE INTO NEWS_DATA_INFO(ID,TITLE,TITLE_IMG,SUMMARY,CONTENT,PUBDATE,SOURCE,SOURCE_ICON,URL,INDEX_URL,BLOCK,KEY_WORD,COMMENT_NUM,AUTHOR,"
				+ "AUTHOR_IMG,REMARK,CREATE_DATE,DISPOSE_STATE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try {
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, StringUtils.md5(newsInfoBean.getUrl()));
					ps.setString(2, newsInfoBean.getTitle());
//					ps.setString(3, newsInfoBean.getTitle_img());
					ps.setString(4, newsInfoBean.getSummary());
					ps.setString(5, newsInfoBean.getContent());
					ps.setString(6, newsInfoBean.getPub_date());
					ps.setString(7, newsInfoBean.getSource());
					ps.setString(8, newsInfoBean.getSource_icon());
					ps.setString(9, newsInfoBean.getUrl());
					ps.setString(10, newsInfoBean.getIndex_url());
//					ps.setString(11, newsInfoBean.getBlock());
					ps.setString(12, newsInfoBean.getKey_word());
//					ps.setInt(13, newsInfoBean.getComment_num());
					ps.setString(14, newsInfoBean.getAuthor());
					ps.setString(15, newsInfoBean.getAuthor_img());
					ps.setString(16, newsInfoBean.getRemark());
					ps.setString(17, DateUtils.getStrYYYYMMDDHHmmss(new Date()));
					ps.setInt(18, newsInfoBean.getDispose_state());
				}

				@Override
				public int getBatchSize() {
					return 1;
				}
			});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean update(NewsInfoBean newsInfoBean) {
		String sql = "update NEWS_DATA_INFO set TITLE = ?, TITLE_IMG = ? ,SUMMARY = ?,PUBDATE = ?, KEY_WORD =? where id=?";
		try {
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, newsInfoBean.getTitle());
//					ps.setString(2, newsInfoBean.getTitle_img());
					ps.setString(3, newsInfoBean.getSummary());
					ps.setString(4, newsInfoBean.getPub_date());
					ps.setString(5, newsInfoBean.getKey_word());
					ps.setString(6, newsInfoBean.getId());
					// ps.setInt(6, newsInfoBean.getDispose_state());
				}

				@Override
				public int getBatchSize() {
					return 1;
				}
			});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean checkUrl(String href) {
		String result = esUtils.getDataById(StringUtils.md5(href));
		return ("".equals(result) || null == result || "null".equals(result)) ? true : false;
	}

	@Override
	public boolean updateStatus(NewsInfoBean newsInfoBean) {
		String sql = "update NEWS_DATA_INFO set dispose_state = ? , parse_error = ?  where id=?";
		try {
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setInt(1, newsInfoBean.getDispose_state());
					ps.setString(2, newsInfoBean.getParse_error());
					ps.setString(3, newsInfoBean.getId());
				}

				@Override
				public int getBatchSize() {
					return 1;
				}
			});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean batchSave(List<NewsInfoBean> newsInfoBeans) {
		String sql = "INSERT INTO NEWS_DATA_INFO(ID,TITLE,TITLE_IMG,SUMMARY,CONTENT,PUBDATE,SOURCE,SOURCE_ICON,URL,INDEX_URL,BLOCK,KEY_WORD,COMMENT_NUM,AUTHOR,"
				+ "AUTHOR_IMG,REMARK,CREATE_DATE,DISPOSE_STATE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try {
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, StringUtils.md5(newsInfoBeans.get(i).getUrl()));
					ps.setString(2, newsInfoBeans.get(i).getTitle());
//					ps.setString(3, newsInfoBeans.get(i).getTitle_img());
					ps.setString(4, newsInfoBeans.get(i).getSummary());
					ps.setString(5, newsInfoBeans.get(i).getContent());
					ps.setString(6, newsInfoBeans.get(i).getPub_date());
					ps.setString(7, newsInfoBeans.get(i).getSource());
					ps.setString(8, newsInfoBeans.get(i).getSource_icon());
					ps.setString(9, newsInfoBeans.get(i).getUrl());
					ps.setString(10, newsInfoBeans.get(i).getIndex_url());
//					ps.setString(11, newsInfoBeans.get(i).getBlock());
					ps.setString(12, newsInfoBeans.get(i).getKey_word());
//					ps.setInt(13, newsInfoBeans.get(i).getComment_num());
					ps.setString(14, newsInfoBeans.get(i).getAuthor());
					ps.setString(15, newsInfoBeans.get(i).getAuthor_img());
					ps.setString(16, newsInfoBeans.get(i).getRemark());
					ps.setString(17, DateUtils.getStrYYYYMMDDHHmmss(new Date()));
					ps.setInt(18, newsInfoBeans.get(i).getDispose_state());
				}

				@Override
				public int getBatchSize() {
					return newsInfoBeans.size();
				}
			});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取新闻的最后更新时间
	 * 
	 * @author wangyuanxi
	 * @param paramKey
	 * @return
	 */
	public String getLastUpdateTime(String paramKey) {
		try {
			sys_param_t spt = jdbcTemplate.queryForObject("SELECT PARAM_VALUE FROM sys_param_t WHERE PARAM_NAME=?",
					new Object[] { paramKey }, new BeanPropertyRowMapper<sys_param_t>(sys_param_t.class));
			return spt.getParam_value();
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * 设置新闻的最后更新时间
	 * 
	 * @author wangyuanxi
	 * @param paramKey
	 * @param paramValue
	 * @return
	 */
	public boolean setLastUpdateTime(String paramKey, String paramValue) {
		try {
			sys_param_t spt = jdbcTemplate.queryForObject("SELECT ID,PARAM_VALUE FROM sys_param_t WHERE PARAM_NAME=?",
					new Object[] { paramKey }, new BeanPropertyRowMapper<sys_param_t>(sys_param_t.class));
			jdbcTemplate.update("UPDATE sys_param_t SET PARAM_VALUE=? WHERE ID=?",
					new Object[] { paramValue, spt.getId() });
			return true;
		} catch (EmptyResultDataAccessException e) {
			jdbcTemplate.update("INSERT INTO sys_param_t(ID,PARAM_NAME,PARAM_VALUE,PARAM_DESC) VALUES(?,?,?,?)",
					new Object[] { StringUtils.getUUID(), paramKey, paramValue, paramKey + "源的最后更新时间" });
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
}
