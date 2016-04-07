package com.lg.test01.entity;

/**
 * 实体类
 * 
 * @author zzq_eason
 */
public class Article {

	private Integer id;
	private String title;
	private String content;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Article [id=" + id + ", title=" + title + ", content="
				+ content + "]";
	}

}
