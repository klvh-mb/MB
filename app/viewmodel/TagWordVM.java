package viewmodel;

import models.ArticleCategory;
import models.TagWord;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * Date: 23/10/14
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagWordVM {

    @JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("nos") public int noSocialObjects;
    @JsonProperty("noc") public int noClicks;

    public static TagWordVM toTagWordVM(TagWord tagWord) {
		TagWordVM tagWordVM = new TagWordVM();
        tagWordVM.id = tagWord.id;
        tagWordVM.name = tagWord.displayWord;
        tagWordVM.noSocialObjects = tagWord.socialObjectCount;
        tagWordVM.noClicks = tagWord.noClicks;
		return tagWordVM;
	}
}
