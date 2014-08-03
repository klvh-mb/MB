package viewmodel;

import java.util.Date;

import common.collection.Pair;
import models.Community;
import models.Community.CommunityType;
import models.TargetingSocialObject;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityVM {
	@JsonProperty("lu") public Long loggedUserId;
	@JsonProperty("lun") public String loggedUserName;
	
	@JsonProperty("n") public String name;
	@JsonProperty("d") public String description;
	@JsonProperty("nom") public int noOfMembers;
	@JsonProperty("typ") public CommunityType communityType;
	@JsonProperty("ttyp") public TargetingSocialObject.TargetingType targetingType;
	@JsonProperty("icon") public String icon;
	@JsonProperty("dte") public Date createDate;
	@JsonProperty("td") public String tagetDistrict;
	@JsonProperty("id") public long id;
	@JsonProperty("sys") public boolean system;
	@JsonProperty("isM") public boolean isMember;
	@JsonProperty("isP") public boolean isRequested;
	@JsonProperty("isO") public boolean isOwner;
	@JsonProperty("adminP") public boolean adminPostOnly;
	
	public static CommunityVM communityVM(Community c, User user) {
        Pair<Boolean, Boolean> memStatus = c.getMemberStatusForUser(user.id);
        Long memCount = c.getMemberCount();

		CommunityVM vm = new CommunityVM();
		vm.loggedUserId = user.id;
		vm.loggedUserName = user.displayName;
		
		vm.name = c.name;
		vm.description = c.description;
		vm.communityType = c.communityType;
		vm.targetingType = c.targetingType;
		vm.icon = c.icon;
		vm.tagetDistrict = c.tagetDistrict;
		vm.createDate = c.createDate;
		vm.id = c.id;
		vm.system = c.system;
		vm.noOfMembers = memCount.intValue();
		
		//TODO Logic required
		vm.isRequested = memStatus.first;
        vm.isMember = memStatus.second;
		vm.isOwner = (user == c.owner) ? true : false;
		vm.adminPostOnly = c.adminPostOnly;
		
		return vm;
	}
}
