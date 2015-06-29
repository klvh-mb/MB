package viewmodel;

import models.Community;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * Date: 6/12/14
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class HotCommunityVM {
	@JsonProperty("n")      public String name;
	@JsonProperty("d")      public String description;
	@JsonProperty("typ")    public Community.CommunityType communityType;
	@JsonProperty("icon")   public String icon;
	@JsonProperty("id")     public long id;
    @JsonProperty("nom")    public long noOfMembers;
    @JsonProperty("nopst") public long noOfRecentPosts;
    @JsonProperty("nocom") public long noOfRecentComments;

    public static HotCommunityVM hotCommunityVM(Community c, long noOfRecentPosts, long noOfRecentComments) {
        HotCommunityVM vm = new HotCommunityVM();
        vm.name = c.getName();
        vm.description = c.getDescription();
        vm.communityType = c.getCommunityType();
        vm.icon = c.getIcon();
        vm.id = c.getId();
        vm.noOfMembers = c.getMemberCount();
        vm.noOfRecentPosts = noOfRecentPosts;
        vm.noOfRecentComments = noOfRecentComments;
        return vm;
    }
}
