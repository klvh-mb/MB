package models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Transient;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import com.mnt.SocialActivity;

import domain.AuditListener;
import domain.Creatable;
import domain.SocialObjectType;
import domain.Updatable;


/**
 *  This class is analogous of Weighted Graph Data Structure. 
 *  In Weighted four entities are involved:
 * 
 *  Node A (subject / actor)
 *  Edge   (verb / action)
 *  Node B (object / target)
 *  Weight (adverb / degree) 
 * 
 *  Example 1: 
 *  Statement: Joe Lee Rated 4 Start to Post of John Voo.
 *  
 *  Graph will be:
 *  Actor  : Joe Lee,
 *  Target : John Voo's Post,
 *  Action : Ratted,
 *  Weight : 4
 *  
 *  Not all edge need to have weight. E.g Joe Lee Liked to Post of John Voo.
 *  In this example we don't have degree of likeness. 
 *  
 *  E.g Joe Lee Strongly(Adverb) Recommend John Voo's Novel.
 *  Actor  : Joe Lee,
 *  Target : John Voo's Novel,
 *  Action : Recommend,
 *  Weight : Strongly(may be 5)
 *  
 *  @author jagbirs
 *
 */

@Entity
@EntityListeners(AuditListener.class)
public class SocialRelation extends domain.Entity implements Serializable, Creatable, Updatable  {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	
	public Long actor;
	@Enumerated(EnumType.STRING)
	public SocialObjectType actorType;
	
	
	@Enumerated(EnumType.STRING)
	public Action action;
	
	public Integer relationWeight;
	
	@Enumerated(EnumType.STRING)
    public ActionType actionType;
    
    static public enum ActionType {
    		MESSAGE_SEND,
            FRIEND_REQUESTED,
            JOIN_REQUESTED,
            RELATIONSHIP_REQUESTED,
            GRANT,
            UNFRIEND,
            LEAVE_COMMUNITY,
            INVITE_REQUESTED
    }

	
	public Long target;
	@Enumerated(EnumType.STRING)
	public SocialObjectType targetType;

	@Transient
	public String targetname;
	@Transient
	public Long targetOwner;
	@Transient
	public String actorname;
	
	static public enum Action {
		
		LIKED,
		RATTED,
		POSTED_ON,
		POSTED,
		COMMENTED,
		BLOCKED,
		ADDED,
		REQUEST,
		FRIEND,
		CHANGED,
		DELETED,
		SHARED,
		FOLLOWS,
		RECOMMENDED,
		FATHER,
		MOTHER, 
		BROTHER,
		SISTER,
		HUSBAND,
		WIFE,
		MESSAGE_SEND,
		MEMBER,
		POSTED_QUESTION,
		POSTED_ANSWER,
		ANSWERED;
	}
	
	
	

	public SocialRelation(){}
	
	public SocialRelation(Long id, SocialObject actor, Action action,
			Integer weight, SocialObject target) {
		super();
		this.id = id;
		this.actor = actor.id;
		this.actorname = actor.name;
		this.action = action;
		this.relationWeight = weight;
		this.target = target.id;
		this.targetname = target.name;
		this.targetOwner = target.owner == null ? null :target.owner.id;
	}
	
	public SocialRelation(SocialObject actor, SocialObject target) {
		this.actor = actor.id;
		this.actorname = actor.name;
		this.target = target.id;
		this.targetname = target.name;
		this.targetOwner = target.owner == null ? null :target.owner.id;
		this.targetType = target.objectType;
		this.actorType = actor.objectType;
	}
	
	@Transactional
	public void validateUniquenessAndCreate() {
		Query q = JPA.em().createQuery("Select sa from SocialRelation sa where actor = ?1 and action = ?2 and target = ?3 and actorType = ?4 and targetType = ?5");
		q.setParameter(1, this.actor);
		q.setParameter(2, this.action);
		q.setParameter(3, this.target);
		q.setParameter(4, this.actorType);
		q.setParameter(5, this.targetType);
		if(q.getResultList().size() > 0 ) {
			// Already liked ; Any logic !
			return;
		} else {
			save();
		}
	}
	
	// NOTE: Caution, call this method when target and actor pair is one to one.
	@Transactional
	public void createOrUpdateForTargetAndActorPair() {
		Query q = JPA.em().createQuery("Select sa from SocialRelation sa where actor = ?1 and target = ?2 and actorType = ?3 and targetType = ?4");
		q.setParameter(1, this.actor);
		q.setParameter(2, this.target);
		q.setParameter(3, this.actorType);
		q.setParameter(4, this.targetType);
		SocialRelation sa = null;
		
		try{
			sa = (SocialRelation) q.getSingleResult();
		}
		catch (NoResultException nre){
		}
		
		if(sa == null ) {
			save();
		} else {
			sa.actionType = this.actionType;
			sa.merge();
		}
	}
	
	@Override
	public void postSave() {
		SocialActivity.handle(this);
	}
	
	public <T> T getTargetObject(Class<T> claszz){
		String query = "Select c from " + claszz.getName() + " c where id = ?1";
		Query q = JPA.em().createQuery(query);
		q.setParameter(1, this.target);
		return (T)q.getSingleResult();
	}
	
	public SocialObject getTargetObject(){
		if(this.targetType == SocialObjectType.USER) return getTargetObject(User.class); 
		if(this.targetType == SocialObjectType.COMMUNITY) return getTargetObject(Community.class);
		return getTargetObject(User.class); 
	}
	
	public SocialObject getActorObject(){
		if(this.actorType == SocialObjectType.USER) return getActorObject(User.class); 
		if(this.actorType == SocialObjectType.COMMUNITY) return getActorObject(Community.class);
		return getActorObject(User.class); 
	}
	
	public <T> T getActorObject(Class<T> claszz){
		String query = "Select c from " + claszz.getName() + " c where id = ?1";
		Query q = JPA.em().createQuery(query);
		q.setParameter(1, this.actor);
		return (T)q.getSingleResult();
	}
	

}
