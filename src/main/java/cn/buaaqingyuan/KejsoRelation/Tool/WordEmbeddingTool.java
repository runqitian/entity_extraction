package cn.buaaqingyuan.KejsoRelation.Tool;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;

import cn.buaaqingyuan.KejsoRelation.Ner.EntityRecognizer;
import cn.buaaqingyuan.KejsoRelation.Sql.SqlUtil;

public class WordEmbeddingTool {

	public static void SegKejsoCorpus(String outputfile,int total,int offset) throws IOException
	{
		SqlSession session=SqlUtil.getSession();
		List<String> contents = new ArrayList<String>();
		int block = 10000;
		File  word_output_f = new File(outputfile+".word");
		File  pos_output_f = new File(outputfile+".pos");
		for(int i=1;i<=total;i++)
		{
			contents = SqlUtil.getContents(session, offset);
			
			for(String content:contents)
			{
				//断句
				List<String> sentences = EntityRecognizer.PunctuationText(content);
				for (String sentence:sentences)
				{
					List<Term> parse = ToAnalysis.parse(sentence);
					String word_result="";
					String pos_result="";
					for(int j=0;j<parse.size();j++)
					{
						Term item = parse.get(j);
						if(j==0)
						{
							word_result += item.getName()+" ";
							pos_result += item.getNatureStr()+" ";
						}else{
							word_result += " "+item.getName();
							pos_result += " "+item.getNatureStr();
						}
					}
					FileUtils.write(word_output_f, word_result+"\n", true);
					FileUtils.write(pos_output_f, pos_result+"\n", true);
				}
			}	
			offset = offset + block ;
			System.out.format("already processed %f%%, total is %d,current offset is %d\n", (float)i*100/total,i*block,offset);
		}
		session.close();
	}
	
	public static void main(String[] args) throws IOException 
	{
		String outputfile = "kejso_word2vec_2003.txt";
		WordEmbeddingTool.SegKejsoCorpus(outputfile, 62, 0);
	}
}
