package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import common.cache.GameLevelCache;
import controllers.Application;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * Insert into GameLevel (name,level,fromPoints,toPoints,icon_id) values 
 * ('L1',1,0,50,135),
 * ('L2',2,51,150,136),
 * ('L3',3,151,500,137),
 * ('L4',4,501,1000,138),
 * ('L5',5,1001,2000,139),
 * ('L6',6,2001,4000,140),
 * ('L7',7,4001,7000,141),
 * ('L8',8,7001,12000,142),
 * ('L9',9,12001,20000,143),
 * ('L10',10,20001,30000,144);
 * 
 * @author keithlei
 */
@Entity
public class GameLevel {
    private static play.api.Logger logger = play.api.Logger.apply(GameLevel.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public String name;
    
    public Long level;
    
    public Long fromPoints;
    
    public Long toPoints;
    
    @ManyToOne
    public Icon icon;
    
    public GameLevel() {}
    
    public GameLevel(String name, Long level, Long fromPoints, Long toPoints, Icon icon) {
        this.name = name;
        this.level = level;
        this.fromPoints = fromPoints;
        this.toPoints = toPoints;
        this.icon = Icon.getGameLevelIcon(level.intValue());
    }
    
    public static List<GameLevel> loadGameLevels() {
        Query q = JPA.em().createQuery("Select i from GameLevel i order by level");
        return (List<GameLevel>)q.getResultList();
    }
    
    public static List<GameLevel> getGameLevels() {
        return GameLevelCache.getGameLevels();
    }
    
    public static GameLevel getGameLevel(Long points) {
        for (GameLevel gameLevel : getGameLevels()) {
            if (points >= gameLevel.fromPoints && points <= gameLevel.toPoints) {
                return gameLevel;
            }
        }
        logger.underlyingLogger().error(String.format("[u=%d][pts=%d] Failed to get game level. Returning level 1", Application.getLocalUserId(), points));
        return getGameLevels().get(0);
    }
    
    public static GameLevel getNextGameLevel(GameLevel gameLevel) {
    	for (GameLevel nextLevel : getGameLevels()) {
    		if (nextLevel.level == gameLevel.level + 1)
    			return nextLevel;
    	}
    	return null;
    }
    
    @Transactional
    public void save() {
        JPA.em().persist(this);
        JPA.em().flush();	  
    }
      
    @Transactional
    public void delete() {
        JPA.em().remove(this);
    }
    
    @Transactional
    public void merge() {
        JPA.em().merge(this);
    }
    
    @Transactional
    public void refresh() {
        JPA.em().refresh(this);
    }
}
