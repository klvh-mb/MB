package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import play.db.jpa.JPA;

@Entity
public class Icons {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
	public String url;
	
	public static List<Icons> getAllIcons() {
		Query q = JPA.em().createQuery("Select i from Icons i");
		return (List<Icons>)q.getResultList();
	}
}
