package models;

import domain.ReviewType;
import domain.SocialObjectType;
import play.data.validation.Constraints.Required;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/2/15
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ReviewComment extends SocialObject {
    private static final play.api.Logger logger = play.api.Logger.apply(ReviewComment.class);

    // name is not used

    @Required
    public Long socialObject;     // e.g. PN Id

    @Required
    public ReviewType reviewType;

    @Required
    public Date date = new Date();

    @Required
    @Column(length=2000)
    public String body;

    public int noOfLikes=0;

    // Ctor
    public ReviewComment() { }

    /**
     * @param socialObjectId
     * @param user
     * @param body
     * @param type
     */
    public static ReviewComment createReview(Long socialObjectId, User user,String body, ReviewType type) {
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.objectType = SocialObjectType.REVIEW_COMMENT;
        reviewComment.socialObject = socialObjectId;
        reviewComment.reviewType = type;
        reviewComment.body = body;
        reviewComment.owner = user;
        reviewComment.save();

        return reviewComment;
    }


    @Override
    public void onLikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][rev="+id+"] Review onLikedBy");
        }

        recordLike(user);
        this.noOfLikes++;
        user.likesCount++;
    }

    @Override
    public void onUnlikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][rev="+id+"] Review onUnlikedBy");
        }

        this.noOfLikes--;
        user.likesCount--;
    }

    @Override
    public void save() {
        super.save();
    }
}
