package viewmodel;

import models.Community;

public class CommunitiesWidgetChildVM {
	public Long id;
	public String gi;
	public Long mm;
	public String dn;
	public String msg;
	public Boolean isO;
	public Boolean isP;
	public String tp;
	
	public CommunitiesWidgetChildVM(Long id, Long mm, String dn, String msg,String gi,Community.CommunityType type) {
		this.id = id;
		this.dn = dn;
		this.mm = mm;
		this.msg = msg;
		this.gi = gi;
		this.tp = type.name();
	}
	
	public CommunitiesWidgetChildVM(Long id, Long mm, String dn,Community.CommunityType type, String msg) {
		this(id,mm,dn,msg,"",type);
	}
	
	public CommunitiesWidgetChildVM(Long id, Long mm, String dn,Community.CommunityType type) {
		this(id,mm,dn,type,"");
	}
}
