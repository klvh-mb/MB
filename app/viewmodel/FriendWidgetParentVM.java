package viewmodel;

import java.util.List;

public class FriendWidgetParentVM {
	public int sn;
	public List<FriendWidgetChildVM> fvm;
	
	public FriendWidgetParentVM(int sn, List<FriendWidgetChildVM> fvm) {
		this.sn = sn; 
		this.fvm = fvm;
	}
}
