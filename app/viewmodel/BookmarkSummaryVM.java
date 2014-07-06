package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

public class BookmarkSummaryVM {
    @JsonProperty("b") public boolean hasBookmarks;
	@JsonProperty("qc") public long qnaBookmarkCount;
	@JsonProperty("pc") public long postBookmarkCount;
	@JsonProperty("ac") public long articleBookmarkCount;
	
	public BookmarkSummaryVM(long qnaBookmarkCount, long postBookmarkCount, long articleBookmarkCount) {
	    this.hasBookmarks = (qnaBookmarkCount + postBookmarkCount + articleBookmarkCount > 0);
	    this.qnaBookmarkCount = qnaBookmarkCount;
	    this.postBookmarkCount = postBookmarkCount;
	    this.articleBookmarkCount = articleBookmarkCount;
	}
}
