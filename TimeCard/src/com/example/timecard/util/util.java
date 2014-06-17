package com.example.timecard.util;


public class util {
	
	/**
	 * LPAD("기존문자", 문자길이, 채울 문자)
	 * @param str
	 * @param lpadNum
	 * @param ch
	 * @return
	 */
	public static String lpad(String str, int lpadNum, String ch){
		
		String result="";
		
		 
		if(str == null || str.length() >= lpadNum){
			return str;
		}
		
		int cnt = lpadNum - str.length();
		for(int i=0; i<cnt; i++){
			result += ch;
		}
		
		return result+str;
	}
	
	public static String checkNull(String str){
		if(str == null || str.equals(""))
			return "";
		else return str;
	}
	
	public static boolean isNull(String str){
		if(str == null || str.equals("")) return true;
		else return false;
	}
	
}
