package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import org.hibernate.annotations.Index;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Icon {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    @Index(name = "Name") 
    public String name;
    
    @Enumerated(EnumType.STRING)
    public IconType iconType;
    
    public String url;
    
    public static enum IconType {
        COMMUNITY_GENERAL
    }
    
    public Icon(){}
    
    public Icon(String name, IconType iconType, String url) {
        this.name = name;
        this.iconType = iconType;
        this.url = url;
    }
    
    public static List<Icon> getCommunityIcons() {
        return getIcons(IconType.COMMUNITY_GENERAL);
    }
    
    public static List<Icon> getIcons(IconType iconType) {
    	Query q = JPA.em().createQuery("Select i from Icon i where iconType = ?1");
    	q.setParameter(1, iconType);
    	return (List<Icon>)q.getResultList();
    }
    
    public String getName() {
    	return name;
    }
    
    public IconType getIconType() {
        return iconType;
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
