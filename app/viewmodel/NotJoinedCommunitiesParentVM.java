package viewmodel;

import java.util.List;

public class NotJoinedCommunitiesParentVM {
	public int sn;
	public List<NotJoinedCommunitiesWidgetChildVM> fvm;
	
	public NotJoinedCommunitiesParentVM(int sn, List<NotJoinedCommunitiesWidgetChildVM> fvm) {
		this.sn = sn; 
		this.fvm = fvm;
	}
}
