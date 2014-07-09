package com.mnt;

import models.Notification;
import models.Notification.NotificationType;
import models.SocialRelation;

public class SocialActivity {
	// TODO
	public static void handle(SocialRelation socialAction) {

		if(socialAction.action != null) {
			switch (socialAction.action) {

			case MEMBER: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.actor;
				notification.notificationType = NotificationType.COMMUNITY_JOIN_APPROVED;
				notification.message = "You are now member of " + socialAction.targetname;
				/*
				if(socialAction.memberJoinedOpenCommunity) {
					notification.readed = false;
				} else {
					notification.readed = true;
				}
				*/
				notification.readed = false;
				notification.save();
			}
				break;

			case FRIEND: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.actor;
				notification.notificationType = NotificationType.FRIEND_ACCEPTED;
				notification.message = "You are now Friend of "
						+ socialAction.targetname;
				notification.readed = true;
				notification.save();
			}
				break;

			}
		} 
		
		if (socialAction.actionType != null) {
			switch (socialAction.actionType) {
			case JOIN_REQUESTED: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.targetOwner;
				notification.notificationType = NotificationType.COMMUNITY_JOIN_REQUEST;
				notification.message = "wants to join community " + socialAction.targetname;
				notification.save();
			}
				break;

			case FRIEND_REQUESTED: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.target;
				notification.notificationType = NotificationType.FRIEND_REQUEST;
				notification.message = socialAction.actorname
						+ " wants to be Your Friend "
						+ socialAction.targetname;
				notification.save();
			}
				break;

			case RELATIONSHIP_REQUESTED: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.target;
				notification.message = socialAction.actorname
						+ " wants to add you in " + socialAction.action
						+ " list. ";
				notification.save();
			}
				break;
				
			case INVITE_REQUESTED: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.actor;
				notification.notificationType = NotificationType.COMMUNITY_INVITE_REQUEST;
				notification.message = "You are invited to join community " + socialAction.targetname;
				notification.save();
			}
				break;

			}
		}

	}
}
