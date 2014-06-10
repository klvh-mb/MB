package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import models.Community;

public class CommunitiesWidgetChildVM {
    @JsonProperty("id") public Long id;
    @JsonProperty("gi") public String icon;
    @JsonProperty("mm") public Long membersCount;
    @JsonProperty("dn") public String name;
    @JsonProperty("msg") public String desc;
    @JsonProperty("isO")public Boolean isO;
    @JsonProperty("isP")public Boolean isP;
    @JsonProperty("isM") public Boolean isM;
    @JsonProperty("tp") public String type;
	
	public CommunitiesWidgetChildVM(Long id, Long membersCount, String name, String desc, String icon, Community.CommunityType type) {
		this.id = id;
		this.name = name;
		this.membersCount = membersCount;
		this.desc = desc;
		this.icon = icon;
		this.type = type.name();
	}
	
	public CommunitiesWidgetChildVM(Long id, Long membersCount, String name, Community.CommunityType type, String desc) {
		this(id, membersCount, name, desc, "", type);
	}
	
	public CommunitiesWidgetChildVM(Long id, Long membersCount, String name, Community.CommunityType type) {
		this(id, membersCount, name, type, "");
	}
}
