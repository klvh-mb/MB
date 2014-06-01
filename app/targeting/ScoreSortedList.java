package targeting;

import com.google.common.collect.Ordering;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/6/14
 * Time: 10:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScoreSortedList<T> {

    private static final Ordering<Scorable> ordering = new Ordering<Scorable>() {
        @Override
        public int compare(@Nullable Scorable scorable1, @Nullable Scorable scorable2) {
            return scorable1.getScore() - scorable2.getScore();
        }
    };

    private Collection<Scorable<T>> collection;

    public ScoreSortedList(Collection<Scorable<T>> collection) {
        this.collection = collection;
    }

    public List<Scorable<T>> greatestOf(int k) {
        return ordering.greatestOf(collection, k);
    }

}
