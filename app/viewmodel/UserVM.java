package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import controllers.Application;
import controllers.GameController;
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
    @JsonProperty("location") public LocationVM location;
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
    @JsonProperty("questionsCount") public Long questionsCount;
    @JsonProperty("answersCount") public Long answersCount;
    
    // signup verification
    @JsonProperty("emailValidated") public boolean emailValidated = false;
    @JsonProperty("newUser") public boolean newUser = false;
    
    // game
    @JsonProperty("enableSignInForToday") public boolean enableSignInForToday = false;
    
	public UserVM(User user) {
	    this.isMobile = Application.isMobileUser();
	    this.id = user.id;
	    this.isLoggedIn = user.isLoggedIn();
	    if (user.isLoggedIn()) {
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
    			this.location = new LocationVM(user.userInfo.location);

        		this.noOfFriends = user.getFriendsSize();
        		this.noOfGroups = user.getListOfJoinedCommunityIds().size();
        		this.isFbLogin = user.fbLogin;
        		this.isHomeTourCompleted = user.isHomeTourCompleted();
        		
    		    this.isSuperAdmin = user.isSuperAdmin();
    	        this.isBusinessAdmin = user.isBusinessAdmin();
    	        this.isCommunityAdmin = user.isCommunityAdmin();
    	        this.isEditor = user.isEditor();
    	        this.isAdmin = this.isSuperAdmin || this.isBusinessAdmin || this.isCommunityAdmin;
    	        
    	        this.enableSignInForToday = GameController.enableSignInForToday();
    		}
    		this.questionsCount = user.questionsCount;
    		this.answersCount = user.answersCount;
	    }
	    
	    this.emailValidated = user.emailValidated;
	    this.newUser = user.newUser;
	}
}
