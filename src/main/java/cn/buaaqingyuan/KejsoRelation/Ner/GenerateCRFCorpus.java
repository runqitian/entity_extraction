package cn.buaaqingyuan.KejsoRelation.Ner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.MyStaticValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/*
 * 从标注实体的句子语料中生成用于CRF-NER训练格式的语料
 * 
 */
public class GenerateCRFCorpus {
	
	static{
		MyStaticValue.userLibrary = "library/default/default.dic";
	}
	
	// entity in sentence marked by <Entity></Entity>
	// B I E S O
	public static List<String>  parseSentence(String sentence)
	{
		List<String>  words = new ArrayList<String>();
		String clean_sentence = sentence.replaceAll("<Entity>", "begin").replaceAll("</Entity>", "end");
		
		List<Term> parse = ToAnalysis.parse(clean_sentence);
		
		boolean  begin_entity = false;
		boolean  end_entity = true;
		Term next_item = null;
		
		for(int i=0;i<parse.size();i++)
		{
			Term item = parse.get(i);
			
			if(i+1<parse.size())
			{
				next_item = parse.get(i+1);
			}
			
			// tag ==null
			if(item.getNatureStr()=="null")
			{
				continue;
			}
			
			if(item.getName().equals("begin"))
			{
				begin_entity = true;
				end_entity = false;
				continue;
			}
			if(item.getName().equals("end"))
			{
				end_entity = true;
				begin_entity = false;
				continue;
			}
			
			// get flag
			String flag = "O";
			if(end_entity==false&&begin_entity==false&&next_item!=null&&next_item.getName().equals("end"))
			{
				flag="E";
				end_entity = true;
			}else if(begin_entity==true&&end_entity==false&&next_item!=null&&next_item.getName().equals("end"))
			{
				flag="S";
			}else if(begin_entity==true&&end_entity==false&&next_item!=null&&!next_item.getName().equals("end"))
			{
				flag = "B";
				begin_entity = false;
			}else if(begin_entity==false&&end_entity==false&&next_item!=null&&!next_item.getName().equals("end"))
			{
				flag = "I";
			}
			
			String word_line = item.getName()+"\t"+item.getNatureStr()+"\t"+flag;
			words.add(word_line);
		}
		
		return words;
	}
	
	
	public static void  buildfromCorpus(String inputcorpus,String outputfile) throws IOException
	{
		final LineIterator it = FileUtils.lineIterator(new File(inputcorpus), "UTF-8");
		
		File  phrase_f = new File(outputfile);
		
		while( it.hasNext())
		{
			String sentence = it.nextLine();
			List<String> results = parseSentence(sentence);
			for(String word:results)
			{
				FileUtils.write(phrase_f,word+"\n", true);
			}
			// add space line
			FileUtils.write(phrase_f,"\n", true);
		}
		
	}
	
	
	public static void main(String[] args) throws IOException
	{
		String inputcorpus = "Extracted_examples_2010.txt";
		String outputfile="crf_test_data.txt";
		
		GenerateCRFCorpus.buildfromCorpus(inputcorpus, outputfile);
	}

}
