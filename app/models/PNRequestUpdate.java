package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.annotations.Index;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class PNRequestUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    @Index(name = "Name") 
    public String name;
    
    public Long districtId;
    
    public Long count = 0L;
    
    public PNRequestUpdate() {}
    
    public PNRequestUpdate(String name, Long districtId) {
        this.name = name;
        this.districtId = districtId;
        this.count = 1L;
    }
    
    public static PNRequestUpdate getPNRequestUpdate(String name, Long districtId) {
        try {
            Query q = JPA.em().createQuery("Select u from PNRequestUpdate u where name = ?1 and districtId = ?2");
            q.setParameter(1, name);
            q.setParameter(2, districtId);
            return (PNRequestUpdate)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public static List<PNRequestUpdate> getPNRequestUpdates() {
        try {
            Query q = JPA.em().createQuery("Select u from PNRequestUpdate u ");
            return (List<PNRequestUpdate>)q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<PNRequestUpdate>();
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
