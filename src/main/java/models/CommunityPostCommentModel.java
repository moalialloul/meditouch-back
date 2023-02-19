package models;

public class CommunityPostCommentModel {
	int commentId;
	int userFk;
	int postFk;
	String commentDescription;

	@Override
	public String toString() {
		return "{\"commentId\":" + commentId + ", \"userFk\":" + userFk + ", \"postFk\":" + postFk
				+ ", \"commentDescription\":\"" + commentDescription + "\"}";
	}

	public CommunityPostCommentModel() {
	}

	public CommunityPostCommentModel(int commentId, int userFk, int postFk, String commentDescription) {
		super();
		this.commentId = commentId;
		this.userFk = userFk;
		this.postFk = postFk;
		this.commentDescription = commentDescription;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public int getUserFk() {
		return userFk;
	}

	public void setUserFk(int userFk) {
		this.userFk = userFk;
	}

	public int getPostFk() {
		return postFk;
	}

	public void setPostFk(int postFk) {
		this.postFk = postFk;
	}

	public String getCommentDescription() {
		return commentDescription;
	}

	public void setCommentDescription(String commentDescription) {
		this.commentDescription = commentDescription;
	}
}
