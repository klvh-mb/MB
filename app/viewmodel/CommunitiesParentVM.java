package viewmodel;

import java.util.List;

public class CommunitiesParentVM {
	public int sn;
	public List<CommunitiesWidgetChildVM> fvm;
	
	public CommunitiesParentVM(int sn, List<CommunitiesWidgetChildVM> fvm) {
		this.sn = sn; 
		this.fvm = fvm;
	}
}
