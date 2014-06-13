package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
    public String title;
    
    public String description;
    
    public String icon;
    
    @Enumerated(EnumType.STRING)
    public AnnouncementType announcementType;
    
    public Date fromDate;
    
    public Date toDate;
    
    @ManyToOne
    public Location location;       // most of the time just need to check location.LocationCode
    
    public static enum AnnouncementType {
        GENERAL,
        ALERT,
        PROMOTIONS
    }
    
    public Announcement() {}
    
    public Announcement(String title, Date toDate) {
        this(title, "", "", AnnouncementType.GENERAL, new Date(), toDate, null);
    }

    public Announcement(String title, Date toDate, Location location) {
        this(title, "", "", AnnouncementType.GENERAL, new Date(), toDate, location);
    }
    
    public Announcement(String title, AnnouncementType announcementType, Date toDate) {
        this(title, "", "", announcementType, new Date(), toDate, null);
    }

    public Announcement(String title, AnnouncementType announcementType, Date toDate, Location location) {
        this(title, "", "", announcementType, new Date(), toDate, location);
    }
    
    public Announcement(String title, String description, String icon, Date toDate) {
        this(title, description, icon, AnnouncementType.GENERAL, new Date(), toDate, null);
    }
    
    public Announcement(String title, String description, String icon, Date toDate, Location location) {
        this(title, description, icon, AnnouncementType.GENERAL, new Date(), toDate, location);
    }
    
    public Announcement(String title, String description, String icon, 
            AnnouncementType announcementType, Date fromDate, Date toDate, Location location) {
       this.title = title;
       this.description = description;
       this.icon = icon;
       this.announcementType = announcementType;
       this.fromDate = fromDate;
       this.toDate = toDate;
       this.location = location;
    }
    
    public static List<Announcement> getAnnouncements() {
        return getAnnouncements(null);
    }
    
    public static List<Announcement> getAnnouncements(Location location) {
        Query q = JPA.em().createQuery("select a from Announcement a where fromDate < NOW() and toDate > NOW()");
        List<Announcement> announcements = (List<Announcement>)q.getResultList();
        if (location == null) {
            return announcements;
        }
        
        List<Announcement> announcementsByLocation = new ArrayList<Announcement>();
        for (Announcement announcement : announcements) {
            if (location.locationCode == announcement.location.locationCode) {
                announcementsByLocation.add(announcement);
            }
        }
        return announcementsByLocation;
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
        return "[" + location.locationCode + "|" + announcementType + 
                "|" + title + "|" + description + "|" + icon + "|" + fromDate + 
                "|" + toDate + "]";
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
