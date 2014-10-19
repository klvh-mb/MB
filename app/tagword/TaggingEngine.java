package tagword;

import play.db.jpa.Transactional;

import javax.persistence.Query;

/**
 * Created by IntelliJ IDEA.
 * Date: 18/10/14
 * Time: 7:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaggingEngine {
    private static play.api.Logger logger = play.api.Logger.apply(TaggingEngine.class);

    @Transactional
	public static void indexTagWords() {
        // TODO
	}
}
