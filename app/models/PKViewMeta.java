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
@Entity
public class PKViewMeta extends domain.Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    private Long postId;

    private String yesText;
    private String noText;
    private String image;

    private int yesVoteCount = 0;
    private int noVoteCount = 0;
    private int yesCommentCount = 0;
    private int noCommentCount = 0;

    // Ctor
    public PKViewMeta() {}

    public PKViewMeta(Long postId, String yesText, String noText) {
        this.postId = postId;
        this.yesText = yesText;
        this.noText = noText;
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

    public String getImage() {
        return image;
    }

    public int getYesVoteCount() {
        return yesVoteCount;
    }

    public int getNoVoteCount() {
        return noVoteCount;
    }

    public int getYesCommentCount() {
        return yesCommentCount;
    }

    public int getNoCommentCount() {
        return noCommentCount;
    }
}