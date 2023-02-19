package models;

public class BlockModel {
	int blockId;
	int businessAccountFk;
	int userFk;

	@Override
	public String toString() {
		return "{\"blockId\":" + blockId + ", \"businessAccountFk\":" + businessAccountFk + ", \"userFk\":" + userFk
				+ "}";
	}

	public BlockModel(int blockId, int businessAccountFk, int userFk) {
		super();
		this.blockId = blockId;
		this.businessAccountFk = businessAccountFk;
		this.userFk = userFk;
	}

	public BlockModel() {

	}

	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
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
