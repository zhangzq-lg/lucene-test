package com.lg.test.idao.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;

import com.lg.common.utils.ArticleDocumentUtils;
import com.lg.common.utils.LuceneUtils;
import com.lg.test.entity.Article;
import com.lg.test.entity.QueryResult;

public class ArticleIndexDao {

	/**
	 * 增加索引库
	 * 
	 * @param article
	 */
	public void save(Article article) {
		// 1.把Article专程Document
		Document document = ArticleDocumentUtils.articleToDocument(article);
		// 2.添加到索引库
		IndexWriter indexWriter = null;

		try {
			indexWriter = new IndexWriter(LuceneUtils.getDirectory(),
					LuceneUtils.getAnalyzer(), MaxFieldLength.LIMITED);
			indexWriter.addDocument(document); // 添加
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			// 关闭操作
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

	}

	/**
	 * 删除索引，根据id删除 Term: 表示某个字段出现的某一个关键词（在索引库的目录中） Term term = new Term("title",
	 * "关键词");
	 * 
	 * @param id
	 */
	public void delete(Integer id) {

		IndexWriter indexWriter = null;
		try {
			String idStr = NumericUtils.intToPrefixCoded(id);
			Term term = new Term("id", idStr); // id存放到目录中，但是没有分词，所以idStr只有唯一个
			indexWriter = new IndexWriter(LuceneUtils.getDirectory(),
					LuceneUtils.getAnalyzer(), MaxFieldLength.LIMITED);
			indexWriter.deleteDocuments(term); // 删除所有含有这个term的Document对象
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			// 关闭操作
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * 更新索引库 更新流程： 先删除后添加
	 * 
	 * @param article
	 */
	public void update(Article article) {

		IndexWriter indexWriter = null;
		try {
			String idStr = NumericUtils.intToPrefixCoded(article.getId());
			Term term = new Term("id", idStr); // 目的：先删除
			indexWriter = new IndexWriter(LuceneUtils.getDirectory(),
					LuceneUtils.getAnalyzer(), MaxFieldLength.LIMITED);
			// 下面的第二个参数是Document是为了添加
			indexWriter.updateDocument(term,
					ArticleDocumentUtils.articleToDocument(article)); // 更新索引库(流程是先删除后添加，直接更新代价太大)
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			// 关闭操作
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * 查询 queryStr为查询条件
	 * 
	 * @param queryStr
	 * @throws IOException
	 */
	public List<Article> search(String queryStr) {

		// 此处是为了将其关闭所以放在此
		IndexSearcher indexSearcher = null;
		try {
			// 1.把查询条件转为Query对象（在title和content中查找）
			/* QueryParser:只支持在一个字段中查询 , 其子类 MultiFieldQueryParser支持在多个字段中查询 */
			QueryParser queryParser = new MultiFieldQueryParser(
					Version.LUCENE_30, new String[] { "title", "content" },
					LuceneUtils.getAnalyzer());
			Query query = queryParser.parse(queryStr);

			// 2.执行查询，得到结果值
			indexSearcher = new IndexSearcher(LuceneUtils.getDirectory());
			TopDocs topDocs = indexSearcher.search(query, 100); // 最多返回100条数据（此处的代码）

			// 3.将结果值处理成Article的列表
			List<Article> articleList = new ArrayList<Article>();
			for (int i = 0; i < topDocs.scoreDocs.length; i++) {
				// 根据doc的id查询
				int docId = topDocs.scoreDocs[i].doc;
				Document doc = indexSearcher.doc(docId);
				// 将Document对象转化为Article并放入list中
				articleList.add(ArticleDocumentUtils.documentToArticle(doc));
			}

			return articleList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			// 关闭indexsearch
			if (indexSearcher != null) {
				try {
					indexSearcher.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

	}

	/**
	 * 下面方法是分页处理
	 * @param queryStr：查询条件
	 * @param first：从结果列表中的那个索引开始
	 * @param max：最多查询条数
	 * @return
	 */
	public QueryResult search(String queryStr, int first, int max) {
		// 此处是为了将其关闭所以放在此
		IndexSearcher indexSearcher = null;
		try {
			// 1.把查询条件转为Query对象（在title和content中查找）
			/* QueryParser:只支持在一个字段中查询 , 其子类 MultiFieldQueryParser支持在多个字段中查询 */
			QueryParser queryParser = new MultiFieldQueryParser(
					Version.LUCENE_30, new String[] { "title", "content" },
					LuceneUtils.getAnalyzer());
			Query query = queryParser.parse(queryStr);

			// 2.执行查询，得到结果值
			indexSearcher = new IndexSearcher(LuceneUtils.getDirectory());
			TopDocs topDocs = indexSearcher.search(query, first + max); // 最多返回（第二个参数值）数据（此处的代码）
			int count = topDocs.totalHits; // 查询出来结果的总条数（而不是上面限定的条数）

			// 3.将结果值处理成Article的列表
			List<Article> articleList = new ArrayList<Article>();
			for (int i = first; i < (first + max > topDocs.scoreDocs.length ? topDocs.scoreDocs.length : first + max); i++) { // 截取一段数据
				// 根据doc的id查询
				int docId = topDocs.scoreDocs[i].doc;
				Document doc = indexSearcher.doc(docId);
				// 将Document对象转化为Article并放入list中
				articleList.add(ArticleDocumentUtils.documentToArticle(doc));
			}

			// 封装结果并返回
			return new QueryResult(articleList, count);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			// 关闭indexsearch
			if (indexSearcher != null) {
				try {
					indexSearcher.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
	}

}
