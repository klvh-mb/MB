package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

import javax.persistence.NoResultException;

import common.utils.NanoSecondStopWatch;
import models.Article;
import models.ArticleCategory;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.Play;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import targeting.sc.ArticleTargetingEngine;
import viewmodel.ArticleCategoryVM;
import viewmodel.ArticleVM;
import viewmodel.SlidderArticleVM;

import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotLikableException;

import domain.CommentType;
import domain.DefaultValues;

public class ArticleController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(ArticleController.class);

    private static final String STORAGE_PATH = Play.application().configuration().getString("storage.path"); 
    
    private static final int NUM_CATEGORIES_HP = 6;

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
	public static Result getAllArticleCategory() {
		List<ArticleCategory> categories = ArticleCategory.getAllCategory();
		
		List<ArticleCategoryVM> articleCategoryVMs = new ArrayList<>();
		for(ArticleCategory articleCategory : categories) {
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
	public static Result getArticlesCategorywise(Long cat_id, String offset) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		int start = Integer.parseInt(offset) * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT;
		final User localUser = Application.getLocalUser(session());

		List<Article> allArticles = Article.getArticlesByCategory(cat_id, start);
		List<ArticleVM> listOfArticles = new ArrayList<>();
		for(Article article:allArticles) {
			ArticleVM vm = new ArticleVM(article,localUser);
			if(cat_id != 0){
				ArticleCategory ac = ArticleCategory.getCategoryById(cat_id);
				vm.category_url = ac.pictureName;
			}
			listOfArticles.add(vm);
		}

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"] getArticlesCategorywise(cat="+cat_id+", off="+offset+"). Took "+sw.getElapsedMS()+"ms");
        }
		return ok(Json.toJson(listOfArticles));
	}
	
	@Transactional
	public static Result getRelatedArticles(long id, Long categoryId) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		List<Article> allArticles = Article.relatedArticles(id, categoryId, DefaultValues.ARTICLES_RELATED_COUNT);
		List<ArticleVM> listOfArticles = new ArrayList<>();
		for(Article article:allArticles) {
			ArticleVM vm = new ArticleVM(article);
			listOfArticles.add(vm);
		}

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("getRelatedArticles(cat="+categoryId+"). Took "+sw.getElapsedMS()+"ms");
        }

		return ok(Json.toJson(listOfArticles));
	}

    @Transactional
    public static Result getSixArticles() {
        return getTargetedArticles(DefaultValues.FEATURED_ARTICLES_COUNT);
    }

    public static List<ArticleVM> getUtilityArticles(List<Article> articles) {
        final User localUser = Application.getLocalUser(session());
        List<ArticleVM> articleVMs = new ArrayList<>();
        articles.removeAll(localUser.getBookmarkedArticles(0, articles.size()));
        int i = 0;
        for (Article article : articles) {
            if (i == DefaultValues.ARTICLES_UTILITY_COUNT){
                break;
            }
            ArticleVM vm = new ArticleVM(article);
            articleVMs.add(vm);
            i++;
        }
        return articleVMs;
    }
    
    @Transactional
    public static Result getHotArticles() {
        // TODO - Fix * 5 to give buffer to remove bookmarked articles
        List<ArticleVM> articleVMs = getUtilityArticles(
                Article.getMostViewsArticles(DefaultValues.ARTICLES_UTILITY_COUNT * 5));
        return ok(Json.toJson(articleVMs));
    }
    
    @Transactional
    public static Result getRecommendedArticles() {
        // TODO - Fix * 5 to give buffer to remove bookmarked articles
        List<ArticleVM> articleVMs = getUtilityArticles(
                Article.getMostLikesArticles(DefaultValues.ARTICLES_UTILITY_COUNT * 5));
        return ok(Json.toJson(articleVMs));
    }

    @Transactional
	public static Result getNewArticles() {
	    // TODO - Fix * 5 to give buffer to remove bookmarked articles
        List<ArticleVM> articleVMs = getUtilityArticles(
                Article.getArticles(DefaultValues.ARTICLES_UTILITY_COUNT * 5));
        return ok(Json.toJson(articleVMs));
    }
	
	@Transactional
	public static Result getArticles(int n) {
		int i = 0;
		List<Article> allArticles = Article.getArticles(n);
		List<ArticleVM> leftArticles = new ArrayList<>();
		List<ArticleVM> rightArticles = new ArrayList<>();
		for (Article article:allArticles) {
			if (i < n/2){
				ArticleVM vm = new ArticleVM(article);
				leftArticles.add(vm);
			} else {
				ArticleVM vm = new ArticleVM(article);
				rightArticles.add(vm);
			}
			i++;
		}
		
		List<ArticleCategoryVM> categoryVMs = new ArrayList<>();
		List<ArticleCategory> categories = ArticleCategory.getCategories(NUM_CATEGORIES_HP);
		for(ArticleCategory ac : categories) {
			ArticleCategoryVM vm = ArticleCategoryVM.articleCategoryVM(ac);
			categoryVMs.add(vm);
		}
		
		SlidderArticleVM articleVM = new SlidderArticleVM(leftArticles, rightArticles, categoryVMs);
		return ok(Json.toJson(articleVM));
	}

    @Transactional
	public static Result getTargetedArticles(int n) {
        final User localUser = Application.getLocalUser(session());

		int i = 0;
		List<Article> allArticles = ArticleTargetingEngine.getTargetedArticles(localUser, n);
		List<ArticleVM> leftArticles = new ArrayList<>();
		List<ArticleVM> rightArticles = new ArrayList<>();
		for (Article article:allArticles) {
            if (i == n) {
                break;
            }

			if (i < n/2){
				ArticleVM vm = new ArticleVM(article);
				leftArticles.add(vm);
			} else {
				ArticleVM vm = new ArticleVM(article);
				rightArticles.add(vm);
			}
			i++;
		}

		List<ArticleCategoryVM> categoryVMs = new ArrayList<>();
		List<ArticleCategory> categories = ArticleCategory.getCategories(NUM_CATEGORIES_HP);
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
	public static Result getDescriptionOfArticle(Long art_id) {
		Article article = Article.findById(art_id);
		Map<String, String> description = new HashMap<>();
		description.put("description", article.description);
		return ok(Json.toJson(description));
	}
	
	@Transactional
	public static Result deleteArticle(Long art_id) {
		Article.deleteById(art_id);
		return ok();
	}
	
    @Transactional
    public static Result infoArticle(Long art_id) {
        final User localUser = Application.getLocalUser(session());
        Article article = null;
        try {
            article = Article.findById(art_id);
            article.noOfViews++;
        } catch(NoResultException e) {
            return ok("NO_RESULT");
        }
        ArticleVM vm = new ArticleVM(article, localUser);
        vm.description = article.description;
        return ok(Json.toJson(vm));
    }
	
	@Transactional
	public static Result onLike(Long article_id) throws SocialObjectNotLikableException {
		User localUser = Application.getLocalUser(session());
		Article article = Article.findById(article_id);
		article.onLikedBy(localUser);
		return ok();
	}
	
	@Transactional
	public static Result onUnlike(Long article_id) throws SocialObjectNotLikableException {
		User localUser = Application.getLocalUser(session());
		Article article = Article.findById(article_id);
		article.onUnlikedBy(localUser);
		localUser.doUnLike(article_id, article.objectType);
		return ok();
	}
	
	@Transactional
	public static Result onBookamrk(Long article_id) {
		User localUser = Application.getLocalUser(session());
		Article article = Article.findById(article_id);
		article.onBookmarkedBy(localUser);
		return ok();
	}
	
	@Transactional
	public static Result onUnBookmark(Long article_id) {
		User localUser = Application.getLocalUser(session());
		Article article = Article.findById(article_id);
		localUser.unBookmarkOn(article_id, article.objectType);
		return ok();
	}
	
	@Transactional
	public static Result getBookmarkedArticles(int offset) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
		List<ArticleVM> articles = new ArrayList<>();
		List<Article> bookmarkArticles = localUser.getBookmarkedArticles(offset, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
		if(bookmarkArticles != null ){
			for(Article a : bookmarkArticles) {
				ArticleVM vm = new ArticleVM(a,localUser);
				articles.add(vm);
			}
		}

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"] getBookmarkedArticles. Took "+sw.getElapsedMS()+"ms");
        }
		return ok(Json.toJson(articles));
	}
	
	@Transactional
    public static Result getImage(Long year, Long month, Long date, String name) {
	    response().setHeader("Cache-Control", "max-age=604800");
        String path = getImageUrl(year, month, date, name);

        logger.underlyingLogger().debug("getImage. path="+path);
        return ok(new File(path));
    }
    
    public static String getImageUrl(Long year, Long month, Long date, String name) {
        return STORAGE_PATH + "/article/" + year + "/" + month + "/" + date + "/" + name;
    }
}
