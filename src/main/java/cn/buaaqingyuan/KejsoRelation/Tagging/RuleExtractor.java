package cn.buaaqingyuan.KejsoRelation.Tagging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class RuleExtractor {
	
	String pattern_file;
	
	public RuleExtractor(String patternfile)
	{
		this.pattern_file = patternfile;
	}
	
	
	
	public List<String> getPatterns() throws IOException
	{
		LineIterator it = FileUtils.lineIterator(new File(this.pattern_file), "UTF-8");
		
		List<String> patterns = new ArrayList<String>();
		
		while( it.hasNext())
		{
			String sentence = it.nextLine();
			patterns.add(sentence);
		}
		return patterns;
	}
	
	public boolean isMatch(String sentence, String regex)
	{
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sentence);
		return matcher.matches();
	}
	
	public static void main(String[] args)
	{
		String sent = "<Entity>平禹一矿</Entity>位于<Entity>禹州煤田</Entity>东北部荟萃山";
		String regex = "(.*?)<Entity>(.+?)</Entity>位于<Entity>(.+?)</Entity>(.*)";
		RuleExtractor re = new RuleExtractor("");
		System.out.println(re.isMatch(sent, regex));
	}

}
