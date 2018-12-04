package com.isport.analyzer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isport.bean.KTBean;
import com.isport.utils.RedisUtils;
import com.isport.utils.StringUtils;


/** brief description
 * <p>Date : 2016年8月30日 下午5:01:36</p>
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
public class Train {
	@Autowired
	RedisUtils redisUtils;
	int hashbits=64;
	int splitCount=4;
	int length = hashbits / splitCount;
	public boolean  train(KTBean ktBean){
	   try{
			String content=ktBean.getContent();
			String textId=StringUtils.md5(content);
			String valueId=ktBean.getId();
			//全匹配
			String allMatchIds=String.valueOf(redisUtils.get(textId));
			if (allMatchIds!=null&&!"null".equals(allMatchIds)) {
				valueId+="#"+allMatchIds;
			}
			redisUtils.set(textId,valueId);
			//计算simhash值
			SimHash simHash = new SimHash(MMSeg4jAnalyzer.analyze(ktBean.getContent()), hashbits);
			String textSimHashCode=simHash.getStrSimHash();
			//拆分存储
			for (int i = 0; i < splitCount; i++) {
				//记录分段的位置1、2、3、4
				String splitCode=(i+1)+","+textSimHashCode.substring(i*length,(i+1)*length);
				//查询此位置是否已有对应元素
				Map<String,String> valueMap=redisUtils.getMap(splitCode);
				String value=ktBean.getId();
				//如果有元素则进行追加
				if(valueMap!=null&&!valueMap.isEmpty()&&valueMap.containsKey(simHash.getIntSimHash()+"")){
					value+="#"+valueMap.get(simHash.getIntSimHash().toString());
				}
				valueMap.put(simHash.getIntSimHash().toString(),value);
				//保存拆分后的串
				redisUtils.putMap(splitCode, valueMap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	  }
}
