package viewmodel;

public class CommunitiesWidgetChildVM {
	public Long id;
	public String gi;
	public Long mm;
	public String dn;
	public String msg;
	public Boolean isO;
	public Boolean isP;
	
	public CommunitiesWidgetChildVM(Long id, Long mm, String dn, String msg,String gi) {
		this.id = id;
		this.dn = dn;
		this.mm = mm;
		this.msg = msg;
		this.gi = gi;
	}
	
	public CommunitiesWidgetChildVM(Long id, Long mm, String dn, String msg) {
		this(id,mm,dn,msg,"");
	}
	
	public CommunitiesWidgetChildVM(Long id, Long mm, String dn) {
		this(id,mm,dn,"");
	}
}
