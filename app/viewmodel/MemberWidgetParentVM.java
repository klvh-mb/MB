package viewmodel;

import java.util.List;

public class MemberWidgetParentVM {
	public int sn;
	public List<MembersWidgetChildVM> mvm;
	
	public MemberWidgetParentVM(int sn, List<MembersWidgetChildVM> members) {
		this.sn = sn; 
		this.mvm = members;
	}
}
