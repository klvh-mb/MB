package viewmodel;

import java.util.ArrayList;
import java.util.List;

import models.Community;
import models.CommunityCategory;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityCategoryMapVM {

	@JsonProperty("id") public long id;
	@JsonProperty("name") public String name;
	@JsonProperty("communities") public List<CommunitiesWidgetChildVM> communityVMs;
	
	public static CommunityCategoryMapVM communityCategoryMapVM(
	        CommunityCategory communityCategory, List<Community> communities, User user) {
	    
	    CommunityCategoryMapVM communityCategoryMapVM = new CommunityCategoryMapVM();
	    communityCategoryMapVM.id = communityCategory.id;
	    communityCategoryMapVM.name = communityCategory.name;
	    communityCategoryMapVM.communityVMs = new ArrayList<CommunitiesWidgetChildVM>();
	    for (Community community : communities) {
	        communityCategoryMapVM.communityVMs.add(new CommunitiesWidgetChildVM(community, user));
	    }
		return communityCategoryMapVM;
	}
	
	public static CommunityCategoryMapVM communityCategoryMapVM(List<CommunitiesWidgetChildVM> vms, User user) {
        CommunityCategoryMapVM communityCategoryMapVM = new CommunityCategoryMapVM();
        communityCategoryMapVM.id = -1;
        communityCategoryMapVM.name = "其他";
        communityCategoryMapVM.communityVMs = vms;
        return communityCategoryMapVM;
    }
}
