package indexing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.github.cleverage.elasticsearch.Index;
import com.github.cleverage.elasticsearch.IndexUtils;
import com.github.cleverage.elasticsearch.Indexable;
import com.github.cleverage.elasticsearch.annotations.IndexType;

@IndexType(name = "posts")
public class PostIndex extends Index{

	public static Finder<PostIndex> find = new Finder<PostIndex>(PostIndex.class);
	
	@JsonProperty("community_id") public Long community_id;
	@JsonProperty("post_id") public Long post_id;
	@JsonProperty("oid") public Long owner_id;
	@JsonProperty("description") public String description;
	@JsonProperty("comments") public List<CommentIndex> comments = new ArrayList<CommentIndex>();
	
	@JsonProperty("p") public String postedBy;
	@JsonProperty("t") public String postedOn;
	@JsonProperty("n_c") public int noOfComments;
	
	@Override
	public Indexable fromIndex(Map map) {
		if (map == null) {
            return this;
        }
		this.community_id = (Long) IndexUtils.convertValue(map.get("community_id"), Long.class);
		this.post_id = (Long) IndexUtils.convertValue(map.get("post_id"), Long.class);
		this.owner_id = (Long) IndexUtils.convertValue(map.get("oid"), Long.class);
		this.description = (String) map.get("description");
		this.comments = IndexUtils.getIndexables(map, "comments", CommentIndex.class);
		this.postedBy = (String) map.get("p");
		this.postedOn = (String) map.get("t");
		this.noOfComments = (int) map.get("n_c");
		return this;
	}

	@Override
	public Map toIndex() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("community_id", community_id);
		map.put("post_id", post_id);
		map.put("oid", owner_id);
		map.put("description", description);
		map.put("comments", IndexUtils.toIndex(comments));
		map.put("p", postedBy);
		map.put("t", postedOn);
		map.put("n_c", noOfComments);
		return map;
	}

}
