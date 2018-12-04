package com.isport.analyzer;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isport.bean.KTBean;
import com.isport.utils.RedisUtils;
import com.isport.utils.StringUtils;


/** brief description
 * <p>Date : 2016年8月31日 下午7:36:14</p>
 * <p>Module : </p>
 * <p>Description: </p>
 * <p>Remark : </p>
 * @author zhangli
 * @version 
 * <p>------------------------------------------------------------</p>
 * <p> 修改历史</p>
 * <p> 序号 日期 修改人 修改原因</p>
 * <p> 1 </p>
 */
@Service
public class Simlar {
	@Autowired
	RedisUtils redisUtils;
	int hashbits=64;
	int splitCount=4;
	int length = hashbits / splitCount;
	
	int distance=3;//Integer.parseInt(System.getProperty("simlarDis"));
	public Set<String> simlar(KTBean ktBean){
		Set<String> simlarIds=new HashSet<String>();
	 try {
			String content=ktBean.getContent();
			String textId=StringUtils.md5(content);
			//判断是否全匹配
			String allMatchIds=String.valueOf(redisUtils.get(textId));
			if (allMatchIds!=null&&!"null".equals(allMatchIds)) {
				for (String allMatchId : allMatchIds.split("#")) {
					simlarIds.add(allMatchId);
					return simlarIds;
				}
			}
			//计算simHash
			SimHash simHash = new SimHash(MMSeg4jAnalyzer.analyze(ktBean.getContent()), hashbits);
			String textCode=simHash.getStrSimHash();
			//拆分查找匹配
			for (int i = 0; i < splitCount; i++) {
				String splitCode=(i+1)+","+textCode.substring(i*length,(i+1)*length);
				Map<String,String> valueMap=redisUtils.getMap(splitCode);
				//如果有则循环遍历
				if(valueMap!=null&&!valueMap.isEmpty()){
					for(Entry<String,String> entry:valueMap.entrySet()){
						int dis=simHash.hammingDistance(simHash.getIntSimHash(), new BigInteger(entry.getKey()));
						//如果有符合最短距离的元素则记录
						if(dis<distance){
							 simlarIds.add(entry.getValue());
							 return simlarIds;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	  return simlarIds;
	}
}
