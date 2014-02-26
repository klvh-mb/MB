package domain;

import com.mnt.exception.SocialObjectNotLikableException;

import models.User;

public interface Likeable {
	public abstract void onLike(User user) throws SocialObjectNotLikableException;
}
