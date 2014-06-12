package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.db.jpa.JPA;
@Entity
public class UserInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long user_id;
	
	public String parent_birth_year;
	
	public String district;
	
	public String parent_type;
	
	public String bb_gender;
	
	public String bb_birth_year;
	
	public String bb_birth_month;
	
	public String bb_birth_day;
	
	public static boolean findByUserId(Long id) {
		Query q = JPA.em().createQuery("SELECT u FROM UserInfo u where user_id = ?1");
		q.setParameter(1, id);
		try {
			q.getSingleResult();
			return true;
		}
		catch(NoResultException e) {
			return false;
		}
	}

	public void save(User localUser) {
		user_id = localUser.id;
		JPA.em().persist(this);
		JPA.em().flush();
		
	}
}
