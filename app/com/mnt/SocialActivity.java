package com.mnt;

import models.Notification;
import models.SocialRelation;

public class SocialActivity {
//TODO
	public static void handle(SocialRelation socialAction) {
		
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
		
		switch (socialAction.action) {
			case MEMBER :
			{
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.actor;
				notification.message = "You are now member of " + socialAction.target.name ;
				notification.save();
			}
			break;
		}
		
		
	}

}
