package com.quizapp;

public class Quiz {
	private int id;
	private String name;

	public Quiz(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}