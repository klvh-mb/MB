package viewmodel;

import java.util.List;

import models.ArticleCategory;

import org.codehaus.jackson.annotate.JsonProperty;

public class SlidderfArticleVM {
	@JsonProperty("la") public List<ArticleVM> leftArticles;
	@JsonProperty("ra") public List<ArticleVM> rightArticles;
	
	public SlidderfArticleVM( List<ArticleVM> leftArticles, List<ArticleVM> rightArticles) {
		this.leftArticles = leftArticles;
		this.rightArticles = rightArticles;
	}

}
