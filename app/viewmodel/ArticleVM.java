package viewmodel;

import java.util.Date;

import models.Article;
import models.ArticleCategory;
import models.User;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import com.mnt.exception.SocialObjectNotLikableException;

public class ArticleVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("ds") public String description;
	@JsonProperty("sds") public String shortDescription;
	@JsonProperty("lds") public String lines;
	@JsonProperty("ct") public ArticleCategory category;
	@JsonProperty("pd") public Date publishedDate;
	@JsonProperty("img_url") public String img_url;
	@JsonProperty("category_url") public String category_url;
	@JsonProperty("nol") public int noOfLikes;
	@JsonProperty("nov") public int noOfViews;
	@JsonProperty("isLike") public boolean isLike = false;	
	@JsonProperty("isBookmarked") public boolean isBookmarked = false;	

	public ArticleVM(Article article) {
		this.category = article.category;
		this.name = article.name;
		this.id = article.id;
		this.noOfLikes = article.noOfLikes;
		this.noOfViews = article.noOfViews;
		
		this.publishedDate = article.publishedDate;

		if(StringUtils.isEmpty(article.description)) {
			article.description = "";
		} else {
			this.description = article.description;
			this.img_url = article.getFirstImageFromDescription(article.description);
			this.shortDescription = article.getShortDescription(article.description);
			this.lines = article.getLinesFromDescription(article.description);
		}
	}

	public ArticleVM(Article article, User user) {
		this(article);
		try {
			this.isLike = article.isLikedBy(user);
			this.isBookmarked = article.isBookmarkedBy(user);
		} catch (SocialObjectNotLikableException e) {
			;
		}
	}
}
