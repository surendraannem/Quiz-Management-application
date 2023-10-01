package com.quizapp;

import java.sql.*;
import java.util.Scanner;

public class QuizApp {
	private static Scanner scanner = new Scanner(System.in);
	private static DatabaseConnection quizDatabase = new DatabaseConnection();

	public static void main(String[] args) {
		System.out.println("Welcome to the Quiz Management App");
		System.out.println("----------------------------------");

		while (true) {
			System.out.println("1. Student Login");
			System.out.println("2. Teacher Login");
			System.out.println("3. Exit");
			System.out.print("Enter your choice: ");
			int choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			case 1:
				studentLogin();
				break;
			case 2:
				teacherLogin();
				break;
			case 3:
				System.out.println("Exiting the application. Goodbye!");
				quizDatabase.closeConnection();
				System.exit(0);
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void studentLogin() {
		System.out.print("Enter your student ID: ");
		int studentId = scanner.nextInt();
		scanner.nextLine(); 

		Student student = quizDatabase.getStudentById(studentId);
		if (student != null) {
            System.out.println("Welcome, " + student.getName() + "!");
            System.out.println("1. Take Quiz");
            System.out.println("2. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    student.takeQuiz();
                    break;
                case 2:
                    return;
                    
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
		} else {
			System.out.println("Student ID not found.");
			System.out.print("Do you want to register? (yes/no): ");
			String registerChoice = scanner.nextLine();

			if (registerChoice.equalsIgnoreCase("yes")) {
				Student newStudent = registerNewStudent();
				if (newStudent != null) {
					System.out.println("Welcome, " + newStudent.getName() + "!");
					newStudent.takeQuiz();
				} else {
					System.out.println("Student registration failed.");
				}
			}
		}
	}

	private static Student registerNewStudent() {
		System.out.print("Enter your name: ");
		String name = scanner.nextLine();
		Student newStudent = new Student(0, name);
		newStudent.register();
		return newStudent;
	}

	private static void teacherLogin() {
		System.out.print("Enter your teacher ID: ");
		int teacherId = scanner.nextInt();
		scanner.nextLine();

		Teacher teacher = quizDatabase.getTeacherById(teacherId);

		if (teacher != null) {
			System.out.println("Welcome, " + teacher.getName() + "!");
			teacherActions(teacher);
		} else {
			System.out.println("Teacher ID not found.");
			System.out.print("Do you want to register? (yes/no): ");
			String registerChoice = scanner.nextLine();

			if (registerChoice.equalsIgnoreCase("yes")) {
				Teacher newTeacher = registerNewTeacher();
				if (newTeacher != null) {
					System.out.println("Welcome, " + newTeacher.getName() + "!");
					teacherActions(newTeacher);
				} else {
					System.out.println("Teacher registration failed.");
				}
			}
		}
	}

	private static Teacher registerNewTeacher() {
		System.out.print("Enter your name: ");
		String name = scanner.nextLine();

		Teacher newTeacher = new Teacher(0, name);
		newTeacher.register();
		return newTeacher;
	}

	private static void teacherActions(Teacher teacher) {
	    while (true) {
	        System.out.println("1. Add a question");
	        System.out.println("2. Create a quiz");
	        System.out.println("3. View student scores");
	        System.out.println("4. Logout");
	        System.out.print("Enter your choice: ");
	        int choice = scanner.nextInt();

	        switch (choice) {
	            case 1:
	                System.out.print("Enter the quiz ID: ");
	                int quizId = scanner.nextInt();
	                scanner.nextLine();
	                addQuestion(teacher, quizId);
	                break;
	            case 2:
	                createQuiz(teacher);
	                break;
	            case 3:
	                teacher.getAllStudentScores();
	                break;
	            case 4:
	                return;
	            default:
	                System.out.println("Invalid choice. Please try again.");
	        }
	    }
	}

	private static void createQuiz(Teacher teacher) {
		scanner.nextLine(); 
		System.out.print("Enter the name of the quiz: ");
		String quizName = scanner.nextLine();

		int quizId = teacher.createQuiz(quizName);

		if (quizId != -1) {
			System.out.println("Quiz created successfully with ID: " + quizId);

			while (true) {
				System.out.println("1. Add a question");
				System.out.println("2. Finish creating quiz");
				System.out.print("Enter your choice: ");
				int choice = scanner.nextInt();

				switch (choice) {
				case 1:
					addQuestion(teacher, quizId);
					break;
				case 2:
					return;
				default:
					System.out.println("Invalid choice. Please try again.");
				}
			}
		} else {
			System.out.println("Quiz creation failed.");
		}
	}

	private static void addQuestion(Teacher teacher, int quizId) {
		System.out.print("Enter the question text: ");
		String questionText = scanner.nextLine();

		System.out.println("Enter the options (comma-separated): ");
		String optionsInput = scanner.nextLine();
		String[] options = optionsInput.split(",");

		System.out.print("Enter the correct option (1-" + options.length + "): ");
		int correctOption = scanner.nextInt();
		scanner.nextLine();

		Question question = new Question(0, quizId, questionText, options, correctOption);
		teacher.addQuestion(quizId, question);

		System.out.println("Question added to the quiz successfully!");
	}
	
	public static int getQuizIdFromIdentifier(String quizIdentifier) {
		try {
			Connection conn = quizDatabase.getConnection();
			String query = "SELECT id FROM quizzes WHERE quiz_name = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, quizIdentifier);
			ResultSet result = statement.executeQuery();

			if (result.next()) {
				
				return result.getInt("quiz_id");
			} else {
				
				return -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1; 
		}
	}
}
