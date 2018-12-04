package com.isport.analyzer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.chenlb.mmseg4j.example.Simple;
import com.isport.bean.Term;

/** brief description
 * <p>Date : 2016年9月1日 上午10:22:40</p>
 * <p>Module : </p>
 * <p>Description: </p>
 * <p>Remark : </p>
 * @author Administrator
 * @version 
 * <p>------------------------------------------------------------</p>
 * <p> 修改历史</p>
 * <p> 序号 日期 修改人 修改原因</p>
 * <p> 1 </p>
 */

public class MMSeg4jAnalyzer {
	private final static Simple analyzer = new Simple();

	public static Map<String, Term> analyze(String content) {
		long anasta=System.currentTimeMillis();
		Map<String, Term> terms = new HashMap<String, Term>(0);
		try {
			String[] token = analyzer.segWords(content, " ").split(" ");
			for (String string : token) {
				String word = string.trim();
				if (!word.isEmpty() && word.length() > 1 && word != null
						&& !"".equals(word)) {
					Term term = terms.get(word);
					if (term == null) {
						term = new Term(word);
						terms.put(word, term);
					}
					term.incrFreq();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+"分词消耗:"+(System.currentTimeMillis()-anasta)+"毫秒");
		return terms;
	}
}
