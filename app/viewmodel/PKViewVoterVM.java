package viewmodel;

import models.User;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * Date: 18/1/15
 * Time: 12:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class PKViewVoterVM {
	@JsonProperty("pkviewId") public long pkViewId;
    @JsonProperty("userId") public long userId;
    @JsonProperty("name") public String name;


	public PKViewVoterVM(long pkViewId, long userId) {
        this.pkViewId = pkViewId;
        this.userId = userId;
        this.name = User.findById(userId).name;
    }
}
