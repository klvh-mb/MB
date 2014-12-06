package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 6/12/14
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class HotCommunityParentVM {

    @JsonProperty("hcomm") public List<HotCommunityVM> hotCommunities = new ArrayList<>();

    public void addHotCommunityVM(HotCommunityVM vm) {
        hotCommunities.add(vm);
    }
}
