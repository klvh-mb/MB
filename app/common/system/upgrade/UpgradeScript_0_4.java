package common.system.upgrade;

import models.Icon;
import models.SystemVersion;

/**
 * 1) Weather icons upgrade. 
 * 
 * @author keithlei
 *
 */
public class UpgradeScript_0_4 extends UpgradeScript {
    private static final play.api.Logger logger = play.api.Logger.apply(UpgradeScript_0_4.class);
    
    public UpgradeScript_0_4() {
    }
    
    @Override
    public String getVersion() {
        return "0.4";
    }
    
    @Override
    public void insertToSystemVersion() {
        SystemVersion version = new SystemVersion(
                getVersion(), 
                this.getClass().getName(), 
                "1) Weather icons upgrade.");
        version.save();
    }
    
    @Override
    public boolean upgrade() throws Exception {
        logger.underlyingLogger().info("Weather icons upgrade...");
        
        Icon icon = Icon.getWeatherIcon(29);
        icon.url = "/assets/app/images/weather/weather_icons-10.png";
        icon.save();
        
        icon = Icon.getWeatherIcon(30);
        icon.url = "/assets/app/images/weather/weather_icons-11.png";
        icon.save();
        
        return true;
    }
}