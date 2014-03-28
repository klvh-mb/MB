package viewmodel;

import java.util.List;

public class NotJoinedCommunitiesParentVM {
	public int sn;
	public List<CommunitiesWidgetChildVM> fvm;
	
	public NotJoinedCommunitiesParentVM(int sn, List<CommunitiesWidgetChildVM> fvm) {
		this.sn = sn; 
		this.fvm = fvm;
	}
}
