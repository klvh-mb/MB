package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.mnt.exception.SocialObjectNotCommentableException;

import play.Play;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import domain.CommentType;
import domain.SocialObjectType;

/**
 * A resource can be a file or an external url, is contained always in a Folder
 * 
 */

@Entity
public class Resource extends SocialObject {

	public Resource() {
	}

	@JsonIgnore
	@Required
	@ManyToOne
	public Folder folder;

	@Required
	public String resourceName;

	@Lob
	public String description;

	@Required
	public Integer priority = 0;

	@OneToMany(cascade = CascadeType.REMOVE)
	public Set<Comment> comments;

	public Resource(SocialObjectType objectType) {
		this.objectType = objectType;
	}

	public Boolean isImage() {
		return com.mnt.utils.FileUtils.isImage(resourceName);
	}

	public Boolean isExtrenal() {
		return com.mnt.utils.FileUtils.isExternal(resourceName);
	}

	@Override
	public String toString() {
		return super.toString() + " " + resourceName;
	}

	@Override
	public void onLike(User user) {
		recordLike(user);
	}

	public String getPath() {
		if (isExtrenal()) {
			return resourceName;
		} else {
			return Play.application().configuration().getString("storage.path")
					+ owner.id + "/" + folder.id + "/" + id + "/"
					+ resourceName;
		}
	}
	
	@Transactional
	public String getThumbnail() {
		if (isExtrenal()) {
			return resourceName;
		} else {
			return Play.application().configuration().getString("storage.path")
					+ owner.id + "/" + folder.id + "/" + id + "/thumbnail."+resourceName;
		}
	}

	public java.io.File getRealFile() {
		java.io.File f = new java.io.File(getPath());
		if (f.exists()) {
			return f;
		}
		return null;
	}

	public Long getSize() {
		if (isExtrenal()) {
			return null;
		} else {
			return FileUtils.sizeOf(getRealFile());
		}
	}

	@Override
	public SocialObject onComment(User user, String body, CommentType type)
			throws SocialObjectNotCommentableException {
		Comment comment = new Comment(this, user, body);

		if (type == CommentType.ANSWER) {
			comment.commentType = type;
		}

		if (type == CommentType.SIMPLE) {
			comment.commentType = type;
		}

		if (comments == null) {
			comments = new HashSet<Comment>();
		}
		comment.save();
		this.comments.add(comment);
		JPA.em().merge(this);
		recordCommentOnCommunityPost(user);
		return comment;
	}

}
