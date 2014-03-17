package viewmodel;

import java.util.List;

public class FriendsParentVM {
	public int sn;
	public List<FriendsChildVM> fvm;
	
	public FriendsParentVM(int sn, List<FriendsChildVM> fvm) {
		this.sn = sn; 
		this.fvm = fvm;
	}
}
