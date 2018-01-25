package cn.buaaqingyuan.KejsoRelation.Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class Distinct {
	
	public static void main(String[] args) throws IOException {
		
		String input="entitys_8.txt";
		String output="entitys_9.txt";
		final LineIterator it = FileUtils.lineIterator(new File(input), "UTF-8");
		File  phrase_f = new File(output);
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		String s = null;
		while(it.hasNext()){
			s = it.nextLine();
			if(!hm.containsKey(s)){  //如果hm没有包含s这个单词，则把s加入到hm，同时写入文件treeWord.txt中
				hm.put(s, 1);
				//输出到文件
				FileUtils.write(phrase_f,s+"\n", true);
			}
				
		}
	
	}

}
