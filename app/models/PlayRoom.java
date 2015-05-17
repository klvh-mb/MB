package models;

import domain.Commentable;
import domain.Likeable;
import domain.SocialObjectType;

import javax.persistence.Entity;

/**
 * Created by IntelliJ IDEA.
 * Date: 10/5/15
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class PlayRoom extends SocialObject implements Likeable, Commentable {
    private static final play.api.Logger logger = play.api.Logger.apply(PlayRoom.class);

    public Long communityId;
    public Long regionId;
    public Long districtId;

    // name is inherited
    public String nameEn;
    public String icon;

    public String phoneText;
    public String url;
    public String email;
    public String address;
    public String mapUrlSuffix;

    // stats
    public int noOfPosts = 0;
    public int noOfLikes = 0;
    public int noOfViews = 0;
    public int noOfBookmarks = 0;

    // Ctor
    public PlayRoom() {
        this.objectType = SocialObjectType.PLAYROOM;
    }

}
