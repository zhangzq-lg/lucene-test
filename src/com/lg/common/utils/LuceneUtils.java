package com.lg.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class LuceneUtils {

	// 定义一些静态变量
	private static Directory directory; // 索引库位置
	private static Analyzer analyzer; // 分词器

	// 实例化变量
	static {
		
		try {
			// 此处读取配置文件
			Properties properties = new Properties();
			properties.load(LuceneUtils.class.getClassLoader().getResourceAsStream("path.properties"));
			String path = properties.getProperty("indexDirectory");
			String version = properties.getProperty("version");
			
			/*将字符串转换成对应的类
			String clazz = version.substring(version.lastIndexOf("\\."));
			Class.forName(clazz).newInstance();*/
			
			directory = FSDirectory.open(new File(path));/* "./indexDirectory" */
			analyzer = new StandardAnalyzer(Version.LUCENE_30);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Directory getDirectory() {
		return directory;
	}

	public static Analyzer getAnalyzer() {
		return analyzer;
	}
	
	@Test
	public void test() {
		LuceneUtils.getDirectory();
	}

}
