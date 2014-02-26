package com.mnt;

import models.Notification;
import models.SocialAction;

public class SocialActivity {
//TODO
	public static void handle(SocialAction socialAction) {
		
		switch (socialAction.action) {
			case JOIN_REQUESTED :
			{
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.target.owner;
				notification.message = socialAction.actor.name + "has requested to Join" + socialAction.target.name;
				notification.save();
			}
			break;
		}
		
		
	}

}
