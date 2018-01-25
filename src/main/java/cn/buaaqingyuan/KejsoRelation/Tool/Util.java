package cn.buaaqingyuan.KejsoRelation.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	/*
	 *  keywords 条件选择
	 *  允许两种情况
	 *  1.中文字符、英文字符、-
	 *  2.中文字符
	 * */
	public static boolean  filter(String content)
	{
		// 过滤数字
		if(isNumber(content))
		{
			return false;
		}
		
		// 过滤第一个字符
		if(content.charAt(0) == '型')
		{
			return false;
		}
		
		// -不能在首尾
		if(content.indexOf("-") == 0 || content.indexOf("-") == (content.length()-1))
		{
			return false;
		}
		
		//包含特殊字符
		List<String> chs = new ArrayList<String>(Arrays.asList("(",")","的","和","与","及","研发","性能"));
		for(String one:chs)
		{
			if(content.contains(one))
			{
				return false;
			}
		}
		
		String result = content.replaceAll("[^(\\u4E00-\\u9FA5)]", "");
		
		// 长度为1 或 中文长度大于6
		if(content.length() < 2 || result.length() > 6 )
		{
			return false;
		}
		
		//纯中文字符
		if(result.length() == content.length())
		{
			return true;
		}
		
		if(isContainChinese(content)&&isLetterDigitOrChinese(content))
		{
			return true;
		}
		
		return false;
	}
	
	// 判断是否仅包含字母和汉字
	public static boolean isLetterDigitOrChinese(String str) {
		  String regex = "^[-a-zA-Z\u4e00-\u9fa5]+$";
		  return str.matches(regex);
	}
	
	
	// 判断是否为数字
	public static boolean isNumber(String str) {
		  String regex = "^[0-9]+$";
		  return str.matches(regex);
	}
	
	//判断是否包含中文
	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
	    Matcher m = p.matcher(str);
	    if (m.find()) {
	    	return true;
	    }
	    return false;
	}
	
	public static void main(String[] args)
	{
		String text = "子群F-";
		char s = text.charAt(0);
		if(s == '子')
			System.out.println("hello");
		
	}
	
}
