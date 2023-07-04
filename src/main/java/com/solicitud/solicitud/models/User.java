package com.solicitud.solicitud.models;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(	name = "user_")
public class User {
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userid = (long) 9999999;

	@NotBlank
	@Column(length = 75, columnDefinition = "nvarchar")
	private String screenname;

	@NotBlank
	@Column(length = 75, columnDefinition = "nvarchar")
	@Email
	private String emailaddress;

	@NotBlank
	@Column(length = 75, columnDefinition = "nvarchar")
	@JsonIgnore
	private String password_;
	
	@NotBlank
	@Column(length = 255, columnDefinition = "nvarchar")
	private String greeting;
	
	public User() {}

	public User(Long userid, String screenname,  String emailaddress, String password_, String greeting) {
        super();
        this.userid = userid;
		this.screenname = screenname;
		this.emailaddress = emailaddress;
		this.password_ = password_;
		this.greeting = greeting;
	}

	public Long getUserid() {
		return userid;
	}

	public String getScreenname() {
		return screenname;
	}

	public String getEmailaddress() {
		return emailaddress;
	}

	public String getPassword_() {
		return password_;
	}

	public String getGreeting() {
		return greeting;
	}

    public void setUserId(Long userId) {
        this.userid = userId;
    }

    public void setScreenname(String screenName) {
        this.screenname = screenName;
    }

    public void setEmailaddress(String Emailaddress) {
        this.emailaddress = Emailaddress;
    }

    public void setPassword_(String Password_) {
        this.password_ = Password_;
    }

    public void setGreeting(String Greeting) {
        this.greeting = Greeting;
    }
}
