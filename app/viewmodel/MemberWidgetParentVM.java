package viewmodel;

import java.util.List;

public class MemberWidgetParentVM {
	public int sn;
	public List<MembersWidgetChildVM> fvm;
	
	public MemberWidgetParentVM(int sn, List<MembersWidgetChildVM> members) {
		this.sn = sn; 
		this.fvm = members;
	}
}
