package models;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import common.utils.StringUtil;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import domain.CommentType;
import domain.Creatable;
import domain.Likeable;
import domain.SocialObjectType;

/**
 * A Comment by an User on a SocialObject (Post only)
 *
 */
@Entity
public class Comment extends SocialObject implements Comparable<Comment>, Likeable, Serializable, Creatable {
    private static final play.api.Logger logger = play.api.Logger.apply(Comment.class);

    @Required
    public Long socialObject;       // e.g. Post Id

    @Required
    public Date date = new Date();

    @Required
    @Column(length=2000)
    public String body;

    @Required
    public CommentType commentType;

    public int noOfLikes=0;

    private String attribute;

    @ManyToOne(cascade = CascadeType.REMOVE)
  	public Folder folder;

    /**
     * Ctor
     */
    public Comment() {}
    
    public Comment(SocialObject socialObject, User user, String body) {
        this.owner = user;
        this.socialObject = socialObject.id;
        this.body = body;
    }

    public static Comment findById(Long id) {
        Query q = JPA.em().createQuery("SELECT c FROM Comment c where id = ?1 and deleted = false");
        q.setParameter(1, id);
        return (Comment) q.getSingleResult();
    }

    
    @Override
    public void onLikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][cmt="+id+"] Comment onLikedBy");
        }

        recordLike(user);
        this.noOfLikes++;
        user.likesCount++;
    }
    
    @Override
    public void onUnlikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][cmt="+id+"] Comment onUnlikedBy");
        }

        this.noOfLikes--;
        user.likesCount--;
    }

    @Override
    public int compareTo(Comment o) {
        return date.compareTo(o.date);
    }
    
    @Override
    public void save() {
        super.save();
    }
    
    public void delete(User deletedBy) {
        Post post = Post.findById(this.socialObject);
        post.noOfComments--;
        this.deleted = true;
        this.deletedBy = deletedBy;
        GameAccountStatistics.recordDeleteComment(deletedBy.id);
        save();
    }

    public Resource addCommentPhoto(File source) throws IOException {
		ensureAlbumExist();
		Resource cover_photo = this.folder.addFile(source,
				SocialObjectType.COMMENT_PHOTO);
		
		return cover_photo;
    }
  
    public void ensureAlbumExist() {
		if (this.folder == null) {
			this.folder = Folder.createAlbum(this.owner, "comment-ps", "", true);
			this.merge();
		}
	}

    public Post getPost() {
      	Query q = JPA.em().createNativeQuery("SELECT post_id FROM post_comment where comments_id = "+this.id);
      	BigInteger integer = (BigInteger) q.getSingleResult();
        Long id = integer.longValue();
        Post post = Post.findById(id);
        return post;
    }

    public String getShortenedBody() {
        if (body == null || body.startsWith("http")) {
            return "";
        } else {
            return StringUtil.truncateWithDots(body, 12);
        }
    }

    /////////////////// Getters, Setters ///////////////////
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}