package viewmodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import models.User;

public class ProfileVM {
	
	@JsonProperty("dn")	 public String displayName;
	@JsonProperty("ln")  public String lastName;
	@JsonProperty("fn")  public String firstName;
	@JsonProperty("a")   public String aboutMe;
	@JsonProperty("n_f") public long nofriends;
	@JsonProperty("n_p") public long photos;
	@JsonProperty("i")   public long id;
	@JsonProperty("n_c") public long nocommunities;
	@JsonProperty("l_f") public List<User> friends;
	@JsonProperty("isf") boolean isFriend;
	
	
	public static ProfileVM profile(User user,User localUser) {
		ProfileVM vm = new ProfileVM();
		vm.displayName = user.displayName;
		vm.lastName = user.lastName;
		vm.firstName = user.firstName;
		vm.aboutMe = user.aboutMe;
		//vm.friends = user.getFriends();
		vm.nofriends = user._getFriendsCount();
		vm.isFriend = user.isFriendOf(localUser);
		vm.id = user.id;
		return vm;
		
	}

}
