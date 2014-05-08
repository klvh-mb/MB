package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mnt.exception.SocialObjectNotCommentableException;

import domain.CommentType;

import models.Article;
import models.ArticleCategory;
import models.Comment;
import models.Community;
import models.Post;
import models.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.ArticleCategoryVM;
import viewmodel.ArticleVM;
import viewmodel.CommunityPostCommentVM;
import viewmodel.SlidderArticleVM;

public class ArticleController extends Controller {

	@Transactional
	public static Result addArticle() {
		Form<Article> articleForm = DynamicForm.form(Article.class).bindFromRequest();
		DynamicForm form = DynamicForm.form().bindFromRequest();
		
		Long category_id = Long.parseLong(form.get("category_id"));
		
		ArticleCategory ac = ArticleCategory.getCategoryById(category_id);
		Article article = articleForm.get();
		article.category = ac;
		article.publishedDate = new Date();
		article.saveArticle();
		return ok();
	}
	
	@Transactional
	public static Result updateArticle() {
		Form<String> form = DynamicForm.form(String.class).bindFromRequest();
		Map<String, String> dataToUpdate = form.data();
		
		Long id = Long.parseLong(dataToUpdate.get("id"));
		Article article = Article.findById(id);
		article.name = dataToUpdate.get("nm");
		if(dataToUpdate.get("frd").equals("true"))
				article.isFeatured = true;
		if(dataToUpdate.get("frd").equals("false"))
				article.isFeatured = false;
		article.targetAge = Integer.parseInt(dataToUpdate.get("ta"));
		ArticleCategory ac = ArticleCategory.getCategoryById(Long.parseLong(dataToUpdate.get("ct.id")));
		article.category = ac;
		article.description = dataToUpdate.get("ds");
		article.setUpdatedDate(new Date());
		article.updateById();
		return ok();
	}
	
	@Transactional
	public static Result getAllArticleCategory() {
		List<ArticleCategory> articleCategorys = ArticleCategory.getAllCategory();
		
		List<ArticleCategoryVM> articleCategoryVMs = new ArrayList<>();
		for(ArticleCategory articleCategory : articleCategorys) {
			ArticleCategoryVM vm = ArticleCategoryVM.articleCategoryVM(articleCategory);
			articleCategoryVMs.add(vm);
		}
		return ok(Json.toJson(articleCategoryVMs));
	}
	
	@Transactional
	public static Result getAllArticles() {
		List<Article> allArticles = Article.getAllArticles();
		List<ArticleVM> listOfArticles = new ArrayList<>();
		for(Article article:allArticles) {
			ArticleVM vm = new ArticleVM(article);
			listOfArticles.add(vm);
		}
		return ok(Json.toJson(listOfArticles));
	}
	
	@Transactional
	public static Result getArticlesCategorywise(Long cat_id) {
		List<Article> allArticles = Article.getArticlesByCategory(cat_id);
		ArticleCategory ac = ArticleCategory.getCategoryById(cat_id);
		List<ArticleVM> listOfArticles = new ArrayList<>();
		for(Article article:allArticles) {
			ArticleVM vm = new ArticleVM(article);
			vm.category_url = ac.pictureName;
			listOfArticles.add(vm);
		}
		return ok(Json.toJson(listOfArticles));
	}
	
		@Transactional
	public static Result getRelatedArticles(long id, Long categoy_id) {
		System.out.println(categoy_id);
		List<Article> allArticles = Article.relatedArticles(id,categoy_id);
		List<ArticleVM> listOfArticles = new ArrayList<>();
		for(Article article:allArticles) {
			ArticleVM vm = new ArticleVM(article);
			listOfArticles.add(vm);
		}
		return ok(Json.toJson(listOfArticles));
	}
	
	@Transactional
	public static Result getEightArticles() {
		int i = 0;
		List<Article> allArticles = Article.getEightArticles();
		List<ArticleVM> leftArticles = new ArrayList<>();
		List<ArticleVM> rightArticles = new ArrayList<>();
		for(Article article:allArticles) {
			if(i<4){
				ArticleVM vm = new ArticleVM(article);
				leftArticles.add(vm);
			}else {
				ArticleVM vm = new ArticleVM(article);
				rightArticles.add(vm);
			}
			i++;
		}
		
		List<ArticleCategoryVM> categoryVMs = new ArrayList<>();
		List<ArticleCategory> categories = ArticleCategory.getFourCategories(4);
		for(ArticleCategory ac : categories) {
			ArticleCategoryVM vm = ArticleCategoryVM.articleCategoryVM(ac);
			categoryVMs.add(vm);
		}
		
		SlidderArticleVM articleVM = new SlidderArticleVM(leftArticles, rightArticles, categoryVMs);
		return ok(Json.toJson(articleVM));
	}
	
	@Transactional
	public static Result commentOnArticle() {
		final User localUser = Application.getLocalUser(session());
		DynamicForm form = form().bindFromRequest();
		
		Long postId = Long.parseLong(form.get("article_id"));
		String commentText = form.get("commentText");
		
		Article p = Article.findById(postId);
		
		try {
			//NOTE: Currently commentType is hardcoded to SIMPLE
			p.onComment(localUser, commentText, CommentType.SIMPLE);
		} catch (SocialObjectNotCommentableException e) {
			e.printStackTrace();
		}
		return ok(Json.toJson(p.id));
	}
	
	@Transactional
	public static Result getDescriptionOdArticle(Long art_id) {
		Article article = Article.findById(art_id);
		Map<String, String> description = new HashMap<>();
		description.put("description", article.description);
		return ok(Json.toJson(description));
	}
	
	@Transactional
	public static Result deleteArticle(Long art_id) {
		Article.deleteByID(art_id);
		return ok();
	}
	
	@Transactional
	public static Result infoArticle(Long art_id) {
		Article article = Article.findById(art_id);
		ArticleVM vm = new ArticleVM(article);
		vm.description = article.description;
		return ok(Json.toJson(vm));
	}
	
	@Transactional
	public static Result getAllComments(Long id) {
		Article article = Article.findById(id);
		List<CommunityPostCommentVM> commentsToShow = new ArrayList<>();
		List<Comment> comments = article.getCommentsOfPost();
		for(Comment comment : comments) {
			CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment);
			commentsToShow.add(commentVM);
		}
		return ok(Json.toJson(commentsToShow));
	}
	
	
}
