package viewmodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Lob;

import play.data.format.Formats;

import models.User;

public class UserVM {
	public String firstName;
	public String lastName;
	public String displayName;
	public String username;
	public String email;
	public Date date_of_birth;
	public String gender;
	public String aboutMe;
	public String location;
	public Long id;
	public int noOfFriends;
	public int noOfGroups;
	
	public UserVM(User user) {
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.displayName = user.displayName;
		this.username = user.username;
		this.email = user.email;
		this.date_of_birth = user.date_of_birth;
		this.gender = user.gender;
		this.aboutMe = user.aboutMe;
		this.location = user.location;
		this.id = user.id;
		this.noOfFriends = user.getFriends().size();
		this.noOfGroups = user.getListOfJoinedCommunities().size();
		
		
		
	}
	
	
}
