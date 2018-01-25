package cn.buaaqingyuan.KejsoRelation.Ner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.domain.Nature;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;


public class EntityRecognizer {
	
	private static List<String> punctuations = new ArrayList<String>(Arrays.asList("//?","？"));
	
	private static List<String> web_symbols = new ArrayList<String>(Arrays.asList("&middot;","&gt;","&lt;","&quot;","&deg;","&times;","&plusmn;","&oacute;","&iacute;","&amp;","&uuml;"));
	
	private static List<String> symbols = new ArrayList<String>(Arrays.asList(",","，",")","(","。",".","＜","、","”","“","×"));
	
	private static List<String> positions = new ArrayList<String>(Arrays.asList("n","nis","vn","v","ns","nt","nnt","nnd"));
	
	// convert punctuation to 。
	public static List<String> PunctuationText(String content)
	{
		List<String> sents = new ArrayList<String>();
		//delete web symbols
		for(String sym:web_symbols)
		{
			if(content.contains(sym))
			{
				content = content.replaceAll(sym, "");
			}
		}
		for(String punc:punctuations)
		{
			if(content.contains(punc))
			{
				content = content.replaceAll(punc, "。");
			}
			
		}
		// handle .
		Pattern pat = Pattern.compile("([0-9]+)(\\.)([0-9]+)");
		Matcher m = pat.matcher(content);
		content= m.replaceAll("$1#$3").replaceAll("\\.", "。").replaceAll("#", "\\.");	
		
		StringTokenizer token=new StringTokenizer(content,"。");  
        while(token.hasMoreElements()){  
         sents.add(token.nextToken());  
        } 
        return sents;
	}
 	
	public static boolean  isEntity(String nature)
	{
		if(nature.equals("kejso_entity")||nature.equals("nr"))
			return true;
		else
			return false;
	}
	
	public static List<Term> combinePosition(List<Term> parse)
	{
		List<Term> results = new ArrayList<Term>();
		for(int i=0;i<parse.size();i++)
		{
			Term item = parse.get(i);
			if(item.getNatureStr().equals("nnd") || item.getNatureStr().equals("nnt"))
			{
				String name = item.getName();
				int del_pos = -1;
				for(int j=results.size()-1;j>=0;j--)
				{
					if(!positions.contains(results.get(j).getNatureStr()))
					{
						del_pos = j;
						break;
					}
					name = results.get(j).getName() + name;
				}
				for(int j=results.size()-1;del_pos>=0&&j>del_pos;j--)
				{
					results.remove(j);
				}
				Term current = new Term(name,1,"kejso_entity",1);
				results.add(current);
				
			}else{
				results.add(item);
			}
		}
		List<Term> results2 = new ArrayList<Term>();
		//合并连续两个或多个nr
		boolean flag = false;
		for(int i=0;i<results.size();i++)
		{
			Term item = results.get(i);
			if(flag == true && item.getNatureStr().equals("nr"))
			{
				String name = results2.get(results2.size()-1).getName();
				results2.remove(results2.size()-1);
				name += item.getName();
				Term current = new Term(name,1,"nr",1);
				results2.add(current);
				continue;
			}
			if(!item.getNatureStr().equals("nr"))
			{
				results2.add(item);
				flag = false;
			}else{
				results2.add(item);
				flag = true;
			}
			
		}
		return results2;
	}
	
	public static String LabelSentence(String sentence)
	{
		// entitys
		List<String> entitys = new ArrayList<String>();
		List<Term> parse = ToAnalysis.parse(sentence);
		//preprocess parse
		List<Term> parse_2 = new ArrayList<Term>();
		parse_2 = combinePosition(parse);
		String result="";
		for(Term item : parse_2)
		{
			if(isEntity(item.getNatureStr())&&!entitys.contains(item.getName()))
			{
				result = result +"<Entity>"+item.getName()+"</Entity>";
				entitys.add(item.getName());
			}
			else{
				result = result +item.getName();
			}
		}
		//对result中连续的Entity进行合并
		//result = result.replaceAll("</Entity><Entity>", "");
		//统计result中Entity个数
		int num = 0;
		if(result.indexOf("</Entity>") != -1)  
	    {  
	        String[] str1 = result.split("</Entity>");  
	        num = str1.length-1;  
	    }
	    //2
		if(num < 2)
			return "";
		
		//过滤掉距离比较远的句子
		int  one_tail = result.indexOf("</Entity>");
		int  two_head = result.lastIndexOf("<Entity>");
		String middle_content = result.substring(one_tail, two_head);
		boolean flag = false;
		for(String symbol : symbols)
		{
			if(middle_content.contains(symbol))
			{
				flag = true;
				break;
			}
		}
		
		if(flag)
		{
			return "";
		}
		
		
		return result;
	}
	
	//duplicate examples
	
	public static List<String> duplicateExamples(String example)
	{
		List<String> examples = new ArrayList<String>();
		if(example == "")
		{
			return examples;
		}
		//select two entity from all entitys
		String[] str1 = example.split("</Entity>");  
		int entity_num = str1.length - 1;
		if(entity_num == 2)
		{
			examples.add(example);
			return examples;
		}
		for(int i=0;i<entity_num;i++)
		{
			for(int j=i+1;j<entity_num;j++)
			{
				String temp="";
				for(int k=0;k<str1.length;k++)
				{
					if(k == i||k==j)
					{
						temp += str1[k]+"</Entity>";
					}else{
						temp += str1[k].replaceAll("<Entity>", "");
					}
				}
				examples.add(temp);
			}
		}
		return examples;
	}
	
	
	public static List<String> GetEntitysFromText(String text)
	{
		List<String> sentences = PunctuationText(text);
		// entitys
		List<String> entitys = new ArrayList<String>();
		for(String sentence:sentences)
		{
			List<Term> parse = ToAnalysis.parse(sentence);
			//preprocess parse
			List<Term> parse_2 = new ArrayList<Term>();
			parse_2 = combinePosition(parse);
			for(Term item : parse_2)
			{
				if(isEntity(item.getNatureStr())&&!entitys.contains(item.getName()))
				{
					entitys.add(item.getName());
				}
			}
		}
		
		return entitys;
	}
	
	public static List<String> LableText(String text)
	{
		List<String> results = new ArrayList<String>();
		List<String> sentences = PunctuationText(text);
		for(String sentence:sentences)
		{
			String result = LabelSentence(sentence);
			// 改写result
			List<String> rewrite_examples = new ArrayList<String>();
			rewrite_examples = duplicateExamples(result);
			if(rewrite_examples.size() > 0)
			{
				results.addAll(rewrite_examples);
			}
		}
		return results;
	}
		
	public static void main(String[] args) throws IOException 
	{
		
		String text="纽约州立大学著名心理学家玛琳娜罗曼说, “这是你缺乏经验而作出的一次无益的选择";
		
		List<String> results = EntityRecognizer.GetEntitysFromText(text);
		for(String result : results)
		{
			System.out.println(result);
		}
		
		List<String> tests = EntityRecognizer.LableText(text);
		for(String result:tests)
		{
			System.out.println(result);	
		}
	}

}
