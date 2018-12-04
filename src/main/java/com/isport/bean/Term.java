package com.isport.bean;


/** brief description
 * <p>Date : 2016年8月30日 下午5:38:17</p>
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

public class Term {
	private String word;
	private String lexicalCategory = "unknown";
	private int freq = 0;
	private double tf;
	private double idf;
	private double tfidf = 0;
	private double chi = 0;
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getLexicalCategory() {
		return lexicalCategory;
	}

	public void setLexicalCategory(String lexicalCategory) {
		this.lexicalCategory = lexicalCategory;
	}

	public int getFreq() {
		return freq;
	}

	public void incrFreq() {
		freq++;
	}

	public double getTf() {
		return tf;
	}

	public void setTf(double tf) {
		this.tf = tf;
	}

	public double getIdf() {
		return idf;
	}

	public void setIdf(double idf) {
		this.idf = idf;
	}

	public double getTfidf() {
		return tfidf;
	}

	public void setTfidf(double tfidf) {
		this.tfidf = tfidf;
	}

	public Term(String word) {
		super();
		this.word = word;
	}
	
	@Override
	public int hashCode() {
		return word.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		Term other = (Term) obj;
		return word.equals(other.word);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[")
			.append("word=").append(word).append(", ")
			.append("freq=").append(freq).append(", ")
			.append("tf=").append(tf).append(", ")
			.append("idf=").append(idf).append(", ")
			.append("tf-idf=").append(tfidf)
			.append("]");
		return buffer.toString();
	}

	public double getChi() {
		return chi;
	}

	public void setChi(double chi) {
		this.chi = chi;
	}
}
