package viewmodel;

import java.util.List;

public class CommunitiesParentVM {
	public int sn;
	public List<CommunitiesWidgetChildVM> fvm;
	public Boolean isMore;
	
	public CommunitiesParentVM(int sn, List<CommunitiesWidgetChildVM> fvm) {
		this.sn = sn; 
		this.fvm = fvm;
	}
}
