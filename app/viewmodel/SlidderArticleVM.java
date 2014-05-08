package viewmodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class SlidderArticleVM {
	@JsonProperty("la") public List<ArticleVM> leftArticles;
	@JsonProperty("ra") public List<ArticleVM> rightArticles;
	@JsonProperty("ct") public List<ArticleCategoryVM> categories;
	
	public SlidderArticleVM( List<ArticleVM> leftArticles, List<ArticleVM> rightArticles, List<ArticleCategoryVM> categories) {
		this.leftArticles = leftArticles;
		this.rightArticles = rightArticles;
		this.categories = categories;
	}

}
