package customdata.file;

import models.PlayGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 28/6/15
 * Time: 1:47 PM
 */
public class PlayGroupFileReader {
    private static final play.api.Logger logger = play.api.Logger.apply(PlayGroupFileReader.class);

    private List<PlayGroup> pgs = new ArrayList<>();


    public void read(String filePath) throws Exception {

    }

    public List<PlayGroup> getPGs() {
        return pgs;
    }
}
