package domain;

import models.SocialObject;
import models.User;

import com.mnt.exception.SocialObjectNotPostableException;

public interface Postable {
	public abstract SocialObject onPost(User user, String body,PostType type) throws SocialObjectNotPostableException;

}
