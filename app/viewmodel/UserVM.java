package viewmodel;

import models.Location;
import models.User;

public class UserVM {
	public String firstName;
	public String lastName;
	public String displayName;
	public String email;
	public String birthYear;
	public String gender;
	public String aboutMe;
	public Location location;
	public Long id;
	public int noOfFriends;
	public int noOfGroups;
	
	public UserVM(User user) {
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.displayName = user.displayName;
		this.email = user.email;
		this.birthYear = user.userInfo.parent_birth_year;
		this.gender = user.userInfo.parent_gender.name();
		this.aboutMe = user.aboutMe;
		this.location = user.location;
		this.id = user.id;
		this.noOfFriends = user.getFriends().size();
		this.noOfGroups = user.getListOfJoinedCommunities().size();
	}
	
	
}
