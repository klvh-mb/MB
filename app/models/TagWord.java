package models;


import javax.persistence.*;

import domain.SocialObjectType;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 17/10/14
 * Time: 11:55 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class TagWord extends TargetingSocialObject {
    private static play.api.Logger logger = play.api.Logger.apply(TagWord.class);

    public static enum TagCategory {
		ARTICLE
	}

    @Enumerated(EnumType.ORDINAL)
	public TagCategory tagCategory;

    public String tagCategoryId;

    public String displayWord;

    public String matchingWords;    // comma separated. Grouping of related words.

    public int socialObjectCount;

    public int noClicks;

    // Note: Targeting attributes from TargetingSocialObject

    /**
     * Ctor
     */
	public TagWord() {
        this.objectType = SocialObjectType.TAGWORD;
	}

    public static List<TagWord> getTagWordsByCategory(TagCategory tagCategory, String tagCategoryId) {
        Query q = JPA.em().createQuery("Select t from TagWord t where " +
                "t.tagCategory = ?1 and t.tagCategoryId = ?2 and t.deleted = false");
		q.setParameter(1, tagCategory);
        q.setParameter(2, tagCategoryId);

		return (List<TagWord>)q.getResultList();
    }

    public static List<TagWord> getTagWordsByCategoryByCount(TagCategory tagCategory, String tagCategoryId) {
        Query q = JPA.em().createQuery("Select t from TagWord t where " +
                "t.tagCategory = ?1 and t.tagCategoryId = ?2 and t.socialObjectCount > 0 and t.deleted = false order by t.socialObjectCount desc");
		q.setParameter(1, tagCategory);
        q.setParameter(2, tagCategoryId);

		return (List<TagWord>)q.getResultList();
    }

    @Transactional
    public void updateSocialObjectCount(SocialObjectType socialObjectType) {
        socialObjectCount = TagWordScore.getSocialObjectCount(id, socialObjectType).intValue();
        save();
    }

    @Transactional
    public static void incrementNoClicks(Long tagWordId) {
        Query query = JPA.em().createQuery("update TagWord t set t.noClicks = (t.noClicks+1) where t.id = ?1");
        query.setParameter(1, tagWordId);
        query.executeUpdate();
    }
}
