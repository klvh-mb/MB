package models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import com.mnt.SocialActivity;

import domain.AuditListener;
import domain.Creatable;
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
public class SocialAction extends domain.Entity implements Serializable, Creatable, Updatable  {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public SocialObject actor;
	
	@Enumerated(EnumType.ORDINAL)
	public Action action;
	
	@Enumerated(EnumType.ORDINAL)
	public Rection reaction = Rection.NONE;
	
	public Double weight;
	
	@ManyToOne
	public SocialObject target;
	
	static public enum Action {
		
		LIKED,
		RATTED,
		POSTED_ON,
		POSTED,
		COMMENTED,
		BLOCKED,
		ADDED,
		CHANGED,
		DELETED,
		SHARED,
		FOLLOWS,
		RECOMMENDED,
		FRIEND_REQUESTED,
		JOIN_REQUESTED,
		RELATIONSHIP_REQUESTED,
		MESSAGE_SEND
	}
	
	
	public enum Rection {
		NONE,
		YES,
		NO,
		APPROVED,
		NOT_APPROVED,
		ACKNOWLEDGED
	}

	public SocialAction(){}
	
	public SocialAction(Long id, SocialObject actor, Action action,
			Double weight, SocialObject target) {
		super();
		this.id = id;
		this.actor = actor;
		this.action = action;
		this.weight = weight;
		this.target = target;
	}
	
	@Transactional
	public void validateUniquenessAndCreate() {
		Query q = JPA.em().createQuery("Select sa from SocialAction sa where actor = ?1 and action = ?2 and target = ?3");
		q.setParameter(1, this.actor);
		q.setParameter(2, this.action);
		q.setParameter(3, this.target);
		if(q.getResultList().size() > 0 ) {
			// Already liked ; Any logic !
			return;
		} else {
			save();
		}
	}
	
	@Override
	public void postSave() {
		SocialActivity.handle(this);
	}
	

}
