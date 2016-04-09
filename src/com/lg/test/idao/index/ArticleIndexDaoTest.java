package com.lg.test.idao.index;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lg.test.entity.Article;
import com.lg.test.entity.QueryResult;

public class ArticleIndexDaoTest {

	private ArticleIndexDao articleDao = new ArticleIndexDao();

	@Test
	public void testSave() {
		// 生成数据，此处应该是调用服务进行操作，在此处就省略
		Article article = new Article();
		article.setId(0);
		article.setTitle("乱七八糟的想啥");
		article.setContent("2015年1月24日，百度创始人、董事长兼CEO李彦宏在百度2014年会暨十五周年庆典上发表的主题演讲中表示，十五年来，百度坚持相信技术的力量，始终把简单可依赖的文化和人才成长机制当成最宝贵的财富");

		/**
		 * 将数据放入索引库中,其步骤： 1.把Article转为Document 2.把Document放入索引中
		 */
		articleDao.save(article);
	}

	@Test
	public void testSave_20() {
		for (int i = 0; i < 20; i++) {
			// 生成数据，此处应该是调用服务进行操作，在此处就省略
			Article article = new Article();
			article.setId(i);
			article.setTitle("乱七八糟的想啥_" + i);
			article.setContent("2015年1月24日，百度创始人、董事长兼CEO李彦宏在百度2014年会暨十五周年庆典上发表的主题演讲中表示，十五年来，百度坚持相信技术的力量，始终把简单可依赖的文化和人才成长机制当成最宝贵的财富");

			/**
			 * 将数据放入索引库中,其步骤： 1.把Article转为Document 2.把Document放入索引中
			 */
			articleDao.save(article);
		}
	}

	@Test
	public void testDelete() {
		articleDao.delete(0);
	}

	@Test
	public void testUpdate() {
		// 生成数据，此处应该是调用服务进行操作，在此处就省略
		Article article = new Article();
		article.setId(0);
		article.setTitle("全球最大的中文搜索引擎1");
		article.setContent("1999年底，身在美国硅谷的李彦宏看到了中国互联网及中文搜索引擎，抱着技术改变世界的梦想，他毅然辞掉硅谷的高薪工作，携搜索引擎专利技术");

		articleDao.update(article);
	}

	@Test
	public void testSearch() {
		// 获取查询条件，可以使用内容试试
		String queryStr = "乱七八糟";//"全球最大的中文搜索引擎";// "乱七八糟";

		// 执行搜索
		List<Article> articleList = articleDao.search(queryStr);

		// 显示结果
		System.out.println("总结果条数：" + articleList.size());
		for (Article ar : articleList) {
			System.out.println(ar);
		}

	}

	@Test
	public void testSearch_page() {
		// 获取查询条件，可以使用内容试试
		String queryStr = "乱七八糟";// "乱七八糟	全球最大的中文搜索引擎";

		// 执行搜索
		QueryResult qr = articleDao.search(queryStr, 0, 5); // 从索引为0开始查询，最多5条
//		QueryResult qr = articleDao.search(queryStr, 5, 5);
//		QueryResult qr = articleDao.search(queryStr, 10, 5);
//		QueryResult qr = articleDao.search(queryStr, 15, 5);

		// 显示结果
		System.out.println("总结果条数：" + qr.getCount());
		for (Article ar : (List<Article>) qr.getList()) {
			System.out.println(ar);
		}

	}

}
