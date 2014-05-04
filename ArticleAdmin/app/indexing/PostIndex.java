package indexing;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.github.cleverage.elasticsearch.Index;
import com.github.cleverage.elasticsearch.IndexUtils;
import com.github.cleverage.elasticsearch.Indexable;
import com.github.cleverage.elasticsearch.annotations.IndexType;

@IndexType(name = "posts")
public class PostIndex extends Index{

	public static Finder<PostIndex> find = new Finder<PostIndex>(PostIndex.class);
	
	@JsonProperty("community_id")
	public Long community_id;
	
	@JsonProperty("post_id")
	public Long post_id;
	
	@JsonProperty("description")
	public String description;
	
	@Override
	public Indexable fromIndex(Map map) {
		if (map == null) {
            return this;
        }
		this.community_id = (Long) IndexUtils.convertValue(map.get("community_id"), Long.class);
		this.post_id = (Long) IndexUtils.convertValue(map.get("post_id"), Long.class);
		this.description = (String) map.get("description");
		return this;
	}

	@Override
	public Map toIndex() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("community_id", community_id);
		map.put("post_id", post_id);
		map.put("description", description);
		return map;
	}

}
