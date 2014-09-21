package security;

import models.Community;
import models.User;

/**
 * Created by IntelliJ IDEA.
 * Date: 21/9/14
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommunityPermission {

    /**
     * @param localUser
     * @param comm
     * @return
     */
    public static boolean canPostOnCommunity(User localUser, Community comm) {
        if (comm.communityType == Community.CommunityType.OPEN) {
            return true;
        }
        else {
            return localUser.isMemberOf(comm) || localUser.id.equals(comm.owner.id);
        }
    }
}
