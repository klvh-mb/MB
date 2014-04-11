package models;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import models.SocialRelation.Action;
import models.SocialRelation.ActionType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.db.jpa.JPA;

import com.google.common.base.Objects;
import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.exception.SocialObjectNotLikableException;
import com.mnt.exception.SocialObjectNotPostableException;
import com.mnt.utils.MailJob;
import com.mnt.utils.MailJob.Mail.Body;

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

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditListener.class)
public abstract class SocialObject extends domain.Entity implements
		Serializable, Creatable, Updatable, Commentable, Likeable, Postable,
		Joinable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@Enumerated(EnumType.STRING)
	public SocialObjectType objectType;

	public String name;

	@JsonIgnore
	@ManyToOne
	public SocialObject owner;

	protected final void recordLike(User user) {
		SocialRelation action = new SocialRelation();
		action.action = SocialRelation.Action.LIKED;
		action.target = this;
		action.actor = user;
		action.validateUniquenessAndCreate();
	}

	protected final void recordJoinRequest(User user) {
		SocialRelation action = new SocialRelation();
		action.actionType = SocialRelation.ActionType.JOIN_REQUESTED;
		action.target = this;
		action.actor = user;
		action.createOrUpdateForTargetAndActorPair();
	}
	
	protected final void beMemberOfCommunity(User user) {
		SocialRelation action = new SocialRelation();
		action.action = SocialRelation.Action.MEMBER;
		action.target = this;
		action.actor = user;
		action.actionType = SocialRelation.ActionType.GRANT;
		String message = "Congratulation "+user.name+","+"\n"+" You are now mwmber of "+this.name+" Community.";
		MailJob.sendMail("Some subject",new Body(message), user.email);
		action.validateUniquenessAndCreate();
	}

	protected final void recordJoinRequestAccepted(User user) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
		q.setParameter(1, user);
		q.setParameter(2, this);
		q.setParameter(3, SocialRelation.ActionType.JOIN_REQUESTED);

		SocialRelation action = (SocialRelation) q.getSingleResult();
		action.actionType = SocialRelation.ActionType.GRANT;
		action.action = SocialRelation.Action.MEMBER;
		String message = "Congratulation "+user.name+","+"\n"+" You are now mwmber of "+this.name+" Community.";
		MailJob.sendMail("Some subject",new Body(message), user.email);
		action.save();

	}
	
	protected final void recordInviteRequestAccepted(User user) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
		q.setParameter(1, this);
		q.setParameter(2, user);
		q.setParameter(3, SocialRelation.ActionType.INVITE_REQUESTED);

		SocialRelation action = (SocialRelation) q.getSingleResult();
		action.actionType = SocialRelation.ActionType.GRANT;
		action.action = SocialRelation.Action.MEMBER;
		action.save();

	}

	protected final void recordFriendRequest(User invitee) {
		SocialRelation action = new SocialRelation();
		action.actionType = SocialRelation.ActionType.FRIEND_REQUESTED;
		action.target = invitee;
		action.actor = this;
		action.validateUniquenessAndCreate();
	}

	protected final void recordFriendRequestAccepted(User user) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
		q.setParameter(1, user);
		q.setParameter(2, this);
		q.setParameter(3, SocialRelation.ActionType.FRIEND_REQUESTED);

		SocialRelation action = (SocialRelation) q.getSingleResult();
		action.actionType = SocialRelation.ActionType.GRANT;
		action.action = SocialRelation.Action.FRIEND;
		action.save();
	}

	protected final void recordRelationshipRequest(User user, Action relation) {
		SocialRelation action = new SocialRelation();
		action.actionType = SocialRelation.ActionType.RELATIONSHIP_REQUESTED;
		action.action = relation;
		action.target = user;
		action.actor = this;
		action.validateUniquenessAndCreate();
	}

	protected final void recordRelationshipRequestAccepted(User user,
			Action relation) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
		q.setParameter(1, user);
		q.setParameter(2, this);
		q.setParameter(3, SocialRelation.ActionType.RELATIONSHIP_REQUESTED);
		SocialRelation action = (SocialRelation) q.getSingleResult();
		action.actionType = SocialRelation.ActionType.GRANT;
		action.action = relation;
		action.save();

	}

	protected final void recordPostOn(User user) {
		SocialRelation action = new SocialRelation();
		action.action = SocialRelation.Action.POSTED_ON;
		action.target = this;
		action.actor = user;
		action.save();
	}

	protected final void recordPost(SocialObject user) {
		SocialRelation action = new SocialRelation();
		action.action = SocialRelation.Action.POSTED;
		action.target = this;
		action.actor = user;
		action.save();
	}

	protected void recordQnA(SocialObject user) {
		SocialRelation action = new SocialRelation();
		action.action = SocialRelation.Action.POSTED_QUESTION;
		action.target = this;
		action.actor = user;
		action.save();
	}

	protected void recordCommentOnCommunityPost(SocialObject user) {
		SocialRelation action = new SocialRelation();
		action.action = SocialRelation.Action.COMMENTED;
		action.target = this;
		action.actor = user;
		action.save();
	}
	
	protected void recordAnswerOnCommunityPost(SocialObject user) {
		SocialRelation action = new SocialRelation();
		action.action = SocialRelation.Action.ANSWERED;
		action.target = this;
		action.actor = user;
		action.save();
	}

	protected void recordAddedPhoto(SocialObject user) {
		SocialRelation action = new SocialRelation();
		action.action = SocialRelation.Action.ADDED;
		action.target = this;
		action.actor = user;
		action.save();
	}

	protected final void recordInviteRequestByCommunity(User invitee) {
		SocialRelation action = new SocialRelation();
		action.actionType = SocialRelation.ActionType.INVITE_REQUESTED;
		action.target = invitee;
		action.actor = this;
		action.validateUniquenessAndCreate();
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

	public void onLike(User user) throws SocialObjectNotLikableException {
		throw new SocialObjectNotLikableException(
				"Please make sure Social Object you are liking is Likable");
	}
	
	public SocialObject onComment(User user, String body, CommentType type) throws SocialObjectNotCommentableException {
		throw new SocialObjectNotCommentableException("Please make sure Social Object you are commenting is Commentable");
	}

	public SocialObject onPost(User user, String body, PostType type)
			throws SocialObjectNotPostableException {
		throw new SocialObjectNotPostableException(
				"Please make sure Social Object you are posting  is Postable");
	}

	public void onJoinRequest(User user)
			throws SocialObjectNotJoinableException {
		throw new SocialObjectNotJoinableException(
				"Please make sure Social Object you are joining  is Joinable");
	}

	public void onJoinRequestAccepted(User toBeMemeber)
			throws SocialObjectNotJoinableException {
		throw new SocialObjectNotJoinableException(
				"Please make sure Social Object you are joining  is Joinable");
	}
	
	public void onInviteRequestAccepted(User toBeMemeber)
			throws SocialObjectNotJoinableException {
		throw new SocialObjectNotJoinableException(
				"Please make sure Social Object you are joining  is Joinable");
	}

}
