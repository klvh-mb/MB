package processor;

import common.collection.Pair;
import common.utils.StringUtil;
import domain.SocialObjectType;
import models.PrimarySocialRelation;
import models.SocialObject;
import models.User;
import play.db.jpa.JPA;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 22/7/14
 * Time: 11:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class LikeManager {

    /**
     * @param user
     * @param objIds
     */
    public static Set<Pair<Long, SocialObjectType>> getLikedBy(User user, List<Long> objIds) {
        Set<Pair<Long, SocialObjectType>> results = new HashSet<>();

        String idsForIn = StringUtil.collectionToString(objIds, ",");

        Query q = JPA.em().createQuery("Select sr.target, sr.targetType from PrimarySocialRelation sr where sr.action=?1 and sr.actor=?2 " +
                "and sr.target in ("+idsForIn+")");
        q.setParameter(1, PrimarySocialRelation.Action.LIKED);
        q.setParameter(2, user.id);

        List<Object[]> qRes = q.getResultList();
        for (Object[] entry : qRes) {
            results.add(new Pair<>((Long) entry[0], (SocialObjectType) entry[1]));
        }
        return results;
    }
}
