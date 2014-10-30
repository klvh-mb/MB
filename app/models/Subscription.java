package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;

@Entity
public class Subscription {

    public Subscription() {}
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public String name;
    
    public String HTMLtemplate;
    
    public String TXTtemplate;

    public static List<Subscription> getAllSubscription() {
        Query q = JPA.em().createQuery("Select a from Subscription a ");
        return (List<Subscription>)q.getResultList();       
    }
    
    public static Subscription findById(Long id) {
        try { 
            Query q = JPA.em().createQuery("SELECT u FROM Subscription u where id = ?1");
            q.setParameter(1, id);
            return (Subscription) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } 
    }
}