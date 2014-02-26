package domain;

import models.User;

import com.mnt.exception.SocialObjectNotPostableException;

public interface Postable {
	public abstract void onPost(User user, String body) throws SocialObjectNotPostableException;

}
