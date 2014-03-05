
package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.F;
import akka.japi.Function;
import be.objectify.deadbolt.core.models.Role;

@Entity
public class SecurityRole  extends domain.Entity implements Role {
	/**
	 * 
	 */
	
	public SecurityRole() {}
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;

	public String roleName;

	
	@Override
	public String getName() {
		return roleName;
	}

	public static SecurityRole findByRoleName(String roleName) {
		Query q = JPA.em().createQuery("SELECT l from SecurityRole l  where roleName = ?1");
		q.setParameter(1, roleName);
		try {
			return (SecurityRole) q.getSingleResult();
		} catch(NoResultException e) {
			return null;
		}
	}
	
	public static int findRowCount() {
		Query q = JPA.em().createQuery("SELECT l from SecurityRole l");
		return q.getMaxResults();
	}
}
