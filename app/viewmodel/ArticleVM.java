package viewmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Article;
import models.ArticleCategory;
import models.Comment;

import org.codehaus.jackson.annotate.JsonProperty;

public class ArticleVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("ds") public String description;
	@JsonProperty("frd") public Boolean isFeatured;	
	@JsonProperty("ct") public ArticleCategory category;
	@JsonProperty("n_c") public int noOfComments;
	@JsonProperty("ta") public int targetAge;
	@JsonProperty("cs") public List<CommunityPostCommentVM> comments;
	@JsonProperty("pd") public Date publishedDate;
	@JsonProperty("img_url") public String img_url;
	@JsonProperty("category_url") public String category_url;
	@JsonProperty("lds") public String lines;
	
	public ArticleVM(Article article) {
		this.category = article.category;
		this.name = article.name;
		this.id = article.id;
		this.isFeatured = article.isFeatured;
		if(article.targetAge != null ) { 
			this.targetAge = article.targetAge;
		}
		
		this.noOfComments = article.getCommentsOfPost().size();
		this.publishedDate = article.publishedDate;
		
		if(article.description == null || article.description.isEmpty()) {
			article.description = "";
		} else {
			this.img_url = article.getFirstImageFromDescription(article.description);
			this.description = article.getShortDescription(article.description);
			this.lines = article.getLinesFromDescription(article.description);
		}
		this.img_url = article.getFirstImageFromDescription(article.description);
		
		List<CommunityPostCommentVM> commentsToShow = new ArrayList<>();
		int i = 0;
		List<Comment> comments = article.getCommentsOfPost();
		for(Comment comment : comments) {
			i++;
			CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment);
			commentsToShow.add(commentVM);
			if(i == 3){
				break;
			}
		}
		
		this.comments = commentsToShow;
		
	}

}
