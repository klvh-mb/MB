package viewmodel;

import java.io.IOException;

import models.Notification;

import org.codehaus.jackson.map.ObjectMapper;

public class NotificationVM {
	public Long id;
	public Long nid;
	public int sta;
	public String msg;
	public String tp;
	public UrlsVM  url;
	
	public NotificationVM(Notification no) {
		this.id = no.recipient;
		this.nid = no.id;
		this.tp = no.notificationType.name();
		this.msg = no.message;
		this.sta = no.status;
		try {
			this.url = new ObjectMapper().readValue(no.URLs, UrlsVM.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
