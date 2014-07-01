package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import models.Community;
import models.User;

public class CommunitiesWidgetChildVM {
    @JsonProperty("id") public Long id;
    @JsonProperty("gi") public String icon;
    @JsonProperty("mm") public Long membersCount;
    @JsonProperty("dn") public String name;
    @JsonProperty("msg") public String desc;
    @JsonProperty("isO") public Boolean isO;
    @JsonProperty("isP") public Boolean isP;
    @JsonProperty("isM") public Boolean isM;
    @JsonProperty("tp") public String type;
	
    public CommunitiesWidgetChildVM(Long id, Long membersCount, String name, String desc, 
            String icon, Community.CommunityType type, Boolean isO, Boolean isP, Boolean isM) {
        this.id = id;
        this.name = name;
        this.membersCount = membersCount;
        this.desc = desc;
        this.icon = icon;
        this.type = type.name();
        this.isO = isO;
        this.isP = isP;
        this.isM = isM;
    }

	public CommunitiesWidgetChildVM(Community community, User user) {
        this(community.id, (long)community.getMembers().size(), community.name, 
                community.description, community.iconName, community.communityType,
                (user == community.owner), user.isJoinRequestPendingFor(community), user.isMemberOf(community));
    }
}
