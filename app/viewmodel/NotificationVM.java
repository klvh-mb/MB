package viewmodel;

import models.Notification;

public class NotificationVM {
	public Long id;
	public Long mm;
	public Long nid;
	public String msg;
	public String dn;
	public String tp;
	
	public NotificationVM(Notification no) {
		this.id = no.socialAction.actor;
		this.mm = no.socialAction.target;
		this.nid = no.id;
		this.dn = no.socialAction.getActorObject().name;
		this.tp = no.notificationType.name();
		this.msg = no.message;
	}
}
