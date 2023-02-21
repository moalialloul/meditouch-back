package models;

public class SurveyAnswersModel {
	int questionFk;
	int answerFk;

	@Override
	public String toString() {
		return "{\"questionFk\":" + questionFk + ", \"answerFk\":" + answerFk + "}";
	}

	public SurveyAnswersModel() {
		super();
	}

	public SurveyAnswersModel(int questionFk, int answerFk) {
		super();
		this.questionFk = questionFk;
		this.answerFk = answerFk;
	}

	public int getQuestionFk() {
		return questionFk;
	}

	public void setQuestionFk(int questionFk) {
		this.questionFk = questionFk;
	}

	public int getAnswerFk() {
		return answerFk;
	}

	public void setAnswerFk(int answerFk) {
		this.answerFk = answerFk;
	}
}
