package models;

import java.io.Serializable;

import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import common.cache.FriendCache;
import models.SocialRelation.Action;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

import com.google.common.base.Objects;
import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.exception.SocialObjectNotLikableException;
import com.mnt.exception.SocialObjectNotPostableException;

import domain.AuditListener;
import domain.CommentType;
import domain.Commentable;
import domain.Creatable;
import domain.Joinable;
import domain.Likeable;
import domain.PostType;
import domain.Postable;
import domain.SocialObjectType;
import domain.Updatable;

//@Entity
//@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditListener.class)
@MappedSuperclass
public abstract class SocialObject extends domain.Entity implements
		Serializable, Creatable, Updatable, Commentable, Likeable, Postable,
		Joinable {

	@Id
	//MySQL5Dialect does not support sequence
	//@GeneratedValue(generator = "social-sequence")
	//@GenericGenerator(name = "social-sequence",strategy = "com.mnt.persist.generator.SocialSequenceGenerator")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@Enumerated(EnumType.STRING)
	public SocialObjectType objectType;

	public String name;

	@JsonIgnore
	@ManyToOne
	public User owner;

	/*
	 * Folder
	 *     System albums will not generate socialAction onCreate and should be always public 
	 *     (the privacy is set on the single inner elements)
	 *     
	 * Community
	 *     System communities will have special treatment e.g. targeting, privacy
     */
	@Required
    public Boolean system = false;
    
	@Required
    public Boolean deleted = false;     // social objects should always be soft delete
	
	protected final void recordLike(User user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.LIKED;
		action.validateUniquenessAndCreate();
	}
	
	protected final void recordBookmark(User user) {
		SecondarySocialRelation action = new SecondarySocialRelation(user, this);
		action.action = SecondarySocialRelation.Action.BOOKMARKED;
		action.validateUniquenessAndCreate();
	}
	
	protected final void recordJoinRequest(User user) {
		SocialRelation action = new SocialRelation(user, this);
		action.actionType = SocialRelation.ActionType.JOIN_REQUESTED;
		action.createOrUpdateForTargetAndActorPair();
	}
	
	protected final void ownerMemberOfCommunity(User user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.MEMBER;
		action.actionType = SocialRelation.ActionType.GRANT;
		action.isPostSave = false;
		action.ensureUniqueAndCreate();
	}
	
	protected final void beMemberOfOpenCommunity(User user) {
	    // Join community
	    SocialRelation action = new SocialRelation(user, this);
        action.action = SocialRelation.Action.MEMBER;
        action.actionType = SocialRelation.ActionType.GRANT;
        action.memberJoinedOpenCommunity = true;

        if (action.ensureUniqueAndCreate()) {
            // save community affinity
            UserCommunityAffinity.onJoinedCommunity(user.id, this.id);

            // skip email
            //String message = "Congratulation " + user.name + "," + "\n" + " You are now member of " + this.name + " Community.";
            //MailJob.sendMail("Some subject", new Body(message), user.email);
        }
        
        // Clear up invite request if any
        SocialRelation request = getInviteRequest(user);
        if (request != null) {
            request.delete();
        }
	}

	protected final void recordJoinRequestAccepted(User user) {
		// must have a join request to proceed
        SocialRelation request = getJoinRequest(user);
        if (request == null) {
            return;
        }
        
        // use existing join request to capture MEMBER relationship
        request.action = SocialRelation.Action.MEMBER;
        request.actionType = SocialRelation.ActionType.GRANT;
        request.ensureUniqueAndCreate();
        
        // save community affinity
        UserCommunityAffinity.onJoinedCommunity(user.id, this.id);

        // skip email
        //String message = "Congratulation " + user.name + "," + "\n" + " You are now member of " + this.name + " Community.";
        //MailJob.sendMail("Some subject", new Body(message), user.email);
	}
	
	protected final void recordInviteRequestAccepted(User user) {
	    // must have a join request to proceed
        SocialRelation request = getInviteRequest(user);
        if (request == null) {
            return;
        }
        
        // use existing join request to capture MEMBER relationship
        request.action = SocialRelation.Action.MEMBER;
        request.actionType = SocialRelation.ActionType.GRANT;

        // save community affinity
        UserCommunityAffinity.onJoinedCommunity(user.id, this.id);
	}

	protected final SocialRelation getJoinRequest(User user) {
        return getRequest(user, SocialRelation.ActionType.JOIN_REQUESTED);
    }
    
    protected final SocialRelation getInviteRequest(User user) {
        return getRequest(user, SocialRelation.ActionType.INVITE_REQUESTED);
    }
    
    protected final SocialRelation getRequest(User user, SocialRelation.ActionType actionType) {
        Query q = JPA.em().createQuery(
                "SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
        q.setParameter(1, user.id);
        q.setParameter(2, this.id);
        q.setParameter(3, actionType);

        try {
            SocialRelation request = (SocialRelation) q.getSingleResult();
            return request;
        } catch (NoResultException nre){
        }
        return null;
    }
    
	protected final void recordFriendRequest(User invitee) {
		SocialRelation action = new SocialRelation(this, invitee);
		action.actionType = SocialRelation.ActionType.FRIEND_REQUESTED;
		action.ensureUniqueAndCreate();
	}

	protected final void recordFriendRequestAccepted(User user) {
		Query q = JPA.em().createQuery(
		        "SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
		q.setParameter(1, user.id);
		q.setParameter(2, this.id);
		q.setParameter(3, SocialRelation.ActionType.FRIEND_REQUESTED);

		SocialRelation action = (SocialRelation) q.getSingleResult();
		action.actionType = SocialRelation.ActionType.GRANT;
		action.action = SocialRelation.Action.FRIEND;
        // update SocialRelation to GRANT
		action.save();

        // update Friends cache
        FriendCache.onBecomeFriend(user.id, this.id);
	}

	protected final void recordRelationshipRequest(User user, Action relation) {
		SocialRelation action = new SocialRelation(this,user);
		action.actionType = SocialRelation.ActionType.RELATIONSHIP_REQUESTED;
		action.action = relation;
		action.ensureUniqueAndCreate();
	}

	protected final void recordRelationshipRequestAccepted(User user,
			Action relation) {
		Query q = JPA.em().createQuery(
		        "SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
		q.setParameter(1, user.id);
		q.setParameter(2, this.id);
		q.setParameter(3, SocialRelation.ActionType.RELATIONSHIP_REQUESTED);
		SocialRelation action = (SocialRelation) q.getSingleResult();
		action.actionType = SocialRelation.ActionType.GRANT;
		action.action = relation;
		action.save();
	}

	protected final void recordPost(SocialObject user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.POSTED;
		action.postSave();
	}

	protected void recordQnA(SocialObject user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.POSTED_QUESTION;
		action.postSave();
	}

	protected void recordCommentOnCommunityPost(SocialObject user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.COMMENTED;
		action.postSave();
	}
	
	protected void recordCommentOnArticle(SocialObject user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.COMMENTED;
		 JPA.em().persist(this);
		  JPA.em().flush();
		  postSave();
	}
	
	protected void recordAnswerOnCommunityPost(SocialObject user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.ANSWERED;
		action.postSave();
	}

	protected void recordAddedPhoto(SocialObject user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.ADDED;
		action.save();
	}

	protected final void recordInviteRequestByCommunity(User invitee) {
		SocialRelation action = new SocialRelation(invitee, this);
		action.actionType = SocialRelation.ActionType.INVITE_REQUESTED;
		action.ensureUniqueAndCreate();
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(name, objectType, id);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof SocialObject) {
			final SocialObject other = (SocialObject) obj;
			return new EqualsBuilder().append(name, other.name)
					.append(id, other.id).append(objectType, other.objectType)
					.isEquals();
		} else {
			return false;
		}
	}

	public void onLikedBy(User user) throws SocialObjectNotLikableException {
		throw new SocialObjectNotLikableException(
				"Please make sure Social Object you are liking is Likable");
	}
	
	public void onUnlikedBy(User user) throws SocialObjectNotLikableException {
		throw new SocialObjectNotLikableException(
				"Please make sure Social Object you are unliking is Likable");
	}
	
	public SocialObject onComment(User user, String body, CommentType type) throws SocialObjectNotCommentableException {
		throw new SocialObjectNotCommentableException("Please make sure Social Object you are commenting is Commentable");
	}

	public void onDeleteComment(User user, String body, CommentType type) throws SocialObjectNotCommentableException {
        throw new SocialObjectNotCommentableException("Please make sure Social Object you are deleteing comment is Commentable");
    }
	
	public SocialObject onPost(User user, String title, String body, PostType type)
			throws SocialObjectNotPostableException {
		throw new SocialObjectNotPostableException(
				"Please make sure Social Object you are posting  is Postable");
	}

	public void onJoinRequest(User user)
			throws SocialObjectNotJoinableException {
		throw new SocialObjectNotJoinableException(
				"Please make sure Social Object you are joining  is Joinable");
	}

	public void onJoinRequestAccepted(User toBeMember)
			throws SocialObjectNotJoinableException {
		throw new SocialObjectNotJoinableException(
				"Please make sure Social Object you are joining  is Joinable");
	}
	
	public void onInviteRequestAccepted(User toBeMember)
			throws SocialObjectNotJoinableException {
		throw new SocialObjectNotJoinableException(
				"Please make sure Social Object you are joining  is Joinable");
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SocialObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(SocialObjectType objectType) {
		this.objectType = objectType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
