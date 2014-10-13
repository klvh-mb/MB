package viewmodel;

import java.util.ArrayList;
import java.util.List;

import models.Community;
import models.CommunityCategory;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityCategoryMapVM {

	@JsonProperty("id") public long id;
	@JsonProperty("name") public String name;
	@JsonProperty("communities") public List<CommunitiesWidgetChildVM> communityVMs;
	
	public static CommunityCategoryMapVM communityCategoryMapVM(CommunityCategory communityCategory, List<Community> communities) {
	    CommunityCategoryMapVM communityCategoryMapVM = new CommunityCategoryMapVM();
	    communityCategoryMapVM.id = communityCategory.id;
	    communityCategoryMapVM.name = communityCategory.name;
	    communityCategoryMapVM.communityVMs = new ArrayList<>();
	    for (Community community : communities) {
	        communityCategoryMapVM.communityVMs.add(new CommunitiesWidgetChildVM(community));
	    }
		return communityCategoryMapVM;
	}
}
