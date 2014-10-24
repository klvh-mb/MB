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

    @Transactional
    public static List<TagWord> getTagWordsByCategory(TagCategory tagCategory, String tagCategoryId) {
        Query q = JPA.em().createQuery("Select t from TagWord t where " +
                "t.tagCategory = ?1 and t.tagCategoryId = ?2 and t.deleted = false");
		q.setParameter(1, tagCategory);
        q.setParameter(2, tagCategoryId);

		return (List<TagWord>)q.getResultList();
    }

    @Transactional
    public void updateSocialObjectCount(SocialObjectType socialObjectType) {
        socialObjectCount = TagWordScore.getSocialObjectCount(id, socialObjectType).intValue();
        save();
    }
}
