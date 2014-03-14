package com.mnt;

import models.Notification;
import models.SocialRelation;

public class SocialActivity {
	// TODO
	public static void handle(SocialRelation socialAction) {

		if (socialAction.actionType != null) {
			switch (socialAction.actionType) {
				case JOIN_REQUESTED: {
					Notification notification = new Notification();
					notification.socialAction = socialAction;
					notification.recipetent = socialAction.target.owner;
					notification.message = socialAction.actor.name
							+ "has requested to Join" + socialAction.target.name;
					notification.save();
				}
					break;
	
				case FRIEND_REQUESTED: {
					Notification notification = new Notification();
					notification.socialAction = socialAction;
					notification.recipetent = socialAction.target;
					notification.message = socialAction.actor.name
							+ " wants to be Your Friend "
							+ socialAction.target.name;
					notification.save();
				}
					break;
	
				}
		}
		
		if(socialAction.action != null) {
			switch (socialAction.action) {

			case MEMBER: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.actor;
				notification.message = "You are now member of "
						+ socialAction.target.name;
				notification.save();
			}
				break;

			case FRIEND: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.actor;
				notification.message = "You are now Friend of "
						+ socialAction.target.name;
				notification.save();
			}
				break;

			case LIKED: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.target.owner;
				notification.message = socialAction.actor.name
						+ " Liked on your " + socialAction.target.objectType;
				notification.save();
			}
				break;

			case COMMENTED: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.target.owner;
				notification.message = socialAction.actor.name
						+ " Commented on your Post";
				notification.save();
			}
				break;

			case ANSWERED: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.target;
				notification.message = socialAction.actor.name
						+ " Answered to your Question";
				notification.save();
			}
				break;

			}
			
		} if (socialAction.actionType != null) {
			switch (socialAction.actionType) {
			case JOIN_REQUESTED: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.target.owner;
				notification.message = socialAction.actor.name
						+ "has requested to Join" + socialAction.target.name;
				notification.save();
			}
				break;

			case FRIEND_REQUESTED: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.target;
				notification.message = socialAction.actor.name
						+ " wants to be Your Friend "
						+ socialAction.target.name;
				notification.save();
			}
				break;

			case RELATIONSHIP_REQUESTED: {
				Notification notification = new Notification();
				notification.socialAction = socialAction;
				notification.recipetent = socialAction.target;
				notification.message = socialAction.actor.name
						+ " wants to add you in " + socialAction.action
						+ " list. ";
				notification.save();
			}
				break;

			}
		}

	}
}
