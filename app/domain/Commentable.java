package domain;

import models.SocialObject;
import models.User;

import com.mnt.exception.SocialObjectNotCommentableException;

public interface Commentable {
	public abstract SocialObject onComment(User user, String body, CommentType type) throws SocialObjectNotCommentableException;
	public abstract void onDeleteComment(User user, String body, CommentType type) throws SocialObjectNotCommentableException;
}
