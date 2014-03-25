package viewmodel;

import models.Community;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityVM {
	@JsonProperty("n") public String name;
	@JsonProperty("i") public long id;
	
	public static CommunityVM communityVM (Community c) {
		CommunityVM vm = new CommunityVM();
		vm.name = c.name;
		vm.id = c.id;
		return vm;
	}
}
