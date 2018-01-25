package cn.buaaqingyuan.KejsoRelation.Ner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ansj.domain.Nature;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class PositionRecognizer {
	
	private static List<String> positions = new ArrayList<String>(Arrays.asList("n","nis","vn","ns","nt","nnt","nnd","kejso_entity","j"));
	
	public static void LabelPositionAndPerson(String sentence)
	{
		List<Term> parse = ToAnalysis.parse(sentence);
		System.out.println(parse);
		List<Term> result = new ArrayList<Term>();
		result = combinePosition(parse);
		System.out.println(result);
		//duplicate examples
		
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
	
	public static void main(String[] args) 
	{
		String sentence = "纽约州立大学著名心理学家玛琳娜罗曼说, “这是你缺乏经验而作出的一次无益的选择";
		LabelPositionAndPerson(sentence);
		
	}

}
