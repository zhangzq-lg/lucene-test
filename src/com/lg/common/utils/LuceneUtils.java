package com.lg.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class LuceneUtils {

	// 定义一些静态变量
	private static Directory directory; // 索引库位置
	private static Analyzer analyzer; // 分词器

	// 管理IndexWriter对象，只能产生一个实例
	private static IndexWriter indexWriter;

	// 实例化变量
	static {

		try {
			// 此处读取配置文件
			Properties properties = new Properties();
			properties.load(LuceneUtils.class.getClassLoader()
					.getResourceAsStream("path.properties"));
			String path = properties.getProperty("indexDirectory");
			String version = properties.getProperty("version");

			/*
			 * 将字符串转换成对应的类 String clazz =
			 * version.substring(version.lastIndexOf("\\."));
			 * Class.forName(clazz).newInstance();
			 */

			directory = FSDirectory.open(new File(path));/* "./indexDirectory" */
			analyzer = new StandardAnalyzer(Version.LUCENE_30);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static IndexWriter getIndexWriter() {
		// 在此处实例化，什么时候使用就实例化它（静态的变量只有一个实例）
		// 此处的判断是由于多个线程同时访问进行的判断
		if(indexWriter == null) {
			// 注意线程安全问题,此处给目标对象加锁
			synchronized(LuceneUtils.class) {
				// 进行同步后，再次进行判断
				if(indexWriter == null) {
					try {
						indexWriter = new IndexWriter(directory, analyzer, MaxFieldLength.LIMITED);
						System.out.println("===== 初始化IndexWriter对象 ======");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
			
			// 指定一段代码，在虚拟机(JVM)退出之前执行
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					if(indexWriter != null) {
						try {
							indexWriter.close();
							System.out.println("===== 关闭IndexWriter对象 ======");
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
			});
		}
		
		return indexWriter;
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
