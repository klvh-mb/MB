package viewmodel;

import java.util.List;

public class FriendWidgetParentVM {
	public Long sn;
	public List<FriendWidgetChildVM> fvm;
	
	public FriendWidgetParentVM(Long sn, List<FriendWidgetChildVM> fvm) {
		this.sn = sn; 
		this.fvm = fvm;
	}
}
