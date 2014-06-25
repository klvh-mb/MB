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
	public Long noOfFriends;
	public int noOfGroups;
	
	public UserVM(User user) {
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.displayName = user.displayName;
		this.email = user.email;
		this.birthYear = user.userInfo.birthYear;
		this.gender = user.userInfo.gender.name();
		this.aboutMe = user.userInfo.aboutMe;
		this.location = user.userInfo.location;
		this.id = user.id;
		this.noOfFriends = user.getFriendsSize();
		this.noOfGroups = user.getListOfJoinedCommunities().size();
	}
	
	
}
