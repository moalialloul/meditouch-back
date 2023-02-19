package models;

public class CommunityPostModel {
	int postId;
	int userFk;
	String postService;
	String postDescription;

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public int getUserFk() {
		return userFk;
	}

	public void setUserFk(int userFk) {
		this.userFk = userFk;
	}

	public String getPostService() {
		return postService;
	}

	public void setPostService(String postService) {
		this.postService = postService;
	}

	public String getPostDescription() {
		return postDescription;
	}

	public void setPostDescription(String postDescription) {
		this.postDescription = postDescription;
	}

	public CommunityPostModel(int postId, int userFk, String postService, String postDescription) {
		super();
		this.postId = postId;
		this.userFk = userFk;
		this.postService = postService;
		this.postDescription = postDescription;
	}

	public CommunityPostModel() {
		super();
	}

	@Override
	public String toString() {
		return "{\"postId\":" + postId + ", \"userFk\":" + userFk + ", \"postService\":\"" + postService
				+ "\", \"postDescription\":\"" + postDescription + "\"}";
	}

}
