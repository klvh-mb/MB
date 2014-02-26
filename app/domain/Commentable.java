package domain;

import models.User;

import com.mnt.exception.SocialObjectNotCommentableException;

public interface Commentable {
	public abstract void onComment(User user, String body) throws SocialObjectNotCommentableException;

}
