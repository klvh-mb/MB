package models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.data.validation.Constraints.Required;

import domain.AuditListener;
import domain.Creatable;
import domain.Updatable;

@Entity
@EntityListeners(AuditListener.class)
public class Notification  extends domain.Entity implements Serializable, Creatable, Updatable  {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	/*This notification is due to which Action.*/
	@ManyToOne
	public SocialAction socialAction;
	
	/*To whom this notification is intended for*/
	@OneToOne @Required
	public SocialObject recipetent;
	
	@Required
	public String message;
	
	public Boolean readed = false;
	
	

}
