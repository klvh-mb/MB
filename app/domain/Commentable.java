package domain;

import models.Comment;
import models.User;

import com.mnt.exception.SocialObjectNotCommentableException;

public interface Commentable {
	public abstract Comment onComment(User user, String body, CommentType type) throws SocialObjectNotCommentableException;

}
