package viewmodel;

import models.Privacy;

public class PrivacyVM {

    public int activity = 1;
    public int joinedCommunity = 1;
    public int friendList = 1;
    public int detail = 1;
    
    public PrivacyVM(Privacy privacy) {
        if (privacy != null) {
            this.activity = privacy.showActivitiesTo;
            this.joinedCommunity = privacy.showJoinedcommunitiesTo;
            this.friendList = privacy.showFriendListTo;
            this.detail = privacy.showDetailsTo;
        }
    }
}