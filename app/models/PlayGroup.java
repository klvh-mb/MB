package models;

import common.model.SchoolType;
import domain.Commentable;
import domain.Likeable;
import domain.SocialObjectType;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.Entity;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 19/6/15
 * Time: 11:20 PM
 */
@Entity
public class PlayGroup extends SocialObject implements Likeable, Commentable {
    private static final play.api.Logger logger = play.api.Logger.apply(PlayGroup.class);

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

    // business attributes
    public String description;
    public String target;
    public String trailClass;
    public boolean inEnglish;
    public boolean inCantonese;
    public boolean inMandarin;

    // stats
    public int noOfPosts = 0;
    public int noOfLikes = 0;
    public int noOfViews = 0;
    public int noOfBookmarks = 0;

    // Ctor
    public PlayGroup() {
        this.objectType = SocialObjectType.PLAYGROUP;
    }

   ///////////////////// onSocialActions /////////////////////
    @Override
    public void onLikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][pn="+this.id+"] PlayGroup onLikedBy");
        }
        recordLike(user);
        this.noOfLikes++;
        user.likesCount++;
    }

    @Override
    public void onUnlikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][pn="+this.id+"] PlayGroup onUnlikedBy");
        }
        this.noOfLikes--;
        user.likesCount--;
    }

    @Transactional
    public void onBookmarkedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][pn="+this.id+"] PlayGroup onBookmarkedBy");
        }
        // 1) school saved list
        SchoolSaved saved = new SchoolSaved(user.getId(), this.id, SchoolType.PG);
        saved.save();
        // 2) join PN community
        if (communityId != null) {
            try {
                Community pnComm = Community.findById(communityId);
                pnComm.onJoinRequest(user);
            } catch (Exception e) {
                logger.underlyingLogger().error("Error joining PG community: "+communityId, e);
            }
        }
        // 3) record bookmark
        recordBookmark(user);
        this.noOfBookmarks++;
    }

    @Transactional
    public void onUnBookmarkedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][pn="+this.id+"] PlayGroup onBookmarkedBy");
        }
        // 1) remove from school saved list
        List<SchoolSaved> savedList = SchoolSaved.findByUserSchoolId(user.getId(), this.id, SchoolType.PG);
        if (savedList != null) {
            for (SchoolSaved saved : savedList) {
                saved.delete();
            }
        }
        // 2) leave PN community
        if (communityId != null) {
            try {
                Community pnComm = Community.findById(communityId);
                user.leaveCommunity(pnComm);
            } catch (Exception e) {
                logger.underlyingLogger().error("Error leaving PG community: "+communityId, e);
            }
        }
        // 3) remove bookmark record
        user.unBookmarkOn(this.id, this.objectType);
        this.noOfBookmarks--;
    }


    ///////////////////// GET SQLs /////////////////////
    public static PlayGroup findById(Long id) {
        Query q = JPA.em().createQuery("SELECT p FROM PlayGroup p where p.id=?1");
        q.setParameter(1, id);
        try {
            return (PlayGroup) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static PlayGroup findByNameDistrictId(String pnName, Long districtId) {
        Query q = JPA.em().createQuery("SELECT p FROM PlayGroup p where p.name=?1 and p.districtId=?2");
        q.setParameter(1, pnName);
        q.setParameter(2, districtId);
        try {
            return (PlayGroup) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<PlayGroup> findAll() {
        Query q = JPA.em().createQuery("SELECT p FROM PlayGroup p");
        return (List<PlayGroup>)q.getResultList();
    }

    public static List<PlayGroup> searchByName(String nameSubStr) {
        Query q = JPA.em().createQuery("SELECT p FROM PlayGroup p where p.name like ?1 or UPPER(p.nameEn) like ?2");
        q.setParameter(1, "%"+nameSubStr+"%");
        q.setParameter(2, "%"+nameSubStr.toUpperCase()+"%");
        return (List<PlayGroup>)q.getResultList();
    }

    public static List<PlayGroup> getPGsByDistrict(Long districtId) {
        Query q = JPA.em().createQuery("SELECT p FROM PlayGroup p where p.districtId = ?1 order by p.name");
        q.setParameter(1, districtId);
        return (List<PlayGroup>)q.getResultList();
    }

    public static List<PlayGroup> getTopViews(int num) {
        Query q = JPA.em().createQuery("SELECT p FROM PlayGroup p order by p.noOfViews desc");
        q.setMaxResults(num);
        return (List<PlayGroup>)q.getResultList();
    }

    public static List<PlayGroup> getTopDiscussed(int num) {
        Query q = JPA.em().createQuery("SELECT p FROM PlayGroup p order by p.noOfPosts desc");
        q.setMaxResults(num);
        return (List<PlayGroup>)q.getResultList();
    }

    public static List<PlayGroup> getTopBookmarked(int num) {
        Query q = JPA.em().createQuery("SELECT p FROM PlayGroup p order by p.noOfBookmarks desc");
        q.setMaxResults(num);
        return (List<PlayGroup>)q.getResultList();
    }

    public static List<PlayGroup> getBookmarked(Long userId) {
        Query q = JPA.em().createQuery("SELECT p FROM PlayGroup p, SchoolSaved ss where ss.schoolType=?1 and ss.userId=?2 " +
                "and ss.schoolId = p.id");
        q.setParameter(1, SchoolType.PG);
        q.setParameter(2, userId);
        return (List<PlayGroup>)q.getResultList();
    }
}
