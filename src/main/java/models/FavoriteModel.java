package models;

public class FavoriteModel {
	int favoriteId;
	int businessAccountFk;
	int userFk;

	public FavoriteModel(int favoriteId, int businessAccountFk, int userFk) {
		super();
		this.favoriteId = favoriteId;
		this.businessAccountFk = businessAccountFk;
		this.userFk = userFk;
	}

	public FavoriteModel() {

	}

	@Override
	public String toString() {
		return "{\"favoriteId\":" + favoriteId + ", \"businessAccountFk\":" + businessAccountFk + ", \"userFk\":"
				+ userFk + "}";
	}

	public int getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(int favoriteId) {
		this.favoriteId = favoriteId;
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
}
