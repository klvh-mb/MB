package models;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 21/6/14
 * Time: 12:45 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class UserCommunityAffinity extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(UserCommunityAffinity.class);

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private Long userId;

    private Long communityId;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    private Date lastJoined;

    private int viewCount;

    private int activityCount;

    private boolean newsfeedEnabled = true;      // default to true

    public UserCommunityAffinity() { }

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

    public static List<UserCommunityAffinity> findByUser(Long userId) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<UserCommunityAffinity> q = cb.createQuery(UserCommunityAffinity.class);
		Root<UserCommunityAffinity> c = q.from(UserCommunityAffinity.class);
		q.select(c);
		q.where(cb.equal(c.get("userId"), userId));
		return JPA.em().createQuery(q).getResultList();
	}

    public static UserCommunityAffinity findByUserCommunity(Long userId, Long communityId) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<UserCommunityAffinity> q = cb.createQuery(UserCommunityAffinity.class);
        Root<UserCommunityAffinity> c = q.from(UserCommunityAffinity.class);
        q.select(c);
        q.where(cb.and(cb.equal(c.get("userId"), userId), cb.equal(c.get("communityId"), communityId)));
        try {
            return JPA.em().createQuery(q).getSingleResult();
        } catch (NoResultException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
            return null;
        } catch (NonUniqueResultException ne) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(ne));
            return null;
        }
    }
    
    public Long getUserId() {
        return userId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public Date getLastJoined() {
        return lastJoined;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getActivityCount() {
        return activityCount;
    }

    public boolean isNewsfeedEnabled() {
        return newsfeedEnabled;
    }
    
    public void setNewsfeedEnabled(boolean newsfeedEnabled) {
        this.newsfeedEnabled = newsfeedEnabled;
    }
}
