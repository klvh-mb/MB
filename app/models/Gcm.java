package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Gcm {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	public String reg_id;

	public Long user_id;

	public static Gcm findById(Long id) {
		try { 
			Query q = JPA.em().createQuery("SELECT g FROM Gcm g where id = ?1");
			q.setParameter(1, id);
			return (Gcm) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} 
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReg_id() {
		return reg_id;
	}

	public void setReg_id(String reg_id) {
		this.reg_id = reg_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public static Gcm getGcmByUser_id(String key, Long user_id) {
		try { 
			Query q = JPA.em().createQuery("SELECT g FROM Gcm g where user_id = ?1");
			q.setParameter(1, user_id);
			return (Gcm) q.getSingleResult();
		} catch (NoResultException e) {
			Gcm gcm = new Gcm();
			gcm.setReg_id(key);
			gcm.setUser_id(user_id);
			gcm.save();
			return gcm;
		} 
	}

	public static Gcm getGcmByUser_id(Long user_id) {
		try { 
			Query q = JPA.em().createQuery("SELECT g FROM Gcm g where user_id = ?1");
			q.setParameter(1, user_id);
			return (Gcm) q.getSingleResult();
		} catch (NoResultException e) {
			return new Gcm();
		} 
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
