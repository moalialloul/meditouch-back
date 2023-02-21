package models;

public class SurveyModel {
	int surveyId;
	String surveyName;
	String surveyDescription;

	@Override
	public String toString() {
		return "{\"surveyId\":" + surveyId + ", \"surveyName\":\"" + surveyName + "\", \"surveyDescription\":\""
				+ surveyDescription + "\"}";
	}

	public SurveyModel(int surveyId, String surveyName, String surveyDescription) {
		super();
		this.surveyId = surveyId;
		this.surveyName = surveyName;
		this.surveyDescription = surveyDescription;
	}

	public SurveyModel() {
		super();
	}

	public int getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}

	public String getSurveyName() {
		return surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public String getSurveyDescription() {
		return surveyDescription;
	}

	public void setSurveyDescription(String surveyDescription) {
		this.surveyDescription = surveyDescription;
	}
}
