package viewmodel;

public class NotificationVM {
	public Long id;
	public Long mm;
	public String msg;
	public String dn;
	public String tp;
	
	public NotificationVM(Long id,Long mm, String dn, String tp,String msg) {
		this.id = id;
		this.mm = mm;
		this.dn = dn;
		this.tp = tp;
		this.msg = msg;
	}
}
