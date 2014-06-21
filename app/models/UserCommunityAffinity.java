package models;

import org.codehaus.jackson.annotate.JsonIgnore;
import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * Date: 21/6/14
 * Time: 12:45 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class UserCommunityAffinity extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply("application");

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
    public Long id;

    public Long userId;

    public Long communityId;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    public Date lastJoined;

    public int viewCount;

    public int activityCount;

    public boolean newsfeedEnabled = true;      // default to true

    /**
     * Ctor
     * @param userId
     * @param communityId
     */
    private UserCommunityAffinity(Long userId, Long communityId) {
        this.userId = userId;
        this.communityId = communityId;
        this.lastJoined = new Date();
    }

    @Transactional
    public static void onJoinedCommunity(Long userId, Long communityId) {
        try {
            UserCommunityAffinity affinity = new UserCommunityAffinity(userId, communityId);
            affinity.save();
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in onJoinedCommunity(u="+userId+",c="+communityId+")", e);
        }
    }

    @Transactional
    public static void onLeftCommunity(Long userId, Long communityId) {
        JPA.em().createQuery("DELETE from UserCommunityAffinity where userId = ?1 and communityId = ?2").
		setParameter(1, userId).
		setParameter(2, communityId).
        executeUpdate();
    }

    @Transactional
    public static void onCommunityView(Long userId, Long communityId) {
        JPA.em().createQuery("UPDATE UserCommunityAffinity SET viewCount = viewCount + 1 where userId = ?1 and communityId = ?2").
		setParameter(1, userId).
		setParameter(2, communityId).
        executeUpdate();
    }

    @Transactional
    public static void onCommunityActivity(Long userId, Long communityId) {
        JPA.em().createQuery("UPDATE UserCommunityAffinity SET activityCount = activityCount + 1 where userId = ?1 and communityId = ?2").
		setParameter(1, userId).
		setParameter(2, communityId).
        executeUpdate();
    }
}
