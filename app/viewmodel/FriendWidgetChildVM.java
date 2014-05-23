package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

public class FriendWidgetChildVM {
	public Long id;
	public String dn;
	public Long nid;
	@JsonProperty("isf") public boolean isFriend;
	@JsonProperty("isP") public boolean isFriendRequestPending;
	
	public FriendWidgetChildVM(Long id, String dn) {
		this(id, dn, 0L);
	}
	
	public FriendWidgetChildVM(Long id, String dn,Long nid) {
		this.id = id;
		this.dn = dn;
		this.nid = nid;
	}
}
