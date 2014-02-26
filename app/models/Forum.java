package models;

import domain.Likeable;

public class Forum extends SocialObject  implements Likeable {

	@Override
	public void onLike(User user) {
		recordLike(user);
	}
}
