package com.lg.test.entity;

import java.util.List;

public class QueryResult {

	private List list; // 一部分数据
	private int count; // 总记录条数

	public QueryResult() {}

	public QueryResult(List list, int count) {
		this.list = list;
		this.count = count;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
