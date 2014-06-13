package viewmodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import models.Location;
import models.User;

public class ProfileVM {
	
    @JsonProperty("dn")	 public String displayName;
    @JsonProperty("ln")  public String lastName;
    @JsonProperty("fn")  public String firstName;
    @JsonProperty("yr") public String birthYear;
    @JsonProperty("gd")  public String gender;
    @JsonProperty("a")   public String aboutMe;
    @JsonProperty("loc") public Location location;
    @JsonProperty("n_f") public long nofriends;
    @JsonProperty("n_p") public long photos;
    @JsonProperty("i")   public long id;
    
    @JsonProperty("n_c") public long nocommunities;
    @JsonProperty("l_f") public List<User> friends;
    @JsonProperty("isf") boolean isFriend;
    @JsonProperty("isP") boolean isFriendRequestPending;
    
    public static ProfileVM profile(User user, User localUser) {
        ProfileVM vm = new ProfileVM();
        vm.displayName = user.displayName;
        vm.lastName = user.lastName;
        vm.firstName = user.firstName;
        vm.birthYear = user.userInfo.parent_birth_year;
        vm.gender = user.userInfo.parent_gender.name();
        vm.aboutMe = user.aboutMe;
        vm.location = user.location;
        //vm.friends = user.getFriends();
        vm.nofriends = user._getFriendsCount();
        vm.isFriend = user.isFriendOf(localUser);
        vm.isFriendRequestPending = user.isFriendRequestPendingFor(localUser);
        vm.id = user.id;
        return vm;
    }
}
