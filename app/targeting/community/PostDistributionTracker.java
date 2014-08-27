package targeting.community;

import common.collection.Pair;
import redis.clients.jedis.Tuple;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 27/8/14
 * Time: 10:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class PostDistributionTracker {
    private Map<Long, LinkedList<Tuple>> postDistMap = new HashMap<>();

    public void addCommunity(Long commId, LinkedList<Tuple> posts) {
        postDistMap.put(commId, posts);
    }

    public Pair<Long, Tuple> peekLatest(Set<Long> commIds) {
        Long retCommId = null;
        Tuple retTuple = null;

        for (Long commId : postDistMap.keySet()) {
            if (commIds == null || commIds.contains(commId)) {
                Tuple tuple = postDistMap.get(commId).peekFirst();
                if (tuple != null) {
                    if (retCommId == null || retTuple == null) {
                        retCommId = commId;
                        retTuple = tuple;
                    } else {
                        if (tuple.getScore() > retTuple.getScore()) {
                            retCommId = commId;
                            retTuple = tuple;
                        }
                    }
                }
            }
        }

        if (retCommId != null) {
            return new Pair<>(retCommId, retTuple);
        } else {
            return null;
        }
    }

    public void removeLatest(Long commId) {
        postDistMap.get(commId).pollFirst();
    }
}
