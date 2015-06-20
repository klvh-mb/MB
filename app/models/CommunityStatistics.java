package models;

import common.cache.CommunityMetaCache;
import common.utils.StringUtil;
import domain.PostType;
import org.elasticsearch.common.joda.time.LocalDate;
import play.data.validation.Constraints;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Table to capture activity statistics by community. (Updated nightly, or on-demand)
 *
 * Created by IntelliJ IDEA.
 * Date: 5/12/14
 * Time: 11:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class CommunityStatistics {
    private static play.api.Logger logger = play.api.Logger.apply(CommunityStatistics.class);

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

    @Constraints.Required
    public Long communityId;

    @Enumerated(EnumType.STRING)
    public TargetingSocialObject.TargetingType targetingType;

    public Date activityDate;

	public Long numPosts = 0L;

	public Long numComments = 0L;



    // Get, create if not found
	private static CommunityStatistics getCommunityStatistics(long commId) {
        return getCommunityStatistics(commId, new LocalDate());
    }

    private static CommunityStatistics getCommunityStatistics(long commId, LocalDate refLocalDate) {
		Date refDate = refLocalDate.toDate();

        Query q = JPA.em().createQuery("SELECT s FROM CommunityStatistics s where s.communityId = ?1 and s.activityDate = ?2");
        q.setParameter(1, commId);
        q.setParameter(2, refDate);

        synchronized (CommunityStatistics.class) {
            try {
                return (CommunityStatistics) q.getSingleResult();
            } catch (NoResultException e) {
                Community community = Community.findById(commId);

                CommunityStatistics statistics = new CommunityStatistics();
                statistics.communityId = commId;
                if (community != null) {
                    statistics.targetingType = community.getTargetingType();
                }
                statistics.activityDate = refDate;
                statistics.save();
                return statistics;
            }
        }
	}

    @Transactional
    public static void onNewPost(Long communityId, TargetingSocialObject.TargetingType targetingType) {
        final LocalDate today = new LocalDate();
        getCommunityStatistics(communityId, today);

        JPA.em().createQuery("UPDATE CommunityStatistics s SET s.numPosts = s.numPosts + 1 where s.communityId = ?1 and s.activityDate = ?2").
		setParameter(1, communityId).
		setParameter(2, today.toDate()).
        executeUpdate();

        // Handle no of posts in school entities.
        if (targetingType == TargetingSocialObject.TargetingType.PLAYGROUP ||
            targetingType == TargetingSocialObject.TargetingType.PRE_NURSERY ||
            targetingType == TargetingSocialObject.TargetingType.KINDY) {
            PlayGroup pg = PlayGroup.findById(CommunityMetaCache.getPGIdFromCommunity(communityId));
            if (pg != null) {
                pg.noOfPosts++;
                pg.merge();
            }
            PreNursery pn = PreNursery.findById(CommunityMetaCache.getPNIdFromCommunity(communityId));
            if (pn != null) {
                pn.noOfPosts++;
                pn.merge();
            }
            Kindergarten kg = Kindergarten.findById(CommunityMetaCache.getKGIdFromCommunity(communityId));
            if (kg != null) {
                kg.noOfPosts++;
                kg.merge();
            }
        }
    }

    @Transactional
    public static void onDeletePost(Long communityId, TargetingSocialObject.TargetingType targetingType) {
        LocalDate today = new LocalDate();
        getCommunityStatistics(communityId, today);

        JPA.em().createQuery("UPDATE CommunityStatistics s SET s.numPosts = s.numPosts - 1 where s.communityId = ?1 and s.activityDate = ?2").
		setParameter(1, communityId).
		setParameter(2, today.toDate()).
        executeUpdate();

        if (targetingType == TargetingSocialObject.TargetingType.PRE_NURSERY ||
            targetingType == TargetingSocialObject.TargetingType.KINDY) {
            PreNursery pn = PreNursery.findById(CommunityMetaCache.getPNIdFromCommunity(communityId));
            if (pn != null) {
                pn.noOfPosts--;
                pn.merge();
            }
            Kindergarten kg = Kindergarten.findById(CommunityMetaCache.getKGIdFromCommunity(communityId));
            if (kg != null) {
                kg.noOfPosts--;
                kg.merge();
            }
        }
    }

    @Transactional
    public static void onNewComment(Long communityId) {
        LocalDate today = new LocalDate();
        getCommunityStatistics(communityId, today);

        JPA.em().createQuery("UPDATE CommunityStatistics s SET s.numComments = s.numComments + 1 where s.communityId = ?1 and s.activityDate = ?2").
		setParameter(1, communityId).
		setParameter(2, today.toDate()).
        executeUpdate();
    }

    @Transactional
    public static void setNumPosts(Long communityId, LocalDate refDate, Long numPosts) {
        getCommunityStatistics(communityId, refDate);

        JPA.em().createQuery("UPDATE CommunityStatistics s SET s.numPosts = ?1 where s.communityId = ?2 and s.activityDate = ?3").
		setParameter(1, numPosts).
        setParameter(2, communityId).
		setParameter(3, refDate.toDate()).
        executeUpdate();
    }

    @Transactional
    public static void setNumComments(Long communityId, LocalDate refDate, Long numComments) {
        getCommunityStatistics(communityId, refDate);

        JPA.em().createQuery("UPDATE CommunityStatistics s SET s.numComments = ?1 where s.communityId = ?2 and s.activityDate = ?3").
        setParameter(1, numComments).
        setParameter(2, communityId).
		setParameter(3, refDate.toDate()).
        executeUpdate();
    }

    ///////////////////////////// Query API /////////////////////////////
    /**
     * Return list of communities, sorted by activity count.
     * @param numDaysBefore
     * @return
     */
    public static List<StatisticsSummary> getMostActiveCommunities(int numDaysBefore,
                                                                   Set<String> excludeTargetTypes) {
        LocalDate since = (new LocalDate()).minusDays(numDaysBefore);
        String idsForIn = StringUtil.collectionToString(excludeTargetTypes, ",");

        Query q = JPA.em().createNativeQuery("select s.communityId, SUM(s.numPosts), SUM(s.numComments) from CommunityStatistics s "+
               "where s.activityDate > ?1 and (s.targetingType is NULL or s.targetingType not in ("+idsForIn+")) "+
               "group by s.communityId "+
               "order by SUM(s.numPosts+s.numComments) desc");
        q.setParameter(1, since.toDate());
        List<Object[]> commRanks = q.getResultList();

        final List<StatisticsSummary> result = new ArrayList<>();
        for (Object[] commRank : commRanks) {
            BigInteger commId = (BigInteger) commRank[0];
            BigDecimal numPosts = (BigDecimal) commRank[1];
            BigDecimal numComments = (BigDecimal) commRank[2];

            StatisticsSummary entry = new StatisticsSummary();
            entry.commId = commId.longValue();
            entry.numPosts = numPosts.longValue();
            entry.numComments = numComments.longValue();
            result.add(entry);
        }
        return result;
    }

    ///////////////////////////// OnDemand API /////////////////////////////
    /**
     * @param numDaysBefore
     */
    public static void populatePastStats(int numDaysBefore) {
        logger.underlyingLogger().info("CommunityStatistics - Begin population (daysBefore="+numDaysBefore+")");

        for (int i = numDaysBefore; i >= 0; i--) {
            LocalDate refDate = (new LocalDate()).minusDays(i);

            Query q = JPA.em().createNativeQuery("select p.community_id, count(*) from Post p "+
                   "where p.deleted = 0 and p.CREATED_DATE > ?1 and p.CREATED_DATE < ?2 "+
                   "group by p.community_id");
            q.setParameter(1, refDate.toDate());
            q.setParameter(2, refDate.plusDays(1).toDate());
            List<Object[]> postStats = q.getResultList();

            for (Object[] postStat : postStats) {
                BigInteger commId = (BigInteger) postStat[0];
                BigInteger numPosts = (BigInteger) postStat[1];
                setNumPosts(commId.longValue(), refDate, numPosts.longValue());
            }

            q = JPA.em().createNativeQuery("select p.community_id, count(*) from Post p, Comment c "+
                   "where p.id = c.socialObject and p.postType = ?3 and p.deleted = 0 and c.deleted = 0 and c.CREATED_DATE > ?1 and c.CREATED_DATE < ?2 "+
                   "group by p.community_id");
            q.setParameter(1, refDate.toDate());
            q.setParameter(2, refDate.plusDays(1).toDate());
            q.setParameter(3, PostType.QUESTION.ordinal());
            List<Object[]> commentStats = q.getResultList();

            for (Object[] commentStat : commentStats) {
                BigInteger commId = (BigInteger) commentStat[0];
                BigInteger numComments = (BigInteger) commentStat[1];
                setNumComments(commId.longValue(), refDate, numComments.longValue());
            }
        }

        logger.underlyingLogger().info("CommunityStatistics - Done population (daysBefore=" + numDaysBefore + ")");
    }

    @Transactional
    public void save() {
		JPA.em().persist(this);
		JPA.em().flush();
	}

    public static class StatisticsSummary {
        public Long commId;
        public Long numPosts;
        public Long numComments;
    }
}
