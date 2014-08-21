package viewmodel;

import common.collection.Pair;

import org.codehaus.jackson.annotate.JsonProperty;

import models.Community;
import models.TargetingSocialObject;
import models.User;

public class CommunitiesWidgetChildVM {
    @JsonProperty("id") public Long id;
    @JsonProperty("gi") public String icon;
    @JsonProperty("mm") public Long membersCount;
    @JsonProperty("dn") public String name;
    @JsonProperty("msg") public String desc;
    @JsonProperty("sys") public Boolean system;
    @JsonProperty("isO") public Boolean isO;
    @JsonProperty("isP") public Boolean isP;
    @JsonProperty("isM") public Boolean isM;
    @JsonProperty("tp") public String type;
    @JsonProperty("ttyp") public TargetingSocialObject.TargetingType targetingType;
    @JsonProperty("tinfo") public String targetingInfo;
	
	public CommunitiesWidgetChildVM(Community community, User user) {
        Pair<Boolean, Boolean> memStatus = community.getMemberStatusForUser(user.id);
        Long memCount = community.getMemberCount();

        this.id = community.id;
        this.name = community.name;
        this.membersCount = memCount;
        this.desc = community.description;
        this.icon = community.icon;
        this.type = community.communityType.name();
        this.targetingType = community.targetingType;
        this.targetingInfo = community.targetingInfo;
        this.system = community.system;
        this.isO = user == community.owner;
        this.isP = memStatus.first;
        this.isM = memStatus.second;
    }
	
	public CommunitiesWidgetChildVM(Community community) {
        Long memCount = community.getMemberCount();

        this.id = community.id;
        this.name = community.name;
        this.membersCount = memCount;
        this.desc = community.description;
        this.icon = community.icon;
        this.type = community.communityType.name();
        this.targetingType = community.targetingType;
        this.targetingInfo = community.targetingInfo;
        this.system = community.system;
        this.isO = false;
        this.isP = false;
        this.isM = false;
    }
}
