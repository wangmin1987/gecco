package com.isport.analyzer;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isport.bean.Term;


public class SimHash implements Serializable{
	private static final long serialVersionUID = 1L;
    private String strSimHash;
    private BigInteger intSimHash;
    private int hashbits = 64;
    Map<String, Term> terms = new HashMap<String, Term>(0);
	/**
	 * @return the strSimHash
	 */
	public String getStrSimHash() {
		return strSimHash;
	}
	/**
	 * @param strSimHash the strSimHash to set
	 */
	public void setStrSimHash(String strSimHash) {
		this.strSimHash = strSimHash;
	}
	/**
	 * @return the intSimHash
	 */
	public BigInteger getIntSimHash() {
		return intSimHash;
	}
	/**
	 * @param intSimHash the intSimHash to set
	 */
	public void setIntSimHash(BigInteger intSimHash) {
		this.intSimHash = intSimHash;
	}
	/**
	 * @return the hashbits
	 */
	public int getHashbits() {
		return hashbits;
	}
	/**
	 * @param hashbits the hashbits to set
	 */
	public void setHashbits(int hashbits) {
		this.hashbits = hashbits;
	}
	/**
	 * @return the terms
	 */
	public Map<String, Term> getTerms() {
		return terms;
	}
	/**
	 * @param terms the terms to set
	 */
	public void setTerms(Map<String, Term> terms) {
		this.terms = terms;
	}
	public SimHash(){
    	super();
    }
    public SimHash(Map<String, Term> terms, int hashbits) {
    	this.hashbits = hashbits;
    	long simsta=System.currentTimeMillis();
        this.intSimHash = this.simHashWords(terms);
        System.out.println(Thread.currentThread().getName()+"计算simHash消耗:"+(System.currentTimeMillis()-simsta)+"毫秒");
	}
	private BigInteger simHashWords(Map<String, Term> terms) {
		int[] v = new int[this.hashbits];   //将simhash的各位初始化为0 
		for(Entry<String, Term> entry:terms.entrySet()){
			BigInteger t = hash(entry.getValue().getWord());
			for (int i = 0; i < this.hashbits; i++) {  
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);  //	1后面i个0
                 if (t.and(bitmask).signum() != 0) { //正负数   对各word的hashcode的每一位，如果该位为1，则simhash相应位的值加1；否则减1 
                    v[i] += entry.getValue().getFreq();
                } else {
                    v[i] -= entry.getValue().getFreq();
                }
            }
		  }
        StringBuffer bf = new StringBuffer();
        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < this.hashbits; i++) {  //如果该位大于1，则设为1；否则设为0 
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                bf.append("1");
            }else{
            	bf.append("0");
            }
        }
        this.strSimHash = bf.toString();
        return fingerprint;
	  } 
   /**
    * hash计算
    * @param source
    * @return
    */
    private BigInteger hash(String source) {
    	if (source == null || source.length() == 0) {
			return new BigInteger("0");
		} else {
			char[] sourceArray = source.toCharArray();
			BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
			BigInteger m = new BigInteger("1000003");
			BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
			for (char item : sourceArray) {
				BigInteger temp = BigInteger.valueOf((long) item);
				x = x.multiply(m).xor(temp).and(mask);
			}
			x = x.xor(new BigInteger(String.valueOf(source.length())));
			if (x.equals(new BigInteger("-1"))) {
				x = new BigInteger("-2");
			}
			return x;
		}
    }
    /**
     * 海明距离
     * @param other
     * @return
     */
    public int hammingDistance(SimHash other) {
    	BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;
         while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }
    public int hammingDistance(BigInteger bi1,BigInteger bi2) {
    	BigInteger x = bi1.xor(bi2);
        int tot = 0;
         while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }
    public static int getDistance(String str1, String str2) {
		int distance=0;
		if (str1.length() != str2.length()) {
			distance = -1;
		} else {
			distance = 0;
			for (int i = 0; i < str1.length(); i++) {
				if (str1.charAt(i) != str2.charAt(i)) {
					distance++;
				}
			}
		}
		return distance;
	}
    public static void main(String[] args) {}
    @Override
    public boolean equals(Object obj) {
    	// TODO Auto-generated method stub
    	if(!(obj instanceof SimHash)){
    		return false;
    	}
    	SimHash sim = (SimHash) obj;
    	if(sim.hammingDistance(this)<=5){
    		return true;
    	}
    	return false;
    }
}