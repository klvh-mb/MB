package models;

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

/**
 * Table to capture activity statistics by community.
 *
 * Created by IntelliJ IDEA.
 * Date: 5/12/14
 * Time: 11:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class CommunityStatistics {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

    @Constraints.Required
    public Long communityId;

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
                CommunityStatistics statistics = new CommunityStatistics();
                statistics.communityId = commId;
                statistics.activityDate = refDate;
                statistics.save();
                return statistics;
            }
        }
	}

    @Transactional
    public static void onNewPost(Long communityId) {
        LocalDate today = new LocalDate();
        getCommunityStatistics(communityId, today);

        JPA.em().createQuery("UPDATE CommunityStatistics s SET s.numPosts = s.numPosts + 1 where s.communityId = ?1 and s.activityDate = ?2").
		setParameter(1, communityId).
		setParameter(2, today.toDate()).
        executeUpdate();
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

    /**
     * Return list of communities, sorted by activity count.
     * @param numDaysBefore
     * @return
     */
    public static List<StatisticsSummary> getMostActiveCommunities(int numDaysBefore) {
        LocalDate since = (new LocalDate()).minusDays(numDaysBefore);

        Query q = JPA.em().createNativeQuery("select s.communityId, SUM(s.numPosts), SUM(s.numComments) from CommunityStatistics s "+
               "where s.activityDate > ?1 "+
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
