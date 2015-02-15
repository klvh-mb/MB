package models;

import java.util.List;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.*;

import common.cache.CommunityMetaCache;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/9/14
 * Time: 11:53 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class CommunityCategory {

    public static enum CategoryType {
		BUSINESS,
        SOCIAL
	}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

    @Enumerated(EnumType.ORDINAL)
	public CategoryType categoryType = CategoryType.BUSINESS;

	public String name;

	public int seq;

	public boolean deleted = false;

    /**
     * Ctor
     */
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

    // For cache load
    public static List<CommunityCategory> loadAllBusinessCategories() {
        Query q = JPA.em().createQuery("Select c from CommunityCategory c where categoryType = ?1 and deleted = false order by seq");
        q.setParameter(1, CategoryType.BUSINESS);
        return (List<CommunityCategory>)q.getResultList();
    }

    public static List<CommunityCategory> loadAllSocialCategories() {
        Query q = JPA.em().createQuery("Select c from CommunityCategory c where categoryType = ?1 and deleted = false order by seq");
        q.setParameter(1, CategoryType.SOCIAL);
        return (List<CommunityCategory>)q.getResultList();
    }

    // For controllers
    public static List<CommunityCategory> getAllBusinessCategories() {
        return CommunityMetaCache.getAllBusinessCategories();
    }

    public static List<CommunityCategory> getAllSocialCategories() {
        return CommunityMetaCache.getAllSocialCategories();
    }
}
