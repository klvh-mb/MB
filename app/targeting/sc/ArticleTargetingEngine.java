package targeting.sc;

import models.Article;
import models.User;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/5/14
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleTargetingEngine {

    List<Article> getTargetedArticles(User user, int maxCount) {
        if (user == null) {
            throw new IllegalArgumentException("user is null");
        }

        String parentGender = user.getGender();
        String district = user.getLocation();

        return Collections.EMPTY_LIST;       // TODO
    }

}
