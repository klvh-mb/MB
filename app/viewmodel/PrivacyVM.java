package viewmodel;

import models.Privacy;

public class PrivacyVM {

	public Long id;
	public int activity;
	public int joinedCommunity;
	public int friendList;
	public int detail;
	
	public PrivacyVM(Privacy privacy) {
		this.id = privacy.user.id;
		this.activity = privacy.showActivitiesTo;
		this.joinedCommunity = privacy.showJoinedcommunitiesTo;
		this.friendList = privacy.showFriendListTo;
		this.detail = privacy.showDetailsTo;
	}
}
