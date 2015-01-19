package common.system.upgrade;

import java.util.List;

import javax.persistence.Query;

import play.db.jpa.JPA;
import models.Comment;
import models.Post;
import models.SystemVersion;

/**
 * 1) Community icons.
 * 2) Targeting communities.
 * 
 * @author keithlei
 *
 */
public class UpgradeScript_1_0 extends UpgradeScript {
    private static final play.api.Logger logger = play.api.Logger.apply(UpgradeScript_1_0.class);
    
    public UpgradeScript_1_0() {
    }
    
    @Override
    public String getVersion() {
        return "1.0";
    }
    
    @Override
    public void insertToSystemVersion() {
        SystemVersion version = new SystemVersion(
                getVersion(), 
                this.getClass().getName(), 
                "Fill in socialUpdateBy of all posts");
        version.save();
    }
    
    @Override
    public boolean upgrade() throws Exception {
        logger.underlyingLogger().info("Fill in socialUpdateBy...");
        
        List<Post> allPosts = getAllPosts();
        for (Post post : allPosts) {
        	if (post.socialUpdatedBy == null) {
	        	List<Comment> latestComment = post.getCommentsOfPost(1);
	        	if (latestComment == null || latestComment.size() == 0) {
	        		post.socialUpdatedBy = post.owner;
	        	} else {
	        		post.socialUpdatedBy = latestComment.get(0).owner;
	        	}
        	}
        }
        
        return true;
    }
    
    private static List<Post> getAllPosts() {
        Query q = JPA.em().createQuery("Select p from Post p where deleted = false order by socialUpdatedDate desc");
        return (List<Post>)q.getResultList();
    }
}