package models;

import java.util.ArrayList;
import java.util.List;

public class SurveyQuestionAnswersModel {
	int answerId;
	int questionFk;
	List<String> answerText = new ArrayList<String>();
	String questionText;

	@Override
	public String toString() {
		return "\"answerId\":" + answerId + ", \"questionFk\":" + questionFk + ", \"answerText\":\"" + answerText
				+ ",\"questionText\":" + questionText + "\"}";
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public SurveyQuestionAnswersModel() {
		super();
	}

	public SurveyQuestionAnswersModel(int answerId, int questionFk, List<String> answerText, String questionText) {
		super();
		this.answerId = answerId;
		this.questionFk = questionFk;
		this.answerText = answerText;
		this.questionText = questionText;
	}

	public int getAnswerId() {
		return answerId;
	}

	public void setAnswerId(int answerId) {
		this.answerId = answerId;
	}

	public int getQuestionFk() {
		return questionFk;
	}

	public void setQuestionFk(int questionFk) {
		this.questionFk = questionFk;
	}

	public List<String> getAnswerText() {
		return answerText;
	}

	public void setAnswerText(List<String> answerText) {
		this.answerText = answerText;
	}
}
