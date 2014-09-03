package viewmodel;

import models.CommunityCategory;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityCategoryVM {

	@JsonProperty("id") public long id;
	@JsonProperty("name") public String name;
	
	public static CommunityCategoryVM communityCategoryVM(CommunityCategory communityCategory) {
	    CommunityCategoryVM communityCategoryVM = new CommunityCategoryVM();
	    communityCategoryVM.id = communityCategory.id;
	    communityCategoryVM.name = communityCategory.name;
		return communityCategoryVM;
	}
}
