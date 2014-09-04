package com.mnt;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Comment;
import models.Community;
import models.Notification;
import models.Notification.NotificationType;
import models.Post;
import models.PrimarySocialRelation;
import models.SocialRelation;
import models.User;
import play.libs.Json;

import common.cache.FriendCache;

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
				notification.recipient = socialAction.actor;
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
				notification.recipient = socialAction.actor;
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
				notification.recipient = socialAction.targetOwner;
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
				notification.recipient = socialAction.target;
				notification.notificationType = NotificationType.FRIEND_REQUEST;
				notification.message = socialAction.getActorObject().name;
				notification.save();
			}
				break;

			case RELATIONSHIP_REQUESTED: {
				Notification notification = new Notification();
				notification.socialActionID = socialAction.id;
				notification.recipient = socialAction.target;
				notification.message = socialAction.actorname
						+ " wants to add you in " + socialAction.action
						+ " list. ";
				notification.save();
			}
				break;
				
			case INVITE_REQUESTED: {
				Notification notification = new Notification();
				notification.socialActionID = socialAction.id;
				jsonMap.put("photo", "/get-mini-cover-community-image-by-id/"+socialAction.target);
				jsonMap.put("onClick", "#/community/" + socialAction.target + "/moment");
				notification.URLs = Json.stringify(Json.toJson(jsonMap));
				notification.recipient = socialAction.actor;
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
                // fan out to friends of same community only
                List<Long> frdIds = FriendCache.getFriendsIds(socialAction.actor);

                if (frdIds.size() > 0) {
                    Community community = Post.findById(socialAction.target).community;
                    List<User> frdMembers = community.getMembersIn(frdIds);

                    for(User user : frdMembers) {
                        Notification notification =
                                Notification.getNotification(user.id, NotificationType.POSTED, community.id, SocialObjectType.COMMUNITY);

                        if(notification == null){
                            notification = new Notification();
                            notification.notificationType = NotificationType.POSTED;
                            notification.target = community.id;
                            notification.targetType = SocialObjectType.COMMUNITY;
                            notification.recipient = user.id;
                            notification.count++;
                            notification.socialActionID = community.id;
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", "#/community/" + community.id + "/moment");
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.addToList(User.findById(socialAction.actor));  // post owner
                            notification.status = 0;
                            notification.message = socialAction.actorname+" 在「"+community.name+"」裏分享。";
                            notification.setUpdatedDate(new Date());
                            notification.save();
                        } else {
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", "#/community/" + community.id + "/moment");
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.count++;
                            notification.status = 0;
                            notification.addToList(User.findById(socialAction.actor));
                            notification.message = notification.usersName+" 在「"+community.name+"」裏分享。";
                            notification.merge();
                        }
                    }
                }
            }
				break;
				
			case POSTED_QUESTION: {
                // fan out to friends of same community only
                List<Long> frdIds = FriendCache.getFriendsIds(socialAction.actor);

                if (frdIds.size() > 0) {
                    Community community =  Post.findById(socialAction.target).community;
                    List<User> frdMembers = community.getMembersIn(frdIds);

                    for(User user : frdMembers){
                        Notification notification =
                                Notification.getNotification(user.id, NotificationType.POSTED_QUESTION, community.id, SocialObjectType.COMMUNITY);

                        if(notification == null){
                            notification = new Notification();
                            notification.addToList(User.findById(socialAction.actor));
                            notification.target = community.id;
                            notification.targetType = SocialObjectType.COMMUNITY;
                            notification.notificationType = NotificationType.POSTED_QUESTION;
                            notification.recipient = user.id;
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", "#/community/" + community.id + "/question");
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.count++;
                            notification.status = 0;
                            notification.socialActionID = community.id;
                            notification.message = socialAction.actorname+" 在「"+community.name+"」裏發問。";
                            notification.setUpdatedDate(new Date());
                            notification.save();
                        } else {
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", "#/community/" + community.id + "/question");
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.count++;
                            notification.status = 0;
                            notification.addToList(User.findById(socialAction.actor));
                            notification.message = notification.usersName+" 在「"+community.name+"」裏發問。";
                            notification.merge();
                        }
                    }
                }
			}
				break;
				
			case LIKED: {
				if(socialAction.targetType == SocialObjectType.POST){
					Post post = Post.findById(socialAction.target);
					long owner_id = post.owner.id;
					if(User.findById(socialAction.actor).id == owner_id){
						return;
					}
					
					Notification notification =
                            Notification.getNotification(owner_id, NotificationType.LIKED, socialAction.target, SocialObjectType.POST);
						
					if(notification == null){
						notification = new Notification();
						notification.target = socialAction.target;          // post id
						notification.targetType = SocialObjectType.POST;
			        	notification.notificationType = NotificationType.LIKED;
			        	notification.recipient = owner_id;
			        	notification.count = 1L;
			        	notification.socialActionID = socialAction.target;
			        	jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/post-landing/id/"+post.id+"/communityId/"+post.community.id);
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
			        	notification.addToList(User.findById(socialAction.actor));
			        	notification.message = socialAction.actorname+" 對你的分享讚好。";
			        	notification.status = 0;
			        	notification.setUpdatedDate(new Date());
			        	notification.save();
					} else {
						jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/post-landing/id/"+post.id+"/communityId/"+post.community.id);
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
						notification.count++;
						notification.addToList(User.findById(socialAction.actor));
						notification.message = notification.usersName+" 對你的分享讚好。";
						notification.status = 0;
						notification.merge();
					}
				}
                else if(socialAction.targetType == SocialObjectType.COMMENT){
					Comment comment = Comment.findById(socialAction.target);
					long owner_id = comment.owner.id;
					if(User.findById(socialAction.actor).id == owner_id){
						return;
					}
					
					Notification notification =
                            Notification.getNotification(owner_id, NotificationType.LIKED, socialAction.target, SocialObjectType.COMMENT);
					
					if(notification == null){
						notification = new Notification();
						notification.target = socialAction.target;
						notification.targetType = SocialObjectType.COMMENT;
			        	notification.notificationType = NotificationType.LIKED;
			        	notification.recipient = owner_id;
			        	notification.count++;
			        	notification.socialActionID = comment.getPost().id;
			        	jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/post-landing/id/"+comment.getPost().id+"/communityId/"+comment.getPost().community.id);
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
			        	notification.addToList(User.findById(socialAction.actor));
			        	notification.status = 0;
			        	notification.message = socialAction.actorname+" 對你的留言讚好。";
			        	notification.setUpdatedDate(new Date());
			        	notification.save();
					} else {
						jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/post-landing/id/"+comment.getPost().id+"/communityId/"+comment.getPost().community.id);
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
						notification.count++;
						notification.addToList(User.findById(socialAction.actor));
						notification.message = notification.usersName+" 對你的留言讚好。";
						notification.status = 0;
						notification.merge();
					}
					
				}
                else if(socialAction.targetType == SocialObjectType.ANSWER){
					Comment comment = Comment.findById(socialAction.target);
					long owner_id = comment.owner.id;
					if(User.findById(socialAction.actor).id == owner_id){
						return;
					}
					
					Notification notification =
                            Notification.getNotification(owner_id, NotificationType.LIKED, socialAction.target, SocialObjectType.ANSWER);
					
					if(notification == null){
						notification = new Notification();
						notification.target = socialAction.target;
						notification.targetType = SocialObjectType.ANSWER;
			        	notification.notificationType = NotificationType.LIKED;
			        	notification.recipient = owner_id;
			        	notification.count++;
			        	notification.socialActionID = comment.getPost().id;
                        jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/qna-landing/id/"+comment.getPost().id+"/communityId/"+comment.getPost().community.id);
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
			        	notification.addToList(User.findById(socialAction.actor));
			        	notification.status = 0;
			        	notification.message = socialAction.actorname+" 覺得你的回覆有用。";
			        	notification.setUpdatedDate(new Date());
			        	notification.save();
					} else {
						jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/qna-landing/id/"+comment.getPost().id+"/communityId/"+comment.getPost().community.id);
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
						notification.count++;
						notification.addToList(User.findById(socialAction.actor));
						notification.message = notification.usersName+" 覺得你的回覆有用。";
						notification.status = 0;
						notification.merge();
					}	
					
				
				} else if(socialAction.targetType == SocialObjectType.QUESTION){
					Post post = Post.findById(socialAction.target);
					long owner_id = post.owner.id;
					if(User.findById(socialAction.actor).id == owner_id){
						return;
					}
					
					Notification notification =
                            Notification.getNotification(owner_id, NotificationType.LIKED, socialAction.target, SocialObjectType.QUESTION);
					
					if(notification == null){
						notification = new Notification();
						notification.target = socialAction.target;              // post id
						notification.targetType = SocialObjectType.QUESTION;
			        	notification.notificationType = NotificationType.LIKED;
			        	notification.recipient = owner_id;
			        	notification.count++;
			        	notification.socialActionID = socialAction.target;
			        	notification.addToList(User.findById(socialAction.actor));
			        	jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/qna-landing/id/"+post.id+"/communityId/"+post.community.id);
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
			        	notification.message = socialAction.actorname+" 把你的發問推上。";
			        	notification.status = 0;
			        	notification.setUpdatedDate(new Date());
			        	notification.save();
					} else {
						jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
			        	jsonMap.put("onClick", "/#/qna-landing/id/"+post.id+"/communityId/"+post.community.id);
			        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
						notification.count++;
						notification.addToList(User.findById(socialAction.actor));
						notification.message = notification.usersName+" 把你的發問推上。";
						notification.status = 0;
						notification.merge();
					}
				}
			}
				break;
			
				
			case COMMENTED: {
				Post post = Post.findById(socialAction.target);
                long owner_id = post.owner.id;
				if(User.findById(socialAction.actor).id == post.owner.id){
					return;
				}
				Notification notification =
                        Notification.getNotification(owner_id, NotificationType.COMMENT, socialAction.target, SocialObjectType.POST);

				if(notification == null){
					notification = new Notification();
					notification.target = socialAction.target;
					notification.targetType = SocialObjectType.POST;
		        	notification.notificationType = NotificationType.COMMENT;
		        	notification.recipient = owner_id;
		        	notification.count = 1L;
		        	notification.socialActionID = socialAction.target;
		        	jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
		        	jsonMap.put("onClick", "/#/post-landing/id/"+post.id+"/communityId/"+post.community.id);
		        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
		        	notification.addToList(User.findById(socialAction.actor));
		        	notification.message = socialAction.actorname+" 在你的分享留言。";
		        	notification.status = 0;
		        	notification.setUpdatedDate(new Date());
		        	notification.save();
				} else {
					notification.count++;
					notification.addToList(User.findById(socialAction.actor));
					jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
		        	jsonMap.put("onClick", "/#/post-landing/id/"+post.id+"/communityId/"+post.community.id);
		        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
		        	notification.status = 0;
		        	notification.message = notification.usersName+" 在你的分享留言。";
					notification.merge();
				}
			}
				break;
				
				
			case ANSWERED: {
				Post post = Post.findById(socialAction.target);
                long owner_id = post.owner.id;
				if(User.findById(socialAction.actor).id == post.owner.id){
					return;
				}
				Notification notification =
                        Notification.getNotification(owner_id, NotificationType.ANSWERED, socialAction.target, SocialObjectType.POST);
				
				if(notification == null){
					notification = new Notification();
					notification.target = socialAction.target;          // post id
					notification.targetType = SocialObjectType.POST;
		        	notification.notificationType = NotificationType.ANSWERED;
		        	notification.recipient = owner_id;
		        	notification.count = 1L;
		        	jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
		        	jsonMap.put("onClick", "/#/qna-landing/id/"+post.id+"/communityId/"+post.community.id);
		        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
		        	notification.socialActionID = socialAction.target;
		        	notification.addToList(User.findById(socialAction.actor));
		        	notification.message = socialAction.actorname+" 回應了你的發問。";
		        	notification.status = 0;
		        	notification.setUpdatedDate(new Date());
		        	notification.save();
				} else {
					notification.count++;
					notification.addToList(User.findById(socialAction.actor));
					jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
		        	jsonMap.put("onClick", "/#/qna-landing/id/"+post.id+"/communityId/"+post.community.id);
		        	notification.URLs = Json.stringify(Json.toJson(jsonMap));
		        	notification.message = notification.usersName+" 回應了你的發問。";
		        	notification.status = 0;
					notification.merge();
				}
			}
				break;	
			}
		} 
	}
}
