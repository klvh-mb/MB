package models;

import com.mnt.exception.SocialObjectNotCommentableException;
import common.model.SchoolType;
import domain.*;
import org.codehaus.jackson.annotate.JsonIgnore;
import play.db.jpa.JPA;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 25/7/14
 * Time: 9:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class PreNursery extends SocialObject implements Likeable, Commentable {
    private static final play.api.Logger logger = play.api.Logger.apply(PreNursery.class);

    public Long regionId;
    public Long districtId;

    // name is inherited
    public String nameEn;

    public String phoneText;
    public String url;
    public String email;
    public String address;
    public String mapUrlSuffix;

    public boolean couponSupport = false;
    public String classTimes;       // comma separated (AM,PM,WD)

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public Set<ReviewComment> reviews;

    // stats
    public int noOfComments = 0;
    public int noOfLikes = 0;
    public int noOfViews = 0;
    public int noOfBookmarks = 0;


    // Ctor
    public PreNursery() {}


    public SocialObject onReview(User user, String body)
            throws SocialObjectNotCommentableException {
        // create Review object
        ReviewComment review = ReviewComment.createReview(id, user, body, ReviewType.PN);

        if (reviews == null) {
            reviews = new HashSet<>();
        }
        this.reviews.add(review);
        this.noOfComments++;
        JPA.em().merge(this);

        return review;
    }

    public void onDeleteReview(User user) throws SocialObjectNotCommentableException {
        this.noOfComments--;
    }

    @JsonIgnore
    public long getNumReviewComments() {
        Query q = JPA.em().createQuery("Select count(c.id) from ReviewComment c where socialObject=?1 and reviewType=?2 and deleted=false");
        q.setParameter(1, this.id);
        q.setParameter(2, ReviewType.PN);
        return (Long) q.getSingleResult();
    }

    @JsonIgnore
    public List<ReviewComment> getReviewComments() {
        Query q = JPA.em().createQuery("Select c from ReviewComment c where socialObject=?1 and reviewType=?2 and deleted=false order by date");
        q.setParameter(1, this.id);
        q.setParameter(2, ReviewType.PN);
        return (List<ReviewComment>)q.getResultList();
    }

    @JsonIgnore
    public List<ReviewComment> getReviewComments(int limit) {
        Query q = JPA.em().createQuery("Select c from ReviewComment c where socialObject=?1 and reviewType=?2 and deleted=false order by date desc" );
        q.setParameter(1, this.id);
        q.setParameter(2, ReviewType.PN);
        return (List<ReviewComment>)q.setMaxResults(limit).getResultList();
    }

    @Override
    public void onLikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][pn="+this.id+"] PreNursery onLikedBy");
        }
        recordLike(user);
        this.noOfLikes++;
        user.likesCount++;
    }

    @Override
    public void onUnlikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][pn="+this.id+"] PreNursery onUnlikedBy");
        }
        this.noOfLikes--;
        user.likesCount--;
    }

    public void onBookmarkedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][pn="+this.id+"] PreNursery onBookmarkedBy");
        }
        recordBookmark(user);
        this.noOfBookmarks++;
    }

    ///////////////////// GET SQLs /////////////////////
    public static List<PreNursery> searchByName(String nameSubStr) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.name like ?1 or UPPER(pn.namEn) like ?2");
        q.setParameter(1, "%"+nameSubStr+"%");
        q.setParameter(2, "%"+nameSubStr.toUpperCase()+"%");
        return (List<PreNursery>)q.getResultList();

    }

    public static List<PreNursery> getPNsByRegion(Long regionId) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.regionId = ?1 order by pn.districtId, pn.name");
        q.setParameter(1, regionId);
        return (List<PreNursery>)q.getResultList();
    }

    public static List<PreNursery> getPNsByDistrict(Long districtId) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.districtId = ?1 order by pn.name");
        q.setParameter(1, districtId);
        return (List<PreNursery>)q.getResultList();
    }

    public static List<PreNursery> getTopViewsPNs(Long num) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn order by pn.noOfViews desc");
        q.setMaxResults(num.intValue());
        return (List<PreNursery>)q.getResultList();
    }

    public static List<PreNursery> getTopBookmarkedPNs(Long num) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn order by pn.noOfBookmarks desc");
        q.setMaxResults(num.intValue());
        return (List<PreNursery>)q.getResultList();
    }

    public static List<PreNursery> getBookmarkedPNs(Long userId) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn, SecondarySocialRelation sr where sr.action=?1 and sr.actor=?2 " +
                "and sr.targetType=?3 and sr.target = pn.id");
        q.setParameter(1, SecondarySocialRelation.Action.BOOKMARKED);
        q.setParameter(2, userId);
        q.setParameter(3, SocialObjectType.PRE_NURSERY);
        return (List<PreNursery>)q.getResultList();
    }

    public static List<PreNursery> getFormReceivedPNs(Long userId) {
        return getPNsBySavedStatus(userId, SchoolSaved.Status.GotForm);
    }

    public static List<PreNursery> getAppliedPNs(Long userId) {
        return getPNsBySavedStatus(userId, SchoolSaved.Status.Applied);
    }

    public static List<PreNursery> getInterviewedPNs(Long userId) {
        return getPNsBySavedStatus(userId, SchoolSaved.Status.Interviewed);
    }

    public static List<PreNursery> getOfferedPNs(Long userId) {
        return getPNsBySavedStatus(userId, SchoolSaved.Status.Offered);
    }


    private static List<PreNursery> getPNsBySavedStatus(Long userId, SchoolSaved.Status status) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn, SchoolSaved sr where sr.userId=?1 and sr.schoolType=?2 " +
                "and sr.status=?3 and sr.schoolId = pn.id");
        q.setParameter(1, userId);
        q.setParameter(2, SchoolType.PN);
        q.setParameter(3, status);
        return (List<PreNursery>)q.getResultList();
    }

    ///////////////////// SQL /////////////////////
    /**
     * @return
     */
    public static String getDeleteAllSql() {
        return "delete from PreNursery;";
    }

    /**
     * @return
     */
    public String getInsertSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into PreNursery (");
        sb.append("regionId, districtId, name, url, phoneText, email, address, couponSupport, ");
        sb.append("formUrl, mapUrlSuffix, classTimes");
        sb.append(") values (");

        sb.append(regionId).append(", ");
        sb.append(districtId).append(", ");
        sb.append("'").append(name).append("', ");
        if (url != null) sb.append("'").append(url).append("', "); else sb.append("NULL, ");
        if (phoneText != null) sb.append("'").append(phoneText).append("', "); else sb.append("NULL, ");
        if (email != null) sb.append("'").append(email).append("', "); else sb.append("NULL, ");
        if (address != null) sb.append("'").append(address.replace("'","")).append("', "); else sb.append("NULL, ");
        sb.append(couponSupport ? 1 : 0).append(", ");
        if (mapUrlSuffix != null) sb.append("'").append(mapUrlSuffix.replace("'","")).append("', "); else sb.append("NULL, ");
        if (classTimes != null) sb.append("'").append(classTimes.replace("'","")).append("', "); else sb.append("NULL, ");

        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append(");");
        return sb.toString();
    }
}
