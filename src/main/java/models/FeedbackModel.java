package models;

public class FeedbackModel {
	int feedbackId;
	int businessAccountFk;
	int userFk;
	String feedbackDescription;

	public FeedbackModel(int feedbackId, int businessAccountFk, int userFk, String feedbackDescription) {
		super();
		this.feedbackId = feedbackId;
		this.businessAccountFk = businessAccountFk;
		this.userFk = userFk;
		this.feedbackDescription = feedbackDescription;
	}

	public FeedbackModel() {

	}

	@Override
	public String toString() {
		return "{\"feedbackId\":" + feedbackId + ", \"businessAccountFk\":" + businessAccountFk + ", \"userFk\":"
				+ userFk + ", \"feedbackDescription\":\"" + feedbackDescription + "\"}";
	}

	public int getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
	}

	public int getBusinessAccountFk() {
		return businessAccountFk;
	}

	public void setBusinessAccountFk(int businessAccountFk) {
		this.businessAccountFk = businessAccountFk;
	}

	public int getUserFk() {
		return userFk;
	}

	public void setUserFk(int userFk) {
		this.userFk = userFk;
	}

	public String getFeedbackDescription() {
		return feedbackDescription;
	}

	public void setFeedbackDescription(String feedbackDescription) {
		this.feedbackDescription = feedbackDescription;
	}

	
}
