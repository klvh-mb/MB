package viewmodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import models.Location;
import models.PrivacySettings;
import models.User;

public class ProfileVM {
	
    @JsonProperty("dn")	 public String displayName;
    @JsonProperty("yr")  public String birthYear;
    @JsonProperty("gd")  public String gender;
    @JsonProperty("a")   public String aboutMe;
    @JsonProperty("loc") public Location location;
    @JsonProperty("n_f") public long nofriends;
    @JsonProperty("n_p") public long photos;
    @JsonProperty("id")  public long id;
    
    @JsonProperty("n_c") public long nocommunities;
    @JsonProperty("l_f") public List<User> friends;
    @JsonProperty("isf") boolean isFriend;
    @JsonProperty("isP") boolean isFriendRequestPending;
    
    @JsonProperty("isfV") boolean isFriendsVisibleToAll = true;
    @JsonProperty("isaV") boolean isActivityVisibleToAll = true;
    @JsonProperty("iscV") boolean isCommunityVisibleToAll = true;
    @JsonProperty("isdV") boolean isDetailVisibleToAll = true;

    // admin readonly fields
    @JsonProperty("mb")  public boolean mobileSignup;
    @JsonProperty("fb")  public boolean fbLogin;
    @JsonProperty("vl")  public boolean emailValidated;
    @JsonProperty("em")  public String email;
    @JsonProperty("sd")  public String signupDate;
    @JsonProperty("ll")  public String lastLogin;
    @JsonProperty("tl")  public Long totalLogin;
    @JsonProperty("qc")  public Long questionsCount;
    @JsonProperty("ac")  public Long answersCounts;
    @JsonProperty("lc")  public Long likesCount;
    @JsonProperty("wc")  public Long wantAnsCount;
    
    public static ProfileVM profile(User user, User localUser) {
        ProfileVM vm = new ProfileVM();
        vm.displayName = user.displayName;
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
        
        PrivacySettings settings = PrivacySettings.findByUserId(user.id);
        if (settings != null) {
            if(settings.showFriendListTo == 1) {
                vm.isFriendsVisibleToAll = true;
            }
            if(settings.showFriendListTo == 2) {
                vm.isFriendsVisibleToAll = false;
            }
            if(settings.showActivitiesTo == 1) {
                vm.isActivityVisibleToAll = true;
            }
            if(settings.showActivitiesTo == 2) {
                vm.isActivityVisibleToAll = false;
            }   
            if(settings.showJoinedcommunitiesTo == 1) {
                vm.isCommunityVisibleToAll = true;
            }
            if(settings.showJoinedcommunitiesTo == 2) {
                vm.isCommunityVisibleToAll = false;
            }
            if(settings.showDetailsTo == 1) {
                vm.isDetailVisibleToAll = true;
            }
            if(settings.showDetailsTo == 2) {
                vm.isDetailVisibleToAll = false;
            }
        }
        
        vm.id = user.id;

        // admin readonly fields
        if (localUser.isSuperAdmin()) {
            vm.mobileSignup = user.mobileSignup;
            vm.fbLogin = user.fbLogin;
            vm.emailValidated = user.emailValidated;
            vm.email = user.email;
            vm.signupDate = user.getCreatedDate().toString();
            vm.lastLogin = user.lastLogin.toString();
            vm.totalLogin = user.totalLogin;
            vm.questionsCount = user.questionsCount;
            vm.answersCounts = user.answersCount;
            vm.likesCount = user.likesCount;
            vm.wantAnsCount = user.wantAnsCount;
        }
        
        return vm;
    }
}
