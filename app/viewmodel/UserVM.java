package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import controllers.Application;
import models.Icon;
import models.Location;
import models.User;

public class UserVM {
    @JsonProperty("id") public Long id;
    @JsonProperty("firstName") public String firstName;
    @JsonProperty("lastName") public String lastName;
    @JsonProperty("displayName") public String displayName;
    @JsonProperty("email") public String email;
    @JsonProperty("birthYear") public String birthYear;
    @JsonProperty("gender") public String gender;
    @JsonProperty("aboutMe") public String aboutMe;
    @JsonProperty("location") public Location location;
    @JsonProperty("noOfFriends") public Long noOfFriends;
    @JsonProperty("noOfGroups") public int noOfGroups;
    @JsonProperty("isLoggedIn") public boolean isLoggedIn = false;
    @JsonProperty("isSA") public boolean isSuperAdmin = false;
    @JsonProperty("isBA") public boolean isBusinessAdmin = false;
    @JsonProperty("isCA") public boolean isCommunityAdmin = false;
    @JsonProperty("isE") public boolean isEditor = false;
    @JsonProperty("isAdmin") public boolean isAdmin = false;
    @JsonProperty("isMobile") public boolean isMobile = false;
    @JsonProperty("isFbLogin") public boolean isFbLogin = false;
    @JsonProperty("isHomeTourCompleted") public boolean isHomeTourCompleted = false;
    
    // game stats
    @JsonProperty("gameLevel") public int gameLevel = 8;
    @JsonProperty("gameLevelIcon") public String gameLevelIcon = Icon.getGameLevelIcon(gameLevel).url;
    @JsonProperty("gamePoints") public int gamePoints = 100;
    
	public UserVM(User user) {
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.displayName = user.displayName;
		this.email = user.email;
		if(user.userInfo != null) {
			this.birthYear = user.userInfo.birthYear;
			if(user.userInfo.gender != null) {
				this.gender = user.userInfo.gender.name();
			}
			this.aboutMe = user.userInfo.aboutMe;
			this.location = user.userInfo.location;
		}
		this.id = user.id;
		this.noOfFriends = user.getFriendsSize();
		this.noOfGroups = user.getListOfJoinedCommunityIds().size();
		this.isLoggedIn = user.isLoggedIn();
		if (this.isLoggedIn) {
		    this.isSuperAdmin = user.isSuperAdmin();
	        this.isBusinessAdmin = user.isBusinessAdmin();
	        this.isCommunityAdmin = user.isCommunityAdmin();
	        this.isEditor = user.isEditor();
	        this.isAdmin = this.isSuperAdmin || this.isBusinessAdmin || this.isCommunityAdmin;    
		}
		this.isMobile = Application.isMobileUser();
		this.isFbLogin = user.fbLogin;
		this.isHomeTourCompleted = user.isHomeTourCompleted();
	}
}
