package models;

import Enums.TokenType;

public class TokenModel {
	String tokenValue;
	int userFk;
	TokenType tokenType;

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	@Override
	public String toString() {
		return "{\"tokenValue\":\"" + tokenValue + "\", \"userFk\":" + userFk + ", \"tokenType\":" + tokenType + "}";
	}

	public int getUserFk() {
		return userFk;
	}

	public void setUserFk(int userFk) {
		this.userFk = userFk;
	}
}
