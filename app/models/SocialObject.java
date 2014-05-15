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
import javax.persistence.Query;

import models.SocialRelation.Action;

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

	protected final void recordLike(User user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.LIKED;
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
		action.validateUniquenessAndCreate();
	}
	
	protected final void beMemberOfOpenCommunity(User user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.MEMBER;
		action.actionType = SocialRelation.ActionType.GRANT;
		String message = "Congratulation "+user.name+","+"\n"+" You are now member of "+this.name+" Community.";
		MailJob.sendMail("Some subject",new Body(message), user.email);
		action.memberJoinedOpenCommunity = true;
		action.validateUniquenessAndCreate();
	}

	protected final void recordJoinRequestAccepted(User user) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
		q.setParameter(1, user.id);
		q.setParameter(2, this.id);
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
		q.setParameter(1, user.id);
		q.setParameter(2, this.id);
		q.setParameter(3, SocialRelation.ActionType.INVITE_REQUESTED);

		SocialRelation action = (SocialRelation) q.getSingleResult();
		action.actionType = SocialRelation.ActionType.GRANT;
		action.action = SocialRelation.Action.MEMBER;
		action.save();

	}

	protected final void recordFriendRequest(User invitee) {
		SocialRelation action = new SocialRelation(this, invitee);
		action.actionType = SocialRelation.ActionType.FRIEND_REQUESTED;
		action.validateUniquenessAndCreate();
	}

	protected final void recordFriendRequestAccepted(User user) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
		q.setParameter(1, user.id);
		q.setParameter(2, this.id);
		q.setParameter(3, SocialRelation.ActionType.FRIEND_REQUESTED);

		SocialRelation action = (SocialRelation) q.getSingleResult();
		action.actionType = SocialRelation.ActionType.GRANT;
		action.action = SocialRelation.Action.FRIEND;
		action.save();
	}

	protected final void recordRelationshipRequest(User user, Action relation) {
		SocialRelation action = new SocialRelation(this,user);
		action.actionType = SocialRelation.ActionType.RELATIONSHIP_REQUESTED;
		action.action = relation;
		action.validateUniquenessAndCreate();
	}

	protected final void recordRelationshipRequestAccepted(User user,
			Action relation) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
		q.setParameter(1, user.id);
		q.setParameter(2, this.id);
		q.setParameter(3, SocialRelation.ActionType.RELATIONSHIP_REQUESTED);
		SocialRelation action = (SocialRelation) q.getSingleResult();
		action.actionType = SocialRelation.ActionType.GRANT;
		action.action = relation;
		action.save();

	}

	protected final void recordPostOn(User user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.POSTED_ON;
		action.save();
	}

	protected final void recordPost(SocialObject user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.POSTED;
		action.save();
	}

	protected void recordQnA(SocialObject user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.POSTED_QUESTION;
		action.save();
	}

	protected void recordCommentOnCommunityPost(SocialObject user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.COMMENTED;
		action.save();
	}
	
	protected void recordAnswerOnCommunityPost(SocialObject user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.ANSWERED;
		action.save();
	}

	protected void recordAddedPhoto(SocialObject user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.ADDED;
		action.save();
	}

	protected final void recordInviteRequestByCommunity(User invitee) {
		SocialRelation action = new SocialRelation(invitee, this);
		action.actionType = SocialRelation.ActionType.INVITE_REQUESTED;
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
