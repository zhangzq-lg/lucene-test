package com.lg.test.optimize;

import org.junit.Test;

import com.lg.common.utils.LuceneUtils;
import com.lg.test.entity.Article;
import com.lg.test.idao.index.ArticleIndexDao;

/**
 * 优化测试
 * 
 * @author zzq_eason
 */
public class TestApp {

	@Test
	public void test_optimize() throws Exception {
		// 优化索引库文件（就是合并多个小文件为一个大文件）
		LuceneUtils.getIndexWriter().optimize();
	}

	@Test
	public void test_auto() throws Exception {
		// 设置个数，当文件达到一定数值自动合并（每次合并都是io操作），默认是10个，最小为2
		// 以下方便测试设置成4
		LuceneUtils.getIndexWriter().setMergeFactor(4);

		// 执行文件的变动，增加
		// 生成数据，此处应该是调用服务进行操作，在此处就省略
		Article article = new Article();
		article.setId(1);
		article.setTitle("乱七八糟的想啥");
		article.setContent("2015年1月24日，百度创始人、董事长兼CEO李彦宏在百度2014年会暨十五周年庆典上发表的主题演讲中表示，十五年来，百度坚持相信技术的力量，始终把简单可依赖的文化和人才成长机制当成最宝贵的财富");

		/**
		 * 将数据放入索引库中,其步骤： 1.把Article转为Document 2.把Document放入索引中
		 */
		new ArticleIndexDao().save(article);
	}

}
