package models;

import java.sql.Timestamp;

public class BlogModel {
	int blogId;
	String blogType;
	Timestamp blogDate;
	String blogTitle;
	String blogUrl;

	@Override
	public String toString() {
		return "{\"blogId\":" + blogId + ", \"blogType\":\"" + blogType + "\", \"blogDate\":\"" + blogDate
				+ "\", \"blogTitle\":\"" + blogTitle + "\", \"blogUrl\":" + blogUrl + "}";
	}

	public BlogModel() {
		super();
	}

	public BlogModel(int blogId, String blogType, Timestamp blogDate, String blogTitle, String blogUrl) {
		super();
		this.blogId = blogId;
		this.blogType = blogType;
		this.blogDate = blogDate;
		this.blogTitle = blogTitle;
		this.blogUrl = blogUrl;
	}

	public int getBlogId() {
		return blogId;
	}

	public void setBlogId(int blogId) {
		this.blogId = blogId;
	}

	public String getBlogType() {
		return blogType;
	}

	public void setBlogType(String blogType) {
		this.blogType = blogType;
	}

	public Timestamp getBlogDate() {
		return blogDate;
	}

	public void setBlogDate(Timestamp blogDate) {
		this.blogDate = blogDate;
	}

	public String getBlogTitle() {
		return blogTitle;
	}

	public void setBlogTitle(String blogTitle) {
		this.blogTitle = blogTitle;
	}

	public String getBlogUrl() {
		return blogUrl;
	}

	public void setBlogUrl(String blogUrl) {
		this.blogUrl = blogUrl;
	}
}
