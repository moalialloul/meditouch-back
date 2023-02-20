package models;

public class ContactUsModel {
	int contactId;
	String firstName;
	String lastName;
	String subject;
	String message;

	@Override
	public String toString() {
		return "{\"contactId\":" + contactId + ", \"firstName\":\"" + firstName + "\", \"lastName\":\"" + lastName
				+ "\", \"subject\":\"" + subject + "\", \"message\":\"" + message + "\"}";
	}

	public ContactUsModel() {
		super();
	}

	public ContactUsModel(int contactId, String firstName, String lastName, String subject, String message) {
		super();
		this.contactId = contactId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.subject = subject;
		this.message = message;
	}

	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
