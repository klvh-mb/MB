package viewmodel;

import models.Location;

import org.codehaus.jackson.annotate.JsonProperty;

public class FriendWidgetChildVM {
	public Long id;
	public String dn;
	public Long nid;
	public Location ln;
	@JsonProperty("isf") public boolean isFriend;
	@JsonProperty("isP") public boolean isFriendRequestPending;
	
	public FriendWidgetChildVM(Long id, String dn, Location ln) {
		this(id, dn, 0L, ln);
	}
	
	public FriendWidgetChildVM(Long id, String dn, Long nid) {
        this(id, dn, nid, null);
    }
	
	public FriendWidgetChildVM(Long id, String dn, Long nid, Location ln) {
		this.id = id;
		this.dn = dn;
		this.nid = nid;
		this.ln = ln;
	}
}
