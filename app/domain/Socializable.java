package domain;

import models.User;

import com.mnt.exception.SocialObjectNotJoinableException;

public interface Socializable {
	void onFriendRequest(User user) throws SocialObjectNotJoinableException;
	void onFriendRequestAccepted(User user) throws SocialObjectNotJoinableException;
}
