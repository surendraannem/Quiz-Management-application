package com.quizapp;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_db";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "root";

	private Connection connection;

	public DatabaseConnection() {
		try {
			connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Student getStudentById(int studentId) {
		Student student = null;
		try {
			Connection conn = getConnection();
			String query = "SELECT * FROM students WHERE student_id = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, studentId);
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				String name = result.getString("name");
				student = new Student(studentId, name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return student;
	}

	public Teacher getTeacherById(int teacherId) {
		Teacher teacher = null;
		try {
			Connection conn = getConnection();
			String query = "SELECT * FROM teachers WHERE id = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, teacherId);
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				String name = result.getString("name");
				teacher = new Teacher(teacherId, name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return teacher;
	}

	public boolean validateStudentCredentials(String username, String password) {
		boolean isValid = false;
		try {
			Connection conn = getConnection();
			String query = "SELECT * FROM students WHERE username = ? AND password = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				isValid = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isValid;
	}

	public boolean validateTeacherCredentials(String username, String password) {
		boolean isValid = false;
		try {
			Connection conn = getConnection();
			String query = "SELECT * FROM teachers WHERE username = ? AND password = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				isValid = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isValid;
	}

	public void registerStudent(int studentId, String username, String password, String name) {
		try {
			Connection conn = getConnection();
			String query = "INSERT INTO students (username, password, name) VALUES (?, ?, ?)";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, name);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int generateUniqueStudentId() {
		int uniqueStudentId = -1;

		try {
			String sql = "SELECT MAX(student_id) + 1 FROM students";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				uniqueStudentId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return uniqueStudentId;
	}

	public void registerTeacher(String username, String password, String name) {
		try {
			Connection conn = getConnection();
			String query = "INSERT INTO teachers (username, password, name) VALUES (?, ?, ?)";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, name);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Question> getQuizQuestions(int quiz_Id) {
		List<Question> questions = new ArrayList<>();

		try {
			Connection conn = getConnection();
			String query = "SELECT * FROM quiz_questions WHERE quiz_id = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, quiz_Id);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				int id = result.getInt("id");
				String text = result.getString("question_text");
				String option1 = result.getString("option1");
				String option2 = result.getString("option2");
				String option3 = result.getString("option3");
				String option4 = result.getString("option4");
				int correctOption = result.getInt("correct_option");

				Question question = new Question(id, quiz_Id, text, new String[] { option1, option2, option3, option4 },
						correctOption);

				questions.add(question);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return questions;
	}

	public List<Quiz> getAvailableQuizzes() {
		List<Quiz> availableQuizzes = new ArrayList<>();

		try {
			Connection conn = getConnection();
			String query = "SELECT quiz_id, quiz_name FROM quizzes";
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				int quizId = result.getInt("quiz_id");
				String quizName = result.getString("quiz_name");
				Quiz quiz = new Quiz(quizId, quizName);
				availableQuizzes.add(quiz);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return availableQuizzes;
	}

	public String calculateGrade(double percentage) {
		if (percentage >= 90) {
			return "A";
		} else if (percentage >= 80) {
			return "B";
		} else if (percentage >= 70) {
			return "C";
		} else if (percentage >= 60) {
			return "D";
		} else {
			return "F";
		}
	}

	public double calculatePercentage(int score, int maxScore) {
		if (maxScore <= 0) {
			throw new IllegalArgumentException("maxScore must be greater than 0");
		}

		return ((double) score / maxScore) * 100.0;
	}

	public void saveStudentScore(int studentId, int quiz_id, int score) {
		try {
			Connection conn = getConnection();

			if (studentExists(studentId)) {

				int totalQuestions = getQuizQuestions(quiz_id).size();

				double percentage = (double) score / totalQuestions * 100;

				String grade = calculateGrade(percentage);

				String selectQuery = "SELECT * FROM scores WHERE student_id = ? AND quiz_id = ?";
				PreparedStatement selectStatement = conn.prepareStatement(selectQuery);
				selectStatement.setInt(1, studentId);
				selectStatement.setInt(2, quiz_id);
				ResultSet selectResult = selectStatement.executeQuery();

				if (selectResult.next()) {

					String updateQuery = "UPDATE scores SET score = ?, grade = ? WHERE student_id = ? AND quiz_id = ?";
					PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
					updateStatement.setInt(1, score); // Set score as the first parameter
					updateStatement.setString(2, grade);
					updateStatement.setInt(3, studentId);
					updateStatement.setInt(4, quiz_id);
					updateStatement.executeUpdate();
					System.out.println("Score updated successfully.");
				} else {

					String insertQuery = "INSERT INTO scores (student_id, quiz_id, score, grade) VALUES (?, ?, ?, ?)";
					PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
					insertStatement.setInt(1, studentId);
					insertStatement.setInt(2, quiz_id);
					insertStatement.setInt(3, score);
					insertStatement.setString(4, grade);
					insertStatement.executeUpdate();
					System.out.println("Score saved successfully.");
				}
			} else {
				System.out.println("Student ID not found. Score not saved.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean studentExists(int studentId) {
		try {
			Connection conn = getConnection();
			String query = "SELECT student_id FROM students WHERE student_id = ?"; // Updated column name
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, studentId);
			ResultSet result = statement.executeQuery();
			return result.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void addQuestion(Question question) {
		try {
			Connection conn = getConnection();
			String query = "INSERT INTO questions (text, option1, option2, option3, option4, correct_option) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, question.getText());
			statement.setString(2, question.getOptions()[0]);
			statement.setString(3, question.getOptions()[1]);
			statement.setString(4, question.getOptions()[2]);
			statement.setString(5, question.getOptions()[3]);
			statement.setInt(6, question.getCorrectOption());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<StudentScore> getStudentScores(int studentId) {
		List<StudentScore> studentScores = new ArrayList<>();

		try (Connection conn = getConnection()) {
			String query = "SELECT * FROM student_quiz_scores WHERE student_id = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, studentId);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				int scoreId = result.getInt("score_id");
				int quizId = result.getInt("quiz_id");
				int score = result.getInt("score");
				String grade = result.getString("grade");

				StudentScore studentScore = new StudentScore(scoreId, studentId, quizId, score, grade);
				studentScores.add(studentScore);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return studentScores;
	}

	public List<StudentScore> getAllStudentScores() {
		List<StudentScore> scores = new ArrayList<>();
		try {
			Connection conn = getConnection();
			String query = "SELECT * FROM scores";
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				int scoreId = result.getInt("score_id");
				int studentId = result.getInt("student_id");
				int quizId = result.getInt("quiz_id");
				int score = result.getInt("score");
				String grade = result.getString("grade");

				StudentScore studentScore = new StudentScore(scoreId, studentId, quizId, score, grade);
				scores.add(studentScore);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return scores;
	}

	public String getStudentNameById(int studentId) {
		String studentName = null;
		try {
			Connection conn = getConnection();
			String query = "SELECT name FROM students WHERE student_id = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, studentId);
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				studentName = result.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentName;
	}

	public String getQuizNameById(int quizId) {
		String quizName = null;
		try {
			Connection conn = getConnection();
			String query = "SELECT quiz_name FROM quizzes WHERE quiz_id = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, quizId);
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				quizName = result.getString("quiz_name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return quizName;
	}

	public void addSampleQuestions() {
		try {
			Connection conn = getConnection();
			String query = "INSERT INTO questions (text, option1, option2, option3, option4, correct_option) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = conn.prepareStatement(query);

			addQuestion(statement, "Sharks are mammals.", "Yes", "No", null, null, 1);
			addQuestion(statement, "Sea otters have a favorite rock they use to break open food.", "Yes", "No", null,
					null, 0);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addQuestion(PreparedStatement statement, String text, String option1, String option2, String option3,
			String option4, int correctOption) {
		try {
			statement.setString(1, text);
			statement.setString(2, option1);
			statement.setString(3, option2);
			statement.setString(4, option3);
			statement.setString(5, option4);
			statement.setInt(6, correctOption);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static class StudentScore {
		private int scoreId;
		private int studentId;
		private int quizId;
		private int score;
		private String grade;

		public StudentScore(int scoreId, int studentId, int quizId, int score, String grade) {
			this.scoreId = scoreId;
			this.studentId = studentId;
			this.quizId = quizId;
			this.score = score;
			this.grade = grade;
		}

		public int getScoreId() {
			return scoreId;
		}

		public void setScoreId(int scoreId) {
			this.scoreId = scoreId;
		}

		public int getStudentId() {
			return studentId;
		}

		public void setStudentId(int studentId) {
			this.studentId = studentId;
		}

		public int getQuizId() {
			return quizId;
		}

		public void setQuizId(int quizId) {
			this.quizId = quizId;
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
	}

}