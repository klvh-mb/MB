package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import models.Community;
import models.User;

import java.util.List;

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
	
	public CommunitiesWidgetChildVM(Community community, User user) {
        List<Long> memIds = community.getMemberIds();

        this.id = community.id;
        this.name = community.name;
        this.membersCount = (long) memIds.size();
        this.desc = community.description;
        this.icon = community.icon;
        this.type = community.communityType.name();
        this.system = community.system;
        this.isO = user == community.owner;
        this.isP = user.isJoinRequestPendingFor(community);
        this.isM = memIds.contains(user.getId());
    }
}
