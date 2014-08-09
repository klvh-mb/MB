package models;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.i18n.Messages;
import domain.CommentType;
import domain.Creatable;
import domain.Likeable;
import domain.SocialObjectType;

/**
 * A Comment by an User on a SocialObject 
 *
 */

@Entity
public class Comment extends SocialObject implements Comparable<Comment>, Likeable, Serializable, Creatable {
    private static final play.api.Logger logger = play.api.Logger.apply(Comment.class);

    public Comment() {}
    
    public Comment(SocialObject socialObject, User user, String body) {
        this.owner = user;
        this.socialObject = socialObject.id;
        this.body = body;
    }

    @Required
    public Long socialObject;
    
    @Required
    public Date date = new Date();
    
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
    
    @Required
    @Column(length=2000)
    public String body;
    
    @Required
    public CommentType commentType;
    
    public int noOfLikes=0;
      
    @ManyToOne(cascade = CascadeType.REMOVE)
  	public Folder folder;
  
    @Override
    public int compareTo(Comment o) {
        return date.compareTo(o.date);
    }
    
    @Override
    public void save() {
        super.save();
        recordCommentOnCommunityPost(owner);
    }
    
    public static Comment findById(Long id) {
        Query q = JPA.em().createQuery("SELECT c FROM Comment c where id = ?1 and deleted = false");
        q.setParameter(1, id);
        return (Comment) q.getSingleResult();
    }
    
    public static void deleteById(Long id) {
        Comment comment = findById(id);
        comment.deleted = true;
        comment.save();
    }
    
    public boolean isLikedBy(User user) {
        Query q = JPA.em().createQuery("Select sr from PrimarySocialRelation sr where sr.action=?1 and sr.actor=?2 " + 
                "and sr.target=?3 and sr.targetType=?4");
        q.setParameter(1, PrimarySocialRelation.Action.LIKED);
        q.setParameter(2, user.id);
        q.setParameter(3, this.id);
        q.setParameter(4, this.objectType);
        PrimarySocialRelation sr = null;
        try {
            sr = (PrimarySocialRelation)q.getSingleResult();
        } catch (NoResultException nre) {
            return false;
        }
        return true;
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
}