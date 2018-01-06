package pl.net.oth.smartbillions.model;

import java.io.Serializable;

public class SMSMessage implements Serializable{
	private String phoneNumber;
	private String text;
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
