package common.cache;

import models.Emoticon;
import models.Icon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class IconCache {
    // Permanent cache loaded up on system startup.

    private static List<Emoticon> emoticons;
    private static List<Icon> communityIcons;
    private static Map<Integer, Icon> weatherIconsMap;

    static {
        emoticons = Emoticon.loadEmoticons();
        communityIcons = Icon.loadCommunityIcons();
        weatherIconsMap = new HashMap<Integer, Icon>();
    }

    public static List<Emoticon> getEmoticons() {
        return emoticons;
    }
    
    public static List<Icon> getCommunityIcons() {
		return communityIcons;
	}
    
    public static Icon getWeatherIcon(int conditionCode) {
        if (weatherIconsMap.containsKey(conditionCode)) {
            return weatherIconsMap.get(conditionCode);
        }
        Icon icon = Icon.loadWeatherIcon(conditionCode);
        weatherIconsMap.put(conditionCode, icon);
        return icon;
    }
}
