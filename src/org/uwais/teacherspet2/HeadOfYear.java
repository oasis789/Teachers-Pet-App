package org.uwais.teacherspet2;

public class HeadOfYear {

	private String name;
	private int yearGroup;
	private String email;

	public HeadOfYear(String name, int yearGroup, String email) {
		this.name = name;
		this.yearGroup = yearGroup;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public int getYearGroup() {
		return yearGroup;
	}

	public String getEmail() {
		return email;
	}

}
