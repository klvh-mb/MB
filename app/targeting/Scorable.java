package targeting;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/6/14
 * Time: 10:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class Scorable<T> {
    private int score;
    private T object;

    public Scorable(int score, T object) {
        this.score = score;
        this.object = object;
    }

    public int getScore() {
        return score;
    }

    public T getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "Scorable{" +
                "score=" + score +
                ", object=" + object +
                '}';
    }
}