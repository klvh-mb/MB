package viewmodel;

import java.io.IOException;
import java.util.Date;

import models.Notification;

import org.codehaus.jackson.map.ObjectMapper;

public class NotificationVM {
	public Long id;
	public Long nid;
	public int sta;
	public String msg;
	public String tp;
	public UrlsVM  url;
    public Date upd;
	
	public NotificationVM(Notification notif) {
		this.id = notif.recipient;
		this.nid = notif.id;
		this.tp = notif.notificationType.name();
		this.msg = notif.message;
		this.sta = notif.status;
		try {
			this.url = new ObjectMapper().readValue(notif.URLs, UrlsVM.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.upd = notif.getUpdatedDate();
	}
}
