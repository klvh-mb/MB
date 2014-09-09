package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import models.Announcement.AnnouncementType;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class TermsAndConditions {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
	
	public Date date;
	public String terms;
	public String privacy;
	
	@Transactional
	public static TermsAndConditions getTermsAndConditions() {
        Query q = JPA.em().createQuery("Select t from TermsAndConditions t where t.date = (Select Max(t.date) from TermsAndConditions t)");
        return (TermsAndConditions)q.getSingleResult();
    }
	
}
