package models;

import domain.*;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;
import java.util.List;

/**
 * Per tagWord, there would be multiple social object ids associated with the tag word.
 * They are to be displayed in the order of score.
 *
 * Created by IntelliJ IDEA.
 * Date: 18/10/14
 * Time: 12:06 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class TagWordScore extends domain.Entity {
    private static play.api.Logger logger = play.api.Logger.apply(TagWordScore.class);

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

    public Long tagWordId;

	@Enumerated(EnumType.STRING)
	public SocialObjectType socialObjectType;   // e.g. Article

    public Long socialObjectId;                 // e.g. Article id

    public int score;

    @Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;


    public static void createTagWordScore(Long tagWordId,
                                          SocialObjectType socialObjectType,
                                          Long socialObjectId,
                                          int score) {
        TagWordScore tagWordScore = new TagWordScore();
        tagWordScore.tagWordId = tagWordId;
        tagWordScore.socialObjectType = socialObjectType;
        tagWordScore.socialObjectId = socialObjectId;
        tagWordScore.score = score;
        tagWordScore.updatedDate = new Date();
        tagWordScore.save();
    }

    public static Long getSocialObjectCount(Long tagWordId,
                                           SocialObjectType socialObjectType) {
        Query q = JPA.em().createQuery("Select count(ts.id) from TagWordScore ts where ts.tagWordId=?1 and ts.socialObjectType = ?2");
        q.setParameter(1, tagWordId);
        q.setParameter(2, socialObjectType);
        return (Long) q.getSingleResult();
    }
}