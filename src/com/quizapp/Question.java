package com.quizapp;

public class Question {
	private int id;
	private int quizId;
	private String text;
	private String[] options;
	private int correctOption;

	public Question(int id, int quizId, String text, String[] options, int correctOption) {
		this.id = id;
		this.quizId = quizId;
		this.text = text;
		this.options = options;
		this.correctOption = correctOption;
	}

	public int getId() {
		return id;
	}

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public String getText() {
		return text;
	}

	public String[] getOptions() {
		return options;
	}

	public int getCorrectOption() {
		return correctOption;
	}

	public void updateQuestionText(String newText) {
		this.text = newText;
	}

	public void updateOptions(String[] newOptions) {
		this.options = newOptions;
	}

	public void updateCorrectOption(int newCorrectOption) {
		this.correctOption = newCorrectOption;
	}
}
