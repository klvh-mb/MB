package common.cache;

import models.GameLevel;

import java.util.Collections;
import java.util.List;

public class GameLevelCache {
    // Permanent cache loaded up on system startup.

    private static List<GameLevel> gameLevels = Collections.EMPTY_LIST;

    static {
        gameLevels = GameLevel.loadGameLevels();
    }

    public static List<GameLevel> getGameLevels() {
        return gameLevels;
    }
}
