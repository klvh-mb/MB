package models;

import common.collection.Pair;
import play.db.jpa.JPA;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/1/15
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * insert into pkviewmeta (id,noCommentCount,noText,noVoteCount,postId,yesCommentCount,yesText,yesVoteCount) 
 * values (1, 0, 'No no no', 0, 351, 0, 'Yes yes yes', 0); 
 * 
 * @author keithlei
 *
 */
@Entity
public class PKViewMeta extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(Post.class);

    public static final String COMMENT_ATTR_YES = "YES";
    public static final String COMMENT_ATTR_NO = "NO";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    private Long postId;

    private String yesText;
    private String noText;

    private String yesImage;
    private String noImage;

    private int yesVoteCount = 0;
    private int noVoteCount = 0;

    // Ctor
    public PKViewMeta() {}

    public PKViewMeta(Long postId, String yesText, String noText, String yesImage, String noImage) {
        this.postId = postId;
        this.yesText = yesText;
        this.noText = noText;
        this.yesImage = yesImage;
        this.noImage = noImage;
    }

	public static List<Pair<PKViewMeta, Post>> getAllPKViewMeta() {
		Query q = JPA.em().createQuery(
                "select m, p from PKViewMeta m, Post p where m.postId = p.id and p.deleted=false order by p.id desc");
		List<Object[]> objList = (List<Object[]>) q.getResultList();

        List<Pair<PKViewMeta, Post>> result = new ArrayList<>();
        for (Object[] objects : objList) {
            Pair<PKViewMeta, Post> pair = new Pair<>();
            pair.first = (PKViewMeta) objects[0];
            pair.second = (Post) objects[1];
            result.add(pair);
        }
        return result;
	}

    public static List<Pair<PKViewMeta, Post>> getPKViewsByCommunity(Long communityId) {
        Query q = JPA.em().createQuery(
                "select m, p from PKViewMeta m, Post p where p.community.id = ?1 and m.postId = p.id and p.deleted=false order by p.id desc");
		q.setParameter(1, communityId);
        List<Object[]> objList = (List<Object[]>) q.getResultList();

        List<Pair<PKViewMeta, Post>> result = new ArrayList<>();
        for (Object[] objects : objList) {
            Pair<PKViewMeta, Post> pair = new Pair<>();
            pair.first = (PKViewMeta) objects[0];
            pair.second = (Post) objects[1];
            result.add(pair);
        }
        return result;
	}

    public static Pair<PKViewMeta, Post> getLatestPKView() {
        Query q = JPA.em().createQuery(
                "select m, p from PKViewMeta m, Post p where m.postId = p.id and p.deleted=false order by p.id desc");
        q.setMaxResults(1);
        List<Object[]> objList = (List<Object[]>) q.getResultList();

        Pair<PKViewMeta, Post> result = new Pair<>();
        if (objList.size() == 1) {
            result.first = (PKViewMeta) objList.get(0)[0];
            result.second = (Post) objList.get(0)[1];
            return result;
        } else {
            return null;
        }
    }
    
    public static Pair<PKViewMeta, Post> getPKViewById(Long pkViewMetaId) {
        Query q = JPA.em().createQuery(
                "select m, p from PKViewMeta m, Post p where m.id = ?1 and m.postId = p.id and p.deleted=false");
		q.setParameter(1, pkViewMetaId);
        List<Object[]> objList = (List<Object[]>) q.getResultList();

        Pair<PKViewMeta, Post> result = new Pair<>();
        if (objList.size() == 1) {
            result.first = (PKViewMeta) objList.get(0)[0];
            result.second = (Post) objList.get(0)[1];
            return result;
        } else {
            return null;
        }
	}

    public void onYesVote(User user, Post post) {
        this.yesVoteCount++;
        merge();

        post.recordYesVote(user);
        // update affinity
        UserCommunityAffinity.onCommunityActivity(user.id, post.getCommunity().getId());
    }

    public void onNoVote(User user, Post post) {
        this.noVoteCount++;
        merge();

        post.recordNoVote(user);
        // update affinity
        UserCommunityAffinity.onCommunityActivity(user.id, post.getCommunity().getId());
    }

    ///////////////////// Utility /////////////////////
    public static boolean isValidCommentAttribute(String attribute) {
        return COMMENT_ATTR_YES.equals(attribute) || COMMENT_ATTR_NO.equals(attribute);
    }

    ///////////////////// Getters /////////////////////
    public Long getPostId() {
        return postId;
    }

    public String getYesText() {
        return yesText;
    }

    public String getNoText() {
        return noText;
    }

    public String getYesImage() {
        return yesImage;
    }

    public String getNoImage() {
        return noImage;
    }

    public int getYesVoteCount() {
        return yesVoteCount;
    }

    public int getNoVoteCount() {
        return noVoteCount;
    }
}
