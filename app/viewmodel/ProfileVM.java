package viewmodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import models.Location;
import models.Privacy;
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
    @JsonProperty("id")   public long id;
    
    @JsonProperty("n_c") public long nocommunities;
    @JsonProperty("l_f") public List<User> friends;
    @JsonProperty("isf") boolean isFriend;
    @JsonProperty("isP") boolean isFriendRequestPending;
    @JsonProperty("isfV") boolean isFriendsVisibleToAll;
    @JsonProperty("isaV") boolean isActivityVisibleToAll;
    @JsonProperty("iscV") boolean isCommunityVisibleToAll;
    @JsonProperty("isdV") boolean isDetailVisibleToAll;
    
    public static ProfileVM profile(User user, User localUser) {
        ProfileVM vm = new ProfileVM();
        vm.displayName = user.displayName;
        vm.lastName = user.lastName;
        vm.firstName = user.firstName;
        if(user.userInfo != null) {
        	vm.birthYear = user.userInfo.birthYear;
			if(user.userInfo.gender != null) {
				vm.gender = user.userInfo.gender.name();
			}
			vm.aboutMe = user.userInfo.aboutMe;
			vm.location = user.userInfo.location;
		}
        //vm.friends = user.getFriends();
        vm.nofriends = user.getFriendsSize();
        vm.isFriend = user.isFriendOf(localUser);
        vm.isFriendRequestPending = user.isFriendRequestPendingFor(localUser);
        Privacy privacy = Privacy.findByUserId(user.id);
        if(privacy.showFriendListTo == 1) {
        	vm.isFriendsVisibleToAll = true;
        }
        if(privacy.showFriendListTo == 2) 
        {
        	vm.isFriendsVisibleToAll = false;
        }
        if(privacy.showActivitiesTo == 1) {
        	vm.isActivityVisibleToAll = true;
        }
        if(privacy.showActivitiesTo == 2) 
        {
        	vm.isActivityVisibleToAll = false;
        }	
        if(privacy.showJoinedcommunitiesTo == 1) {
        	vm.isCommunityVisibleToAll = true;
        }
        if(privacy.showJoinedcommunitiesTo == 2) 
        {
        	vm.isCommunityVisibleToAll = false;
        }
        if(privacy.showDetailsTo == 1) {
        	vm.isDetailVisibleToAll = true;
        }
        if(privacy.showDetailsTo == 2) 
        {
        	vm.isDetailVisibleToAll = false;
        }
        vm.id = user.id;
        return vm;
    }
}
