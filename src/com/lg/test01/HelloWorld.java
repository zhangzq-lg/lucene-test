package com.lg.test01;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.lg.test01.entity.Article;

/**
 * 添加索引库并查询数据
 * @author zzq_eason
 */
public class HelloWorld {

	// 共同要使用的资源
	// 存放路径
	private static Directory directory = null;
	// 分词器声明
	private static Analyzer analyzer = null;
	static {
		
		try {
			directory = FSDirectory.open(new File("./indexDirectory"));
			analyzer = new StandardAnalyzer(Version.LUCENE_30);
		} catch (IOException e) {
			System.out.println("实例化directory抛出异常");
			throw new RuntimeException(e);
		}
		
	}

	// 创建索引库
	@Test
	public void testCreateIndex() throws IOException {
		// 生成数据，此处应该是调用服务进行操作，在此处就省略
		Article article = new Article();
		article.setId(1);
		article.setTitle("乱七八糟的想啥");
		article.setContent("我心烦啊，想太多了，就是不好，怎么可以让自己静下来，那就把自己打晕吧");

		/**
		 * 将数据放入索引库中,其步骤： 1.把Article转为Document 2.把Document放入索引中
		 */
		Document doc = new Document();
		doc.add(new Field("id", article.getId().toString(), Store.YES,
				Index.ANALYZED));
		doc.add(new Field("title", article.getTitle(), Store.YES,
				Index.ANALYZED));
		doc.add(new Field("content", article.getContent(), Store.YES,
				Index.ANALYZED));

		/*
		 * 存放路径 Directory directory = FSDirectory.open(new
		 * File("./indexDirectory")); 
		 * 分词器声明 Analyzer analyzer = new
		 * StandardAnalyzer(Version.LUCENE_30);
		 */
		// 实例化索引写入类
		IndexWriter indexWriter = new IndexWriter(directory, analyzer,
				MaxFieldLength.LIMITED);
		indexWriter.addDocument(doc);
		// 节省资源，用完关闭
		indexWriter.close();

	}

	// 搜索
	@Test
	public void testSearch() throws Exception {
		// 获取查询条件，可以使用内容试试
		String queryString = "乱七八糟";

		// 执行搜索
		List<Article> articleList = new ArrayList<Article>();

		// 1.将条件转换成Query对象(默认只查询标题)
		QueryParser queryParser = new QueryParser(Version.LUCENE_30, "title", analyzer);
		Query query = queryParser.parse(queryString);
		// 2.Query执行查询得到结果
		IndexSearcher indexSearch = new IndexSearcher(directory);
		TopDocs topDocs =indexSearch.search(query, 20); // 第二个参数表示返回靠前的n条记录
		// 总共条数
		int count = topDocs.totalHits;
		// 其中存放的是文档的编号，文档编号唯一，这样存放数据才能更多
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		// 数据解析
		for(ScoreDoc scoreDoc : scoreDocs) {
			ScoreDoc scoreTemp = scoreDoc;
			float score = scoreTemp.score; // 相关度，目的为了排序，在对象中实现的算法
			int docId = scoreTemp.doc; // 文档的编号
			// 根据文档编号获取文档
			Document docTemp = indexSearch.doc(docId);
			// 将文档转换成Article
			String idStr = docTemp.get("id");
			String title = docTemp.get("title");  /* 第二种方式：docTemp.getField("title").stringValue();*/
			String content = docTemp.get("content");
			// 封装数据
			Article article = new Article();
			article.setId(Integer.valueOf(idStr));
			article.setTitle(title);
			article.setContent(content);
			articleList.add(article);
		}
		// 文档查询得关闭，节约资源
		indexSearch.close();
		
		// 显示结果
		System.out.println("总结果条数：" + articleList.size());
		for (Article ar : articleList) {
			System.out.println(ar);
		}

	}

}
