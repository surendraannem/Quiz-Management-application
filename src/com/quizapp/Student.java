package com.quizapp;

import java.util.List;

import java.util.Scanner;

public class Student {
	private int studentId;
	private String name;
	private Scanner scanner;
	private DatabaseConnection databaseConnection;
	private int score;
	private int maxScore = 100;
	private String grade;

	public Student(int studentId, String name) {
		this.studentId = studentId;
		this.name = name;
		this.scanner = new Scanner(System.in);
		this.databaseConnection = new DatabaseConnection();
		this.score = 0;
		this.grade = "";
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public void register() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter a username: ");
		String username = scanner.nextLine();

		System.out.print("Enter a password: ");
		String password = scanner.nextLine();
		databaseConnection.registerStudent(studentId, username, password, name);

		System.out.println("Student registration successful.");
	}

	public void viewProfile() {
		System.out.println("Student ID: " + studentId);
		System.out.println("Name: " + name);
		System.out.println("Score: " + score);
	}

	public void updateScoreAndGrade(int score, String grade) {
		this.score = score;
		this.grade = grade;
	}

	public void updateScore(int score) {
		this.score = score;
	}

	public int takeQuiz() {
		List<Quiz> availableQuizzes = databaseConnection.getAvailableQuizzes();

		if (availableQuizzes.isEmpty()) {
			System.out.println("No quizzes available.");
			return 0;
		}

		System.out.println("Available Quizzes:");
		for (int i = 0; i < availableQuizzes.size(); i++) {
			System.out.println((i + 1) + ". " + availableQuizzes.get(i).getName());
		}

		System.out.print("Enter the number of the quiz you want to take: ");
		int selectedQuizIndex = scanner.nextInt();
		scanner.nextLine();

		if (selectedQuizIndex >= 1 && selectedQuizIndex <= availableQuizzes.size()) {
			Quiz selectedQuiz = availableQuizzes.get(selectedQuizIndex - 1);
			List<Question> quizQuestions = databaseConnection.getQuizQuestions(selectedQuiz.getId());

			int score = 0;

			for (Question question : quizQuestions) {
				System.out.println("Question: " + question.getText());

				String[] options = question.getOptions();
				for (int i = 0; i < options.length; i++) {
					System.out.println((i + 1) + ". " + options[i]);
				}

				System.out.print("Enter your answer (1-" + options.length + "): ");
				int userAnswer = scanner.nextInt();
				scanner.nextLine();

				if (userAnswer == question.getCorrectOption()) {
					System.out.println("Correct!");
					score++;
				} else {
					System.out.println("Incorrect. The correct answer is: " + options[question.getCorrectOption() - 1]);
				}
			}

			System.out.println("Quiz completed. Your score is: " + score);

			double percentageScore = (double) score / quizQuestions.size() * 100;
			System.out.println("Percentage Score: " + percentageScore);

			String grade = databaseConnection.calculateGrade(percentageScore);
			System.out.println("Grade: " + grade);

			updateScoreAndGrade(score, grade);

			databaseConnection.saveStudentScore(studentId, selectedQuiz.getId(), score);

			return score;
		} else {
			System.out.println("Invalid quiz selection.");
			return 0;
		}
	}
}