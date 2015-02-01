package models;

import com.mnt.exception.SocialObjectNotCommentableException;
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

    public void onDeleteReview(User user)
            throws SocialObjectNotCommentableException {
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
