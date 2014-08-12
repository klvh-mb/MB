package com.mnt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.libs.Json;

import models.Comment;
import models.Community;
import models.Notification;
import models.Notification.NotificationType;
import models.Post;
import models.PrimarySocialRelation;
import models.SocialRelation;
import models.User;
import domain.SocialObjectType;

public class SocialActivity {
	// TODO
	public static void handle(SocialRelation socialAction) {

		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("actor", socialAction.actor);
		jsonMap.put("target", socialAction.target);
		
		if(socialAction.action != null) {
			switch (socialAction.action) {

			case MEMBER: {
				Notification notification = new Notification();
				notification.socialActionID = socialAction.id;
				jsonMap.put("photo", "/get-mini-cover-community-image-by-id/"+socialAction.target);
				jsonMap.put("onClick", "#/community/" + socialAction.target + "/question");
				notification.URLs = Json.stringify(Json.toJson(jsonMap));
				notification.recipetent = socialAction.actor;
				notification.notificationType = NotificationType.COMMUNITY_JOIN_APPROVED;
				notification.message = "You are now member of " + socialAction.getTargetObject().name;
				notification.status = 0;
				notification.save();
			}
				break;

			case FRIEND: {
				Notification notification = new Notification();
				notification.socialActionID = socialAction.id;
				jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.target);
				jsonMap.put("onClick", "#/profile/" + socialAction.target );
				notification.URLs = Json.stringify(Json.toJson(jsonMap));
				notification.recipetent = socialAction.actor;
				notification.notificationType = NotificationType.FRIEND_ACCEPTED;
				notification.message = "You are now Friend of "
						+ socialAction.getTargetObject().name;
				notification.status = 0;
				notification.save();
			}
				break;

			}
		} 
		
		if (socialAction.actionType != null) {
			switch (socialAction.actionType) {
			case JOIN_REQUESTED: {
				Notification notification = new Notification();
				notification.socialActionID = socialAction.id;
				jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
				jsonMap.put("onClick", "#/profile/" + socialAction.actor );
				notification.URLs = Json.stringify(Json.toJson(jsonMap));
				notification.usersName = socialAction.getActorObject().name;
				notification.recipetent = socialAction.targetOwner;
				notification.notificationType = NotificationType.COMMUNITY_JOIN_REQUEST;
				notification.message = socialAction.getActorObject().name+" wants to join community " + socialAction.targetname;
				notification.save();
			}
				break;

			case FRIEND_REQUESTED: {
				Notification notification = new Notification();
				notification.socialActionID = socialAction.id;
				jsonMap.put("photo", "/image/get-mini-image-by-id/" + socialAction.actor);
				jsonMap.put("onClick", "/#/profile/" + socialAction.actor);
				notification.URLs = Json.stringify(Json.toJson(jsonMap));
				notification.recipetent = socialAction.target;
				notification.notificationType = NotificationType.FRIEND_REQUEST;
				notification.message = socialAction.getActorObject().name;
				notification.save();
			}
				break;

			case RELATIONSHIP_REQUESTED: {
				Notification notification = new Notification();
				notification.socialActionID = socialAction.id;
				notification.recipetent = socialAction.target;
				notification.message = socialAction.actorname
						+ " wants to add you in " + socialAction.action
						+ " list. ";
				notification.save();
			}
				break;
				
			case INVITE_REQUESTED: {
				Notification notification = new Notification();
				notification.socialActionID = socialAction.id;
				jsonMap.put("photo", "/image/get-mini-image-by-id/" + socialAction.actor);
				jsonMap.put("onClick", "#/community/" + socialAction.target + "/moment");
				notification.URLs = Json.stringify(Json.toJson(jsonMap));
				notification.recipetent = socialAction.actor;
				notification.notificationType = NotificationType.COMMUNITY_INVITE_REQUEST;
				notification.message = "You are invited to join community " + socialAction.targetname;
				notification.save();
			}
				break;

			}
		}

	}

	public static void handle(PrimarySocialRelation socialAction) {
		if(socialAction.action != null) {
			Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("actor", socialAction.actor);
			jsonMap.put("target", socialAction.target);
			
			switch (socialAction.action) {

			case POSTED: {
				Community community = Post.findById(socialAction.target).community;
				List<User> members = community.getMembers();
				for(User user : members){
					if(user.id == socialAction.actor)
						continue;
					Notification notification = Notification.getNotification(community.id, user.id, NotificationType.POSTED);
					
					if(notification == null){
						notification = new Notification();
			        	notification.notificationType = NotificationType.POSTED;
			        	notification.recipetent = user.id;
			        	notification.count++;
			        	notification.socialActionID = community.id;
						jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
						jsonMap.put("onClick", "#/community/" + community.id + "/moment");
						notification.URLs = Json.stringify(Json.toJson(jsonMap));
			        	notification.addToList(User.findById(socialAction.actor));
			        	notification.message = socialAction.actorname+ " posted on community "
								+ community.name;
			        	notification.save();
					} else {
						jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
						jsonMap.put("onClick", "#/community/" + community.id + "/moment");
						notification.URLs = Json.stringify(Json.toJson(jsonMap));
						notification.count++;
						notification.setMessage(socialAction.actorname+ " posted on community "
								+ community.name);
						notification.addToList(User.findById(socialAction.actor));
						notification.merge();
					}
					
				}
				
			}
				break;
				
			case POSTED_QUESTION: {
				Community community =  Post.findById(socialAction.target).community;
				List<User> members = community.getMembers();
				for(User user : members){
					if(user.id == socialAction.actor)
						continue;
					Notification notification = Notification.getNotification(community.id, user.id, NotificationType.POSTED_QUESTION);
					
					if(notification == null){
						notification = new Notification();
						notification.addToList(User.findById(socialAction.actor));
			        	notification.notificationType = NotificationType.POSTED_QUESTION;
			        	notification.recipetent = user.id;
						jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
						jsonMap.put("onClick", "#/community/" + community.id + "/question");
						notification.URLs = Json.stringify(Json.toJson(jsonMap));
			        	notification.count++;
			        	notification.socialActionID = community.id;
			        	notification.message = socialAction.actorname+ " Questioned on community "
								+ community.name;
			        	notification.save();
					} else {
						jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
						jsonMap.put("onClick", "#/community/" + community.id + "/question");
						notification.URLs = Json.stringify(Json.toJson(jsonMap));
						notification.count++;
						notification.setMessage(socialAction.actorname+ " Questioned on community "
								+ community.name);
						notification.addToList(User.findById(socialAction.actor));
						notification.merge();
					}
					
				}
				
			}
				break;
				
			case LIKED: {
				//notification.URLs = Json.stringify(Json.toJson(jsonMap));
				
				if(socialAction.targetType == SocialObjectType.POST){
					Post post = Post.findById(socialAction.target);
					long owner_id = post.owner.id;
					if(User.findById(socialAction.actor).id == owner_id){
						return;
					}
					
					Notification notification = Notification.getNotification(socialAction.target, socialAction.actor, NotificationType.LIKED);
						
					if(notification == null){
						notification = new Notification();
			        	notification.notificationType = NotificationType.LIKED;
			        	notification.recipetent = owner_id;
			        	notification.count = 1L;
			        	notification.socialActionID = socialAction.target;
			        	jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "#/community/" + post.community.id + "/moment");
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
			        	notification.addToList(User.findById(socialAction.actor));
			        	notification.message = socialAction.actorname+ " Liked on your Post ";
			        	notification.save();
					} else {
						jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "#/community/" + post.community.id + "/moment");
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
						notification.count++;
						notification.setMessage(socialAction.actorname+ " Liked on your Post ");
						notification.addToList(User.findById(socialAction.actor));
						
						notification.merge();
					}
				} else if(socialAction.targetType == SocialObjectType.COMMENT){
					Comment comment = Comment.findById(socialAction.target);
					long owner_id = comment.owner.id;
					if(User.findById(socialAction.actor).id == owner_id){
						return;
					}
					
					Notification notification = Notification.getNotification(socialAction.target, socialAction.actor, NotificationType.LIKED);
					
					if(notification == null){
						notification = new Notification();
			        	notification.notificationType = NotificationType.LIKED;
			        	notification.recipetent = owner_id;
			        	notification.count++;
			        	notification.socialActionID = socialAction.target;
			        	jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/community/" + comment.getPost().community.id + "/moment");
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
			        	notification.addToList(User.findById(socialAction.actor));
			        	
			        	notification.message = socialAction.actorname+ " Liked on your Comment ";
			        	notification.save();
					} else {
						jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/community/" + comment.getPost().community.id + "/moment");
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
						notification.count++;
						notification.setMessage(socialAction.actorname+ " Liked on your Comment ");
						notification.addToList(User.findById(socialAction.actor));
						
						notification.merge();
					}
					
				} else if(socialAction.targetType == SocialObjectType.ANSWER){
					Comment comment = Comment.findById(socialAction.target);
					long owner_id = comment.owner.id;
					if(User.findById(socialAction.actor).id == owner_id){
						return;
					}
					
					Notification notification = Notification.getNotification(socialAction.target, socialAction.actor, NotificationType.LIKED);
					
					if(notification == null){
						notification = new Notification();
			        	notification.notificationType = NotificationType.LIKED;
			        	notification.recipetent = owner_id;
			        	notification.count++;
			        	jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#//community/" + comment.getPost().community.id + "/question");
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
			        	notification.addToList(User.findById(socialAction.actor));
			        	
			        	notification.socialActionID = socialAction.target;
			        	notification.message = socialAction.actorname+ " Liked on your Answer ";
			        	notification.save();
					} else {
						jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/community/" + comment.getPost().community.id + "/question");
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
						notification.count++;
						notification.setMessage(socialAction.actorname+ " Liked on your Answer ");
						notification.addToList(User.findById(socialAction.actor));
						
						notification.merge();
					}	
					
				
				} else if(socialAction.targetType == SocialObjectType.QUESTION){
					Post post = Post.findById(socialAction.target);
					long owner_id = post.owner.id;
					if(User.findById(socialAction.actor).id == owner_id){
						return;
					}
					
					Notification notification = Notification.getNotification(socialAction.target, socialAction.actor, NotificationType.LIKED);
					
					if(notification == null){
						notification = new Notification();
			        	notification.notificationType = NotificationType.LIKED;
			        	notification.recipetent = owner_id;
			        	notification.count++;
			        	notification.socialActionID = socialAction.target;
			        	notification.addToList(User.findById(socialAction.actor));
			        	jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/community/"+ post.community.id +"/question");
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
			        	notification.message = socialAction.actorname+ " Liked on your Question ";
			        	notification.save();
					} else {
						jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/community/"+ post.community.id +"/question");
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
						notification.count++;
						notification.setMessage(socialAction.actorname+ " Liked on your Question ");
						notification.addToList(User.findById(socialAction.actor));
						notification.merge();
					}
				}
				
				
			}
			
				break;	
			
				
			case COMMENTED: {
				Post post = Post.findById(socialAction.target);
				if(User.findById(socialAction.actor).id == post.owner.id){
					return;
				}
				Notification notification = Notification.getNotification(socialAction.target, socialAction.actor, NotificationType.COMMENT);
				
				
				if(notification == null){
					notification = new Notification();
		        	notification.notificationType = NotificationType.COMMENT;
		        	notification.recipetent = Post.findById(socialAction.target).owner.id;
		        	notification.count = 1L;
		        	notification.socialActionID = socialAction.target;
		        	jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
		        	jsonMap.put("onClick", "/#/community/"+post.community.id+"/moment");
		        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
		        	notification.addToList(User.findById(socialAction.actor));
		        	notification.message = socialAction.actorname+ " Commented on your Post ";
		        	notification.save();
				} else {
					notification.count++;
					notification.setMessage(socialAction.actorname+ " Commented on your Post ");
					notification.addToList(User.findById(socialAction.actor));
					jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
		        	jsonMap.put("onClick", "/#/community/"+post.community.id+"/moment");
		        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
					notification.merge();
				}
			}
				break;
				
				
			case ANSWERED: {
				Post post = Post.findById(socialAction.target);
				if(User.findById(socialAction.actor).id == post.owner.id){
					return;
				}
				Notification notification = Notification.getNotification(socialAction.target, socialAction.actor, NotificationType.ANSWERED);
				
				if(notification == null){
					notification = new Notification();
		        	notification.notificationType = NotificationType.ANSWERED;
		        	notification.recipetent = Post.findById(socialAction.target).owner.id;
		        	notification.count = 1L;
		        	jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
		        	jsonMap.put("onClick", "/#/community/"+ post.community.id +"/question");
		        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
		        	notification.socialActionID = socialAction.target;
		        	notification.addToList(User.findById(socialAction.actor));
		        	notification.message = socialAction.actorname+ " Answered on your Question ";
		        	notification.save();
				} else {
					notification.count++;
					notification.setMessage(socialAction.actorname+ " Answered on your Question ");
					notification.addToList(User.findById(socialAction.actor));
					jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.actor);
		        	jsonMap.put("onClick", "/#/community/"+ post.community.id +"/question");
		        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
					notification.merge();
				}
			}
				break;	
			}
		} 
		
	}
}
