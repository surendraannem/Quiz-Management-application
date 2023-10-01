package com.quizapp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import com.quizapp.DatabaseConnection.StudentScore;

public class Teacher {
	private int id;
	private String name;
	private DatabaseConnection databaseConnection;

	public Teacher(int id, String name) {
		this.id = id;
		this.name = name;
		this.databaseConnection = new DatabaseConnection();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void register() {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter a username: ");
		String username = scanner.nextLine();

		System.out.print("Enter a password: ");
		String password = scanner.nextLine();
		databaseConnection.registerTeacher(username, password, name);

		System.out.println("Teacher registration successful.");
	}

	public int createQuiz(String quizName) {
		try {
			Connection conn = databaseConnection.getConnection();
			String query = "INSERT INTO quizzes (teacher_id, quiz_name) VALUES (?, ?)";
			PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, this.id);
			statement.setString(2, quizName);
			statement.executeUpdate();

			ResultSet generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void addQuestion(int quizId, Question question) {
		try {
			Connection conn = databaseConnection.getConnection();
			String query = "INSERT INTO quiz_questions (quiz_id, question_text, option1, option2, option3, option4, correct_option) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, quizId);
			statement.setString(2, question.getText());
			statement.setString(3, question.getOptions()[0]);
			statement.setString(4, question.getOptions()[1]);
			statement.setString(5, question.getOptions()[2]);
			statement.setString(6, question.getOptions()[3]);
			statement.setInt(7, question.getCorrectOption());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getStudentNameById(int studentId) {
		try {
			Connection conn = databaseConnection.getConnection();
			String query = "SELECT 1name FROM students WHERE student_id = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, studentId);
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				return result.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; 
	}

	public void displayStudentScores(int studentId) {

		List<StudentScore> studentScores = databaseConnection.getStudentScores(studentId);

		if (studentScores.isEmpty()) {
			System.out.println("No scores available for this student.");
			return;
		}

		Map<Integer, Integer> quizScores = new HashMap<>();

		int totalScore = 0;
		for (StudentScore studentScore : studentScores) {
			int quizId = studentScore.getQuizId();
			int score = studentScore.getScore();
			totalScore += score;
			quizScores.put(quizId, score);
		}
		System.out.println("Student ID: " + studentId);
		System.out.println("Total Score: " + totalScore);

		for (Map.Entry<Integer, Integer> entry : quizScores.entrySet()) {
			int quizId = entry.getKey();
			int quizScore = entry.getValue();
			int totalQuestions = databaseConnection.getQuizQuestions(quizId).size();
			double percentage = (double) quizScore / totalQuestions * 100;
			String grade = databaseConnection.calculateGrade(percentage);
			String quizName = databaseConnection.getQuizNameById(quizId);

			System.out.println("Quiz: " + quizName);
			System.out.println("Quiz Score: " + quizScore);
			System.out.println("Grade: " + grade);
			System.out.println("--------------------");
		}
	}

	public List<StudentScore> getAllStudentScores() {
		List<StudentScore> scores = databaseConnection.getAllStudentScores();

		for (StudentScore score : scores) {
			String studentName = databaseConnection.getStudentNameById(score.getStudentId());
			String quizName = databaseConnection.getQuizNameById(score.getQuizId());
			int studentScore = score.getScore();
			String grade = score.getGrade();

			System.out.println("Student Name: " + studentName);
			System.out.println("Quiz Name: " + quizName);
			System.out.println("Score: " + studentScore);
			System.out.println("Grade: " + grade);
			System.out.println("--------------------");
		}

		return scores;
	}

}
   