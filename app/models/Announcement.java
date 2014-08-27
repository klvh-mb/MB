package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * Announcement are not frequently updated. Put in the cache and set ttl to e.g. 10mins.
 */
@Entity
public class Announcement  {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    @Required
    @Column(length=2000)
    public String title;
    
    @Column(length=2000)
    public String description;
    
    public String icon;
    
    public String url;
    
    @Enumerated(EnumType.STRING)
    public AnnouncementType announcementType;
    
    public Date fromDate;
    
    public Date toDate;
    
    public static enum AnnouncementType {
        GENERAL,
        TOP_INFO,
        TOP_ALERT,
        PROMOTION
    }
    
    public Announcement() {}
    
    public Announcement(String title, Date toDate) {
        this(title, "", "", "", AnnouncementType.GENERAL, new Date(), toDate);
    }
    
    public Announcement(String title, AnnouncementType announcementType, Date toDate) {
        this(title, "", "", "", announcementType, new Date(), toDate);
    }

    public Announcement(String title, String description, String icon, String url, 
            AnnouncementType announcementType, Date fromDate, Date toDate) {
       this.title = title;
       this.description = description;
       this.icon = icon;
       this.url = url;
       this.announcementType = announcementType;
       this.fromDate = fromDate;
       this.toDate = toDate;
    }
    
    public static List<Announcement> getAnnouncements(AnnouncementType announcementType) {
        Query q = JPA.em().createQuery("select a from Announcement a where announcementType = ?1 and fromDate < NOW() and toDate > NOW()");
        q.setParameter(1, announcementType);
        return (List<Announcement>)q.getResultList();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Announcement) {
            final Announcement other = (Announcement) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
    
    @Override
    public  String toString() {
        return "[" + announcementType + "|" + title + "|" + description + "|" + 
                icon + "|"  + url + "|" + fromDate + "|" + toDate + "]";
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
