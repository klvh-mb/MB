package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import common.model.TodayWeatherInfo;

public class TodayWeatherInfoVM {
    @JsonProperty("loc") private String location;
    @JsonProperty("ti") private String title;
    @JsonProperty("desc") private String description;
    @JsonProperty("cond") private String condition;
    @JsonProperty("code") private int conditionCode;
    @JsonProperty("icon") private String icon;
    @JsonProperty("temp") private int temperature;
    @JsonProperty("dow") private String dayOfWeek;
    @JsonProperty("today") private String today;
    @JsonProperty("ut") private DateTime updatedTime;
    
    public TodayWeatherInfoVM(TodayWeatherInfo info) {
        this.location = info.getLocation();
        this.title = info.getTitle();
        this.description = info.getDescription();
        this.condition = info.getCondition();
        this.conditionCode = info.getConditionCode();
        this.icon = info.getIcon();
        this.temperature = info.getTemperature();
        this.dayOfWeek = info.getDayOfWeek();
        this.today = info.getToday();
        this.updatedTime = info.getUpdatedTime();
    }
}
