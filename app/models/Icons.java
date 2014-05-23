package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Icons {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
	public String url;
	
	public Icons(){}
	
	public Icons(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public static List<Icons> getAllIcons() {
		Query q = JPA.em().createQuery("Select i from Icons i");
		return (List<Icons>)q.getResultList();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
