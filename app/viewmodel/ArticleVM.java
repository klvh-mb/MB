package viewmodel;

import java.util.ArrayList;
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
	@JsonProperty("cs") public List<CommunityPostCommentVM> comments;
	
	public ArticleVM(Article article) {
		this.category = article.category;
		this.name = article.name;
		this.id = article.id;
		this.isFeatured = article.isFeatured;
		this.description = article.description;
		this.noOfComments = article.getCommentsOfPost().size();

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
