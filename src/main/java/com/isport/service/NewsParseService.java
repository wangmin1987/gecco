package com.isport.service;

import java.util.List;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public interface NewsParseService {

	/**
	 * 资讯图片上传：将所选DIV元素内的所有图片转存至自己的服务器，并替换原路径
	 * 
	 * @author wangyuanxi
	 * @param element
	 * @return
	 */
	public boolean uploadImg(Element element);

	/**
	 * 获取资讯配图
	 * 
	 * @author songshaohui
	 * @param element
	 * @return
	 */
	public List<String> getImage(Element element);

	/**
	 * 获取资讯配图：所选DIV元素内的第一张图片
	 * 
	 * @author wangyuanxi
	 * @param element
	 * @return
	 */
	public String getFirstImage(Element element);

	/**
	 * 检测是否为视频资讯（只有视频无图片）
	 * 
	 * @author songshaohui
	 * @param ele
	 * @return
	 */
	public boolean checkVideoImg(String content);

	public boolean checkVideoImg(Element ele);

	/**
	 * 获取资讯摘要
	 * 
	 * @author wangyuanxi
	 * @param content
	 * @return
	 */
	public String getSummary(String content);

	/**
	 * 资讯过滤
	 * 
	 * @date 2018年9月11日 下午3:39:23
	 * @author wangyuanxi
	 * @param content
	 * @throws Exception
	 */
	public void filter(String content) throws Exception;

}
