package viewmodel;

public class CommunitiesWidgetChildVM {
	public Long id;
	public Long mm;
	public String dn;
	public String msg;
	public Boolean isO;
	
	public CommunitiesWidgetChildVM(Long id, Long mm, String dn, String msg) {
		this.id = id;
		this.dn = dn;
		this.mm = mm;
		this.msg = msg;
	}
	
	public CommunitiesWidgetChildVM(Long id, Long mm, String dn) {
		this(id,mm,dn,"");
	}
}
