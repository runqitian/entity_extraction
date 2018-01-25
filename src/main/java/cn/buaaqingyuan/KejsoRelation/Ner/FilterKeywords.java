package cn.buaaqingyuan.KejsoRelation.Ner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import cn.buaaqingyuan.KejsoRelation.Tool.Util;


public class FilterKeywords {
	
	
	public static boolean iscontainV(String content)
	{
		List<Term> parse = ToAnalysis.parse(content);
		// 如果为一个词，则过滤掉
		if(parse.size()==1)
		{
			return true;
		}
		//过滤掉特定模式
		//最后一个是形容词、方位词
		String tag = parse.get(parse.size()-1).getNatureStr();
		if(tag.equals("a") || tag.equals("f") || tag.equals("gi") || tag.equals("b") || tag.equals("gb"))
		{
			return true;
		}
		
		// 两个词模式
		if(parse.size() == 2)
		{
			String tag_1 = parse.get(0).getNatureStr();
			String tag_2 = parse.get(1).getNatureStr();
			// a, n
			if(tag_1.equals("a") && tag_2.equals("n"))
			{
				return true;
			}
			// n,nz
			if(tag_1.equals("n") && tag_2.equals("nz"))
			{
				return true;
			}
			// n , an
			if(tag_1.equals("n") && tag_2.equals("an"))
			{
				return true;
			}
			//a ,nz
			if(tag_1.equals("a") && tag_2.equals("nz"))
			{
				return true;
			}
		}
		
		boolean all_n = true;
		for(Term one:parse)
		{
			String ns = one.getNatureStr();
			if(!ns.equals("n"))
			{
				all_n = false;
			}
			if(ns.equals("v") || ns.equals("vn") || ns.equals("vi") || ns.equals("vf") || ns.equals("d") 
					|| ns.equals("ad") || ns.equals("p") || ns.equals("l"))
			{
				return true;
			}
				
		}
		
		
		return all_n;
	}
	
	public static void  Filter(String input,String output) throws IOException
	{
		final LineIterator it = FileUtils.lineIterator(new File(input), "UTF-8");
		
		File  phrase_f = new File(output);
		
		File  exp_f = new File("exp_keywords.txt");
		
		int count = 0;
		while( it.hasNext())
		{
			String line = it.nextLine();
			count++;
			String[] keywords = line.split(" ");
			for(int i=0;i<keywords.length;i++)
			{
				// 过滤英文
				if(!keywords[i].contains("-"))
				{
					keywords[i] = keywords[i].replaceAll("[^(\\u4E00-\\u9FA5)]", "");
				}
				boolean filter = Util.filter(keywords[i]);
				boolean conv= iscontainV(keywords[i]);
				if(filter&&conv==false)
				{
					FileUtils.write(phrase_f,keywords[i]+"\n", true);
				}
				else if(filter&&conv==true){
					FileUtils.write(exp_f,keywords[i]+"\n", true);
				}
			}
			
		}
		
	}
	
	
	public static void main(String[] args) throws IOException
	{
		
		
		String input="entitys_7.txt";
		String output = "entitys_8.txt";
		FilterKeywords.Filter(input, output);
		
		
		/*
		String content="(";
		System.out.println(Util.filter(content));
		System.out.println(FilterKeywords.iscontainV(content));
		*/
		
		/*
		String content="公众恐慌 沉默活性 长期规划 表面改性效果 区划模拟 瑞米充填 亚低温脑缺血 时代文化精神 模拟方式 自然药材 盲目支出 株间差异 精确对靶 经颅重复性";
		List<Term> parse = ToAnalysis.parse(content);
		System.out.println(parse);
		*/
		
	}

}
