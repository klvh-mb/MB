package domain;

import models.User;

import com.mnt.exception.SocialObjectNotJoinableException;

public interface Joinable {
	void onJoinRequest(User user) throws SocialObjectNotJoinableException;
}
