package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import models.Icon.IconType;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;


@Entity
public class EmotIcons {
	
	
		@Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    public Long id;
	    
	    public String name;
	    
	    public String url;
	    
	    public EmotIcons(){}
	    
	    public EmotIcons(String name, String url) {
	        this.name = name;
	        this.url = url;
	    }
	    
	    public static List<EmotIcons> getIcons() {
	    	Query q = JPA.em().createQuery("Select i from EmotIcons i ");
	    	return (List<EmotIcons>)q.getResultList();
	    }
	    
	    public String getName() {
	    	return name;
	    }
	    
	    public String getUrl() {
	    	return url;
	    }

	    @Transactional
	    public void save() {
	        JPA.em().persist(this);
	        JPA.em().flush();	  
	    }
	      
	    @Transactional
	    public void delete() {
	        JPA.em().remove(this);
	    }
	    
	    @Transactional
	    public void merge() {
	        JPA.em().merge(this);
	    }
	    
	    @Transactional
	    public void refresh() {
	        JPA.em().refresh(this);
	    }
}
