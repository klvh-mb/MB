package domain;

import models.User;

import com.mnt.exception.SocialObjectNotJoinableException;

public interface Socializable {
	void sendFriendInviteTo(User user) throws SocialObjectNotJoinableException;
	void onFriendRequestAccepted(User user) throws SocialObjectNotJoinableException;
}
