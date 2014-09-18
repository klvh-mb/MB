package viewmodel;

import java.util.List;

public class CommunitiesParentVM {
	public int sn;
	public List<CommunitiesWidgetChildVM> communities;
	public Boolean isMore;
	
	public CommunitiesParentVM(int sn, List<CommunitiesWidgetChildVM> communities) {
		this.sn = sn; 
		this.communities = communities;
		this.isMore = (sn > communities.size());
	}
}
