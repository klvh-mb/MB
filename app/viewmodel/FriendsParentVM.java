package viewmodel;

import java.util.List;

public class FriendsParentVM {
	public int sn;
	public List<FriendsVM> fvm;
	
	public FriendsParentVM(int sn, List<FriendsVM> fvm) {
		this.sn = sn; 
		this.fvm = fvm;
	}
}
