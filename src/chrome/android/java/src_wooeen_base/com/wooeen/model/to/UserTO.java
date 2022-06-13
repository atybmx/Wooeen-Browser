package com.wooeen.model.to;

import java.io.Serializable;
import java.util.Date;

public class UserTO implements Serializable{

	private static final long serialVersionUID = 7187419798699492128L;

	private int id;
	private String name;
	private int status;
	private String email;
	private String emailMd5;
	private String pass;
	private String phone;
	private String timezone;
	private CountryTO country;
	private String language;
	private boolean verifiedEmail;
	private boolean verifiedPhone;
	private Date birthDate;
	private boolean recTerms;
	private String recTermsIp;
	
	public UserTO() {
	}
	public UserTO(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean getVerifiedEmail() {
		return verifiedEmail;
	}
	public void setVerifiedEmail(boolean verifiedEmail) {
		this.verifiedEmail = verifiedEmail;
	}
	public boolean getVerifiedPhone() {
		return verifiedPhone;
	}
	public void setVerifiedPhone(boolean verifiedPhone) {
		this.verifiedPhone = verifiedPhone;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public String getEmailMd5() {
		return emailMd5;
	}
	public void setEmailMd5(String emailMd5) {
		this.emailMd5 = emailMd5;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public CountryTO getCountry() {
		return country;
	}

	public void setCountry(CountryTO country) {
		this.country = country;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public boolean getRecTerms() {
		return recTerms;
	}

	public void setRecTerms(boolean recTerms) {
		this.recTerms = recTerms;
	}

	public String getRecTermsIp() {
		return recTermsIp;
	}

	public void setRecTermsIp(String recTermsIp) {
		this.recTermsIp = recTermsIp;
	}
}
