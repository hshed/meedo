package com.cw.msumit.objects;

public class Connection {

	// private variables
	int id;
	String user_id;
	public String name;
	public String emails;
	//String phone_numbers;
	public int _hasWires;

	// Empty constructor
	public Connection() {

	}

	// constructor
	public Connection(String user_id, String name) {
		this.user_id = user_id;
		this.name = name;

	}

	// constructor
	public Connection(String name) {
		this.name = name;
	}

	public Connection(String user_id, String name, String emails) {

		this.user_id = user_id;
		this.name = name;
		this.emails = emails;
	}

	public Connection(String user_id, String name, int hasWires) {
		this.name = name;
		this.user_id = user_id;
		this._hasWires = hasWires;
	}

	/*public Connection(String name, String emails,
			String user_id) {
		this.name = name;
		this.emails = emails;
		//this.phone_numbers = phone_numbers;
		this.user_id = user_id;

	}*/

	/**
	 * getting ID
	 * 
	 * @return ID
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Setting ID
	 * 
	 * @param id
	 */
	public void setID(int id) {
		this.id = id;
	}

	public void setEmail(String emails) {
		this.emails = emails;

	}

	/*public void setPhones(String phones) {
		this.phone_numbers = phones;
	}*/

	/**
	 * getting User ID
	 * 
	 * @return Returns user's friend id
	 */
	public String getUserID() {
		return this.user_id;
	}

	public String getEmails() {
		return this.emails;
	}

	/*public String getPhones() {
		return this.phone_numbers;
	}*/

	/**
	 * setting User's Friend ID
	 * 
	 * @param user_id
	 */
	public void setUserID(String user_id) {
		this.user_id = user_id;
	}

	/**
	 * get Friend's Name
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * set Friend's name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public int getWires() {
		return this._hasWires;
	}

	public void setWires(int hasWires) {
		this._hasWires = hasWires;
	}

}
