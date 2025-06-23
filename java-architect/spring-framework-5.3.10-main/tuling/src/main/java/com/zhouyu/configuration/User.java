package com.zhouyu.configuration;

/**
 * @author one
 * @description TODO
 * @date 2024-3-17
 */
public class User {

	private String username;

	private Integer age;

	private Car myCar;

	public User() {
	}

	public User(String username, Integer age, Car myCar) {
		this.username = username;
		this.age = age;
		this.myCar = myCar;
	}

	public String getUsername() {
		return username;
	}

	public Integer getAge() {
		return age;
	}

	public Car getMyCar() {
		return myCar;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public void setMyCar(Car myCar) {
		this.myCar = myCar;
	}
}
