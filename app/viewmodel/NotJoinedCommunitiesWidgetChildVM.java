package viewmodel;

public class NotJoinedCommunitiesWidgetChildVM {
	public Long id;
	public Long mm;
	public String dn;
	public Boolean fl = true;
	
	public NotJoinedCommunitiesWidgetChildVM(Long id, Long mm, String dn) {
		this.id = id;
		this.dn = dn;
		this.mm = mm;
	}
}
