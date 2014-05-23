package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Article;
import models.ArticleCategory;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.ArticleCategoryVM;
import viewmodel.ArticleVM;

public class ArticleController extends Controller {

	@Transactional
	public static Result addArticle() {
		Form<Article> articleForm = DynamicForm.form(Article.class).bindFromRequest();
		DynamicForm form = DynamicForm.form().bindFromRequest();
		Long category_id;
		
		Article article = articleForm.get();
		try{
		article.TargetAgeMinMonth = Integer.parseInt(form.get("TargetAgeMinMonth"));
		article.TargetAgeMaxMonth = Integer.parseInt(form.get("TargetAgeMaxMonth"));
		}
		catch(NumberFormatException ne)
		{
			return status(507,"PLEASE SELECT TARGET AGE");
		}
			if(article.TargetAgeMinMonth>=article.TargetAgeMaxMonth)
			{
				return status(508,"TargetAgeMinMonth should be less than TargetAgeMaxMonth");
			}
				if(!Article.checkTitleExists(article.name))
				{
					return status(505, "PLEASE CHOOSE OTHER TITLE");
				}
					try{
						 category_id = Long.parseLong(form.get("category_id"));
					}
					catch(NumberFormatException e){
						return status(506, "PLEASE CHOOSE CATEGORY");
					}
						try{
						article.TargetGender = Integer.parseInt(form.get("TargetGender"));
						article.TargetParentGender = Integer.parseInt(form.get("TargetParentGender"));
						}
						catch(NumberFormatException nfe)
						{
							nfe.printStackTrace();
						}
						
					article.TargetDistrict = form.get("TargetDistrict");
					ArticleCategory ac = ArticleCategory.getCategoryById(category_id);
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
		article.name = dataToUpdate.get("name");
		article.TargetAgeMinMonth = Integer.parseInt(dataToUpdate.get("TargetAgeMinMonth"));
		article.TargetAgeMaxMonth = Integer.parseInt(dataToUpdate.get("TargetAgeMaxMonth"));
		if(article.TargetAgeMinMonth>=article.TargetAgeMaxMonth)
		{
			return status(509,"TargetAgeMinMonth should be less than TargetAgeMaxMonth");
		}
		article.TargetGender = Integer.parseInt(dataToUpdate.get("TargetGender"));
		article.TargetParentGender = Integer.parseInt(dataToUpdate.get("TargetParentGender"));
		article.TargetDistrict = dataToUpdate.get("TargetDistrict");
		ArticleCategory ac = ArticleCategory.getCategoryById(Long.parseLong(dataToUpdate.get("category.id")));
		article.category = ac;
		article.description = dataToUpdate.get("description");
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
			ArticleVM vm = new ArticleVM(article.category, article.name, article.id);
			listOfArticles.add(vm);
		}
		return ok(Json.toJson(listOfArticles));
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
		return ok(Json.toJson(article));
	}
}
