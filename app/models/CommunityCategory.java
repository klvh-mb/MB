package models;

import java.util.List;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.*;

import common.cache.CommunityCategoryCache;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/9/14
 * Time: 11:53 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class CommunityCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	public String name;

	public int seq;

	public boolean deleted = false;
	
    public CommunityCategory() { }

    public CommunityCategory(String name, int seq) {
        this.name = name;
        this.seq = seq;
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
    
    public static List<CommunityCategory> loadAllCategories() {
        Query q = JPA.em().createQuery("Select c from CommunityCategory c where deleted = false order by seq");
        return (List<CommunityCategory>)q.getResultList();
    }
    
    public static List<CommunityCategory> getAllCategories() {
        return CommunityCategoryCache.getAllCategories();
    }
}
