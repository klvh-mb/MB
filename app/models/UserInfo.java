package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import common.model.TargetGender;

import play.db.jpa.JPA;

@Entity
public class UserInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String parent_birth_year;
	
	@Enumerated(EnumType.STRING)
	public TargetGender parent_gender;
	
	public String district;
	
	@Enumerated(EnumType.STRING)
	public ParentType parent_type;
	
	@Enumerated(EnumType.STRING)
	public TargetGender bb_gender;
	
	public String bb_birth_year;
	
	public String bb_birth_month;
	
	public String bb_birth_day;
	
	public static enum ParentType {
	    MOM,
	    DAD,
	    SOON_MOM,
	    SOON_DAD,
	    NA
	}
	
	public UserInfo() {
	}
	
	public void merge(UserInfo userInfo) {
	    this.parent_birth_year = userInfo.parent_birth_year;
	    this.district = userInfo.district;
	    this.parent_type = userInfo.parent_type;
	    this.bb_gender = userInfo.bb_gender;
	    this.bb_birth_year = userInfo.bb_birth_year;
	    this.bb_birth_month = userInfo.bb_birth_month;
	    this.bb_birth_day = userInfo.bb_birth_day;
	    
	    if (ParentType.MOM.equals(parent_type) || ParentType.SOON_MOM.equals(parent_type)) {
            this.parent_gender = TargetGender.Female;
        } else if (ParentType.DAD.equals(parent_type) || ParentType.SOON_DAD.equals(parent_type)) {
            this.parent_gender = TargetGender.Male;
        } else {
            this.parent_gender = TargetGender.Female;   // default
        }
	}
	
	public static boolean findByUserId(Long id) {
		Query q = JPA.em().createQuery("SELECT u FROM UserInfo u where user_id = ?1");
		q.setParameter(1, id);
		try {
			q.getSingleResult();
			return true;
		} catch(NoResultException e) {
			return false;
		}
	}

	public void save() {
		JPA.em().persist(this);
		JPA.em().flush();
	}
}