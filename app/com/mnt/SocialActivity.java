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
    // play url prefix
    private static final String MY_PREFIX = "/my#";
    private static final String MAGAZINE_PREFIX = "/magazine#";

    //////////////////////////////////////////////////
    // Url Helpers
    private static String resolveCommunityLandingUrl(Long commId, Community.CommunityType type) {
        boolean isBizCommunity = type != null && type == Community.CommunityType.BUSINESS;
        if (isBizCommunity) {
            return MAGAZINE_PREFIX+"/business/community/"+commId;
        } else {
            return MY_PREFIX+"/community/"+commId;
        }
    }

    private static String resolvePostLandingUrl(Long postId, Long commId, Community.CommunityType type) {
        boolean isBizCommunity = type != null && type == Community.CommunityType.BUSINESS;
        if (isBizCommunity) {
            return MAGAZINE_PREFIX+"/business-post-landing/id/"+postId+"/communityId/"+commId;
        } else {
            return MY_PREFIX +"/post-landing/id/"+postId+"/communityId/"+commId;
        }
    }

    private static String resolveQnALandingUrl(Long postId, Long commId, Community.CommunityType type) {
        boolean isBizCommunity = type != null && type == Community.CommunityType.BUSINESS;
        if (isBizCommunity) {
            return MAGAZINE_PREFIX+"/business-post-landing/id/"+postId+"/communityId/"+commId;
        } else {
            return MY_PREFIX +"/qna-landing/id/"+postId+"/communityId/"+commId;
        }
    }

    private static String resolveCommunityLandingUrl(Long commId) {
        Community.CommunityType type = Community.getCommunityTypeById(commId);
        return resolveCommunityLandingUrl(commId, type);
    }

    private static boolean isBusinessCommunity(Community community) {
        return community != null && community.communityType == Community.CommunityType.BUSINESS;
    }
    //////////////////////////////////////////////////


    /**
     * Handle SocialRelation
     * @param socialAction
     */
	public static void handle(SocialRelation socialAction) {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("actor", socialAction.actor);
		jsonMap.put("target", socialAction.target);
		
		if(socialAction.action != null) {
			switch (socialAction.action) {
                case MEMBER: {
                    Long commId = socialAction.target;

                    Notification notification = new Notification();
                    notification.socialActionID = socialAction.id;
                    jsonMap.put("photo", "/get-mini-cover-community-image-by-id/"+commId);
                    jsonMap.put("onClick", resolveCommunityLandingUrl(commId));
                    notification.URLs = Json.stringify(Json.toJson(jsonMap));
                    notification.recipient = socialAction.actor;
                    notification.notificationType = NotificationType.COMM_JOIN_APPROVED;
                    notification.message = "你正在關注「"+socialAction.getTargetObject().name+"」。";
                    notification.status = 0;
                    notification.save();
                }
				break;

                case FRIEND: {
                    Notification notification = new Notification();
                    notification.socialActionID = socialAction.id;
                    jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.target);
                    jsonMap.put("onClick", MY_PREFIX +"/profile/" + socialAction.target );
                    notification.URLs = Json.stringify(Json.toJson(jsonMap));
                    notification.recipient = socialAction.actor;
                    notification.notificationType = NotificationType.FRD_ACCEPTED;
                    notification.message = "你與 "+socialAction.getTargetObject().name+" 成為了朋友。";
                    notification.status = 0;
                    notification.save();
                }
				break;
			}
		} 
		
		if (socialAction.actionType != null) {
			switch (socialAction.actionType) {
                case JOIN_REQUESTED: {
                    Long requesterId = socialAction.actor;

                    Notification notification = new Notification();
                    notification.socialActionID = socialAction.id;
                    jsonMap.put("photo", "/image/get-mini-image-by-id/"+requesterId);
                    jsonMap.put("onClick", MY_PREFIX +"/profile/"+requesterId);
                    notification.URLs = Json.stringify(Json.toJson(jsonMap));
                    notification.usersName = socialAction.getActorObject().name;
                    notification.recipient = socialAction.targetOwner;
                    notification.notificationType = NotificationType.COMM_JOIN_REQUEST;
                    notification.message = socialAction.getActorObject().name+" 想加入「" + socialAction.targetname+"」社群。";
                    notification.save();
                }
				break;

                case FRIEND_REQUESTED: {
                    Notification notification = new Notification();
                    notification.socialActionID = socialAction.id;
                    jsonMap.put("photo", "/image/get-mini-image-by-id/" + socialAction.actor);
                    jsonMap.put("onClick", MY_PREFIX +"/profile/"+socialAction.actor);
                    notification.URLs = Json.stringify(Json.toJson(jsonMap));
                    notification.recipient = socialAction.target;
                    notification.notificationType = NotificationType.FRD_REQUEST;
                    notification.message = socialAction.getActorObject().name+" 想成為你的朋友。";
                    notification.save();
                }
				break;

//			case RELATIONSHIP_REQUESTED: {
//				Notification notification = new Notification();
//				notification.socialActionID = socialAction.id;
//				notification.recipient = socialAction.target;
//				notification.message = socialAction.actorname
//						+ " wants to add you in " + socialAction.action
//						+ " list. ";
//				notification.save();
//			}
//				break;
				
                case INVITE_REQUESTED: {
                    Long commId = socialAction.target;

                    Notification notification = new Notification();
                    notification.socialActionID = socialAction.id;
                    jsonMap.put("photo", "/get-mini-cover-community-image-by-id/"+commId);
                    jsonMap.put("onClick", resolveCommunityLandingUrl(commId));
                    notification.URLs = Json.stringify(Json.toJson(jsonMap));
                    notification.recipient = socialAction.actor;
                    notification.notificationType = NotificationType.COMM_INVITE_REQUEST;
                    notification.message = "有人推薦「"+socialAction.targetname+"」社群給您。";
                    notification.save();
                }
				break;
			}
		}
	}

    /**
     * Handle PrimarySocialRelation
     * @param socialAction
     */
	public static void handle(PrimarySocialRelation socialAction) {
		if(socialAction.action != null) {
			Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("actor", socialAction.actor);
			jsonMap.put("target", socialAction.target);
			
			switch (socialAction.action) {
                case POSTED: {
                    Post post = Post.findById(socialAction.target);
                    Community community = post.community;
                    if (isBusinessCommunity(community)) {
                        break;
                    }

                    // fan out to friends of same social community only
                    List<Long> frdIds = FriendCache.getFriendsIds(socialAction.actor);

                    if (frdIds.size() > 0) {
                        String commLandingUrl = resolveCommunityLandingUrl(community.id, community.communityType);
                        String postLandingUrl = resolvePostLandingUrl(post.id, community.id, community.communityType);

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
                                jsonMap.put("onClick", postLandingUrl);
                                notification.URLs = Json.stringify(Json.toJson(jsonMap));
                                notification.addToList(User.findById(socialAction.actor));  // post owner
                                notification.status = 0;
                                notification.message = socialAction.actorname+" 在「"+community.name+"」裏分享。";
                                notification.setUpdatedDate(new Date());
                                notification.save();
                            } else {
                                jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                                jsonMap.put("onClick", commLandingUrl);
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
                    Post post = Post.findById(socialAction.target);
                    Community community = post.community;
                    if (isBusinessCommunity(community)) {
                        break;
                    }

                    // fan out to friends of same social community only
                    List<Long> frdIds = FriendCache.getFriendsIds(socialAction.actor);

                    if (frdIds.size() > 0) {
                        String commLandingUrl = resolveCommunityLandingUrl(community.id, community.communityType);
                        String qnaLandingUrl = resolveQnALandingUrl(post.id, community.id, community.communityType);

                        List<User> frdMembers = community.getMembersIn(frdIds);
                        for(User user : frdMembers){
                            Notification notification =
                                    Notification.getNotification(user.id, NotificationType.QUESTIONED, community.id, SocialObjectType.COMMUNITY);

                            if(notification == null){
                                notification = new Notification();
                                notification.addToList(User.findById(socialAction.actor));
                                notification.target = community.id;
                                notification.targetType = SocialObjectType.COMMUNITY;
                                notification.notificationType = NotificationType.QUESTIONED;
                                notification.recipient = user.id;
                                jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                                jsonMap.put("onClick", qnaLandingUrl);
                                notification.URLs = Json.stringify(Json.toJson(jsonMap));
                                notification.count++;
                                notification.status = 0;
                                notification.socialActionID = community.id;
                                notification.message = socialAction.actorname+" 在「"+community.name+"」裏發佈了新話題。";
                                notification.setUpdatedDate(new Date());
                                notification.save();
                            } else {
                                jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                                jsonMap.put("onClick", commLandingUrl);
                                notification.URLs = Json.stringify(Json.toJson(jsonMap));
                                notification.count++;
                                notification.status = 0;
                                notification.addToList(User.findById(socialAction.actor));
                                notification.message = notification.usersName+" 在「"+community.name+"」裏發佈了新話題。";
                                notification.merge();
                            }
                        }
                    }
                }
				break;

                case WANT_ANS: {
                    if(socialAction.targetType == SocialObjectType.QUESTION){
                        Post post = Post.findById(socialAction.target);
                        long owner_id = post.owner.id;
                        if(User.findById(socialAction.actor).id == owner_id){
                            return;
                        }

                        String landingUrl = resolveQnALandingUrl(post.id, post.community.id, post.community.communityType);

                        Notification notification =
                                Notification.getNotification(owner_id, NotificationType.WANTED_ANS, socialAction.target, SocialObjectType.QUESTION);
                        if(notification == null){
                            notification = new Notification();
                            notification.target = socialAction.target;              // post id
                            notification.targetType = SocialObjectType.QUESTION;
                            notification.notificationType = NotificationType.WANTED_ANS;
                            notification.recipient = owner_id;
                            notification.count++;
                            notification.socialActionID = socialAction.target;
                            notification.addToList(User.findById(socialAction.actor));
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.message = socialAction.actorname+" 把你的話題推上。";
                            notification.status = 0;
                            notification.setUpdatedDate(new Date());
                            notification.save();
                        } else {
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.count++;
                            notification.addToList(User.findById(socialAction.actor));
                            notification.message = notification.usersName+" 把你的話題推上。";
                            notification.status = 0;
                            notification.merge();
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

                        String landingUrl = resolvePostLandingUrl(post.id, post.community.id, post.community.communityType);

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
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.addToList(User.findById(socialAction.actor));
                            notification.message = socialAction.actorname+" 對你的分享讚好。";
                            notification.status = 0;
                            notification.setUpdatedDate(new Date());
                            notification.save();
                        } else {
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
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

                        Post post = comment.getPost();
                        String landingUrl = resolvePostLandingUrl(post.id, post.community.id, post.community.communityType);

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
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.addToList(User.findById(socialAction.actor));
                            notification.status = 0;
                            notification.message = socialAction.actorname+" 對你的留言讚好。";
                            notification.setUpdatedDate(new Date());
                            notification.save();
                        } else {
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
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
                            jsonMap.put("onClick", MY_PREFIX +"/qna-landing/id/"+comment.getPost().id+"/communityId/"+comment.getPost().community.id);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.addToList(User.findById(socialAction.actor));
                            notification.status = 0;
                            notification.message = socialAction.actorname+" 覺得你的回覆有用。";
                            notification.setUpdatedDate(new Date());
                            notification.save();
                        } else {
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", MY_PREFIX +"/qna-landing/id/"+comment.getPost().id+"/communityId/"+comment.getPost().community.id);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.count++;
                            notification.addToList(User.findById(socialAction.actor));
                            notification.message = notification.usersName+" 覺得你的回覆有用。";
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

                    String landingUrl = resolvePostLandingUrl(post.id, post.community.id, post.community.communityType);

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
                        jsonMap.put("onClick", landingUrl);
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
                        jsonMap.put("onClick", landingUrl);
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

                    String landingUrl = resolveQnALandingUrl(post.id, post.community.id, post.community.communityType);

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
                        jsonMap.put("onClick", landingUrl);
                        notification.URLs = Json.stringify(Json.toJson(jsonMap));
                        notification.socialActionID = socialAction.target;
                        notification.addToList(User.findById(socialAction.actor));
                        notification.message = socialAction.actorname+" 回應了你的話題。";
                        notification.status = 0;
                        notification.setUpdatedDate(new Date());
                        notification.save();
                    } else {
                        notification.count++;
                        notification.addToList(User.findById(socialAction.actor));
                        jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                        jsonMap.put("onClick", landingUrl);
                        notification.URLs = Json.stringify(Json.toJson(jsonMap));
                        notification.message = notification.usersName+" 回應了你的話題。";
                        notification.status = 0;
                        notification.merge();
                    }
                }
				break;	
			}
		} 
	}
}
