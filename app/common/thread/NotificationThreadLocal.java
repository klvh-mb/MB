package common.thread;

/**
 * Created by IntelliJ IDEA.
 * Date: 17/3/15
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotificationThreadLocal {

    private static ThreadLocal<Boolean> skipNotificationLocal = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    public static void disableNotification(boolean off) {
        skipNotificationLocal.set(off);
    }

    public static boolean isDisableNotification() {
        return skipNotificationLocal.get();
    }
}
