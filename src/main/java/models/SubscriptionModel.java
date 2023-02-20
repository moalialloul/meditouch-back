package models;

public class SubscriptionModel {
	int subscriptionId;
	String userEmail;

	@Override
	public String toString() {
		return "{\"subscriptionId\":" + subscriptionId + ", \"userEmail\":\"" + userEmail + "\"}";
	}

	public SubscriptionModel() {
		super();
	}

	public SubscriptionModel(int subscriptionId, String userEmail) {
		super();
		this.subscriptionId = subscriptionId;
		this.userEmail = userEmail;
	}

	public int getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(int subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}
