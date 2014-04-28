package viewmodel;

public class FriendWidgetChildVM {
	public Long id;
	public String dn;
	public Long nid;
	
	public FriendWidgetChildVM(Long id, String dn) {
		this(id, dn, 0L);
	}
	
	public FriendWidgetChildVM(Long id, String dn,Long nid) {
		this.id = id;
		this.dn = dn;
		this.nid = nid;
	}
}
