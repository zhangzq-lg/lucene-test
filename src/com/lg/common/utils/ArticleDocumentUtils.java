package com.lg.common.utils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.util.NumericUtils;

import com.lg.test01.entity.Article;

public class ArticleDocumentUtils {

	// 将Article转换成Document方法
	public static Document articleToDocument(Article article) {
		// 申明变量
		Document doc = new Document();
		/** 添加字段  */
		// 一定要使用lucene工具类，将数字转为字符串(此方法节省空间，使用toString浪费内存)
		String idStr = NumericUtils.intToPrefixCoded(article.getId());
		
		// 注意：唯一标识符选择Index.ANALYZED_NO_NORMS（不分词）,唯一主键分词会产生问题（分词的话，主键会不唯一）
		doc.add(new Field("id", idStr, Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("title", article.getTitle(), Store.YES, Index.ANALYZED));
		doc.add(new Field("content", article.getContent(), Store.YES, Index.ANALYZED));
		return doc;
	}

	// 将Document转换成Article方法
	public static Article documentToArticle(Document document) {
		// 声明变量
		Article article = new Article();
		// 一定要使用lucene工具类,把字符串转为数字
		Integer id = NumericUtils.prefixCodedToInt(document.get("id"));
		// 设值
		article.setId(id);
		article.setTitle(document.get("title"));
		article.setContent(document.get("content"));
		return article;
	}

}
