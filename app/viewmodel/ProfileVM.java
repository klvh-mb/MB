package viewmodel;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import models.User;

public class ProfileVM {
	
	@JsonProperty("dn")	 public String displayName;
	@JsonProperty("ln")  public String lastName;
	@JsonProperty("fn")  public String firstName;
	@JsonProperty("a")   public String aboutMe;
	@JsonProperty("dob") public Date date_of_birth;
	@JsonProperty("gd")  public String gender;
	@JsonProperty("loc") public String location;
	@JsonProperty("n_f") public long nofriends;
	@JsonProperty("n_p") public long photos;
	@JsonProperty("i")   public long id;
	
	@JsonProperty("n_c") public long nocommunities;
	@JsonProperty("l_f") public List<User> friends;
	@JsonProperty("isf") boolean isFriend;
	@JsonProperty("isP") boolean isFriendRequestPending;
	
	public static ProfileVM profile(User user,User localUser) {
		ProfileVM vm = new ProfileVM();
		vm.displayName = user.displayName;
		vm.lastName = user.lastName;
		vm.firstName = user.firstName;
		vm.aboutMe = user.aboutMe;
		vm.date_of_birth = user.date_of_birth;
		vm.gender = user.gender;
		vm.location = user.location;
		//vm.friends = user.getFriends();
		vm.nofriends = user._getFriendsCount();
		vm.isFriend = user.isFriendOf(localUser);
		vm.isFriendRequestPending = user.isFriendRequestPendingFor(localUser);
		vm.id = user.id;
		return vm;
		
	}

}
