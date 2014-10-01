package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.io.File;

import javax.persistence.NoResultException;

import common.utils.NanoSecondStopWatch;
import models.Article;
import models.ArticleCategory;
import models.User;
import play.data.DynamicForm;
import play.Play;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import targeting.Scorable;
import targeting.ScoreSortedList;
import targeting.sc.ArticleScorer;
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
	
	@Transactional
	public static Result getAllArticleCategories() {
		List<ArticleCategory> categories = ArticleCategory.getAllCategories();
		
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
	public static Result getArticlesCategorywise(Long catId, String offset) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		int start = Integer.parseInt(offset) * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT;
		final User localUser = Application.getLocalUser(session());

		List<Article> allArticles = Article.getArticlesByCategory(catId, start);
		List<ArticleVM> listOfArticles = new ArrayList<>();
		for(Article article:allArticles) {
			ArticleVM vm = new ArticleVM(article,localUser);
			if(catId != 0){
				ArticleCategory ac = ArticleCategory.getCategoryById(catId);
				vm.category_url = ac.pictureName;
			}
			listOfArticles.add(vm);
		}

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getArticlesCategorywise(cat="+catId+", off="+offset+"). Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(listOfArticles));
	}
	
	@Transactional
	public static Result getRelatedArticles(long id, Long catId) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		List<Article> allArticles = Article.relatedArticles(id, catId, DefaultValues.ARTICLES_RELATED_COUNT);
		List<ArticleVM> listOfArticles = new ArrayList<>();
		for(Article article:allArticles) {
			ArticleVM vm = new ArticleVM(article);
			listOfArticles.add(vm);
		}

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("getRelatedArticles(cat="+catId+"). Took "+sw.getElapsedMS()+"ms");
        }

		return ok(Json.toJson(listOfArticles));
	}

    @Transactional
    public static Result getSixArticles(Long catId) {
        return getTargetedArticles(catId, DefaultValues.FEATURED_ARTICLES_COUNT);
    }

    public static List<ArticleVM> getNonBookmarkedArticles(List<Article> articles, int count) {
        final User localUser = Application.getLocalUser(session());
        if (localUser.isLoggedIn()) {
            articles.removeAll(localUser.getBookmarkedArticles(0, articles.size()));
        }
        
        List<ArticleVM> articleVMs = new ArrayList<>();
        int i = 0;
        for (Article article : articles) {
            if (i == count){
                break;
            }
            ArticleVM vm = new ArticleVM(article);
            articleVMs.add(vm);
            i++;
        }
        return articleVMs;
    }
    
    @Transactional
    public static Result getHotArticles(long catId) {
        // TODO - Fix * 5 to give buffer to remove bookmarked articles
        int k = DefaultValues.ARTICLES_UTILITY_COUNT * 5;

        List<Article> mostViewed = Article.getMostViewsArticles(catId, k);

        List<Scorable<Article>> scoredRes = ArticleScorer.markScoresByViewsTime(mostViewed);
        ScoreSortedList<Article> scoreSortedList = new ScoreSortedList<>(scoredRes);

        mostViewed.clear();
        for (Scorable<Article> scorable : scoreSortedList.greatestOf(k)) {
            mostViewed.add(scorable.getObject());
        }

        List<ArticleVM> articleVMs = getNonBookmarkedArticles(mostViewed, DefaultValues.ARTICLES_UTILITY_COUNT);
        return ok(Json.toJson(articleVMs));
    }
    
    @Transactional
    public static Result getRecommendedArticles(long catId) {
        // TODO - Fix * 5 to give buffer to remove bookmarked articles
        int k = DefaultValues.ARTICLES_UTILITY_COUNT * 5;

        List<Article> mostViewed = Article.getMostLikesArticles(catId, k);

        List<Scorable<Article>> scoredRes = ArticleScorer.markScoresByLikesTime(mostViewed);
        ScoreSortedList<Article> scoreSortedList = new ScoreSortedList<>(scoredRes);

        mostViewed.clear();
        for (Scorable<Article> scorable : scoreSortedList.greatestOf(k)) {
            mostViewed.add(scorable.getObject());
        }

        List<ArticleVM> articleVMs = getNonBookmarkedArticles(mostViewed, DefaultValues.ARTICLES_UTILITY_COUNT);
        return ok(Json.toJson(articleVMs));
    }

    @Transactional
	public static Result getNewArticles(long catId) {
	    // TODO - Fix * 5 to give buffer to remove bookmarked articles
        List<ArticleVM> articleVMs = getNonBookmarkedArticles(
                Article.getArticles(catId, DefaultValues.ARTICLES_UTILITY_COUNT * 5), 
                DefaultValues.ARTICLES_UTILITY_COUNT);
        return ok(Json.toJson(articleVMs));
    }
	
	@Transactional
    private static List<Article> getFeaturedArticles(Long catId, int count) {
        Set<Article> articles = new HashSet<Article>();
        articles.addAll(Article.getMostViewsArticles(catId, DefaultValues.FEATURED_ARTICLES_COUNT));
        articles.addAll(Article.getMostLikesArticles(catId, DefaultValues.FEATURED_ARTICLES_COUNT));
        articles.addAll(Article.getArticles(catId, DefaultValues.FEATURED_ARTICLES_COUNT));
        return new ArrayList<Article>(articles);
    }
    
    @Transactional
	private static Result getTargetedArticles(Long catId, int n) {
        final User localUser = Application.getLocalUser(session());
		
		List<Article> allArticles = null;
		if (localUser.isLoggedIn()) {
		    allArticles = ArticleTargetingEngine.getTargetedArticles(localUser, catId, n);    
		} else {
		    allArticles = getFeaturedArticles(catId, n);
		}
		
		// randomize the results
		long seed = System.nanoTime();
		Collections.shuffle(allArticles, new Random(seed));
		
		List<ArticleVM> leftArticles = new ArrayList<>();
		List<ArticleVM> rightArticles = new ArrayList<>();
		
		int i = 0;
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

		SlidderArticleVM articleVM = new SlidderArticleVM(leftArticles, rightArticles);
		return ok(Json.toJson(articleVM));
	}

    /**
     * Note: No comment support on Articles right now
     * @return
     */
	@Transactional
	public static Result commentOnArticle() {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
		DynamicForm form = form().bindFromRequest();
		
		Long postId = Long.parseLong(form.get("articleId"));
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
    public static Result infoArticle(Long articleId) {
        final User localUser = Application.getLocalUser(session());
        Article article = null;
        try {
            article = Article.findById(articleId);
            article.noOfViews++;
        } catch(NoResultException e) {
            return ok("NO_RESULT");
        }
        ArticleVM vm = new ArticleVM(article, localUser);
        vm.description = article.description;
        return ok(Json.toJson(vm));
    }
	
	@Transactional
	public static Result onLike(Long articleId) throws SocialObjectNotLikableException {
		User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
		Article article = Article.findById(articleId);
		article.onLikedBy(localUser);
		return ok();
	}
	
	@Transactional
	public static Result onUnlike(Long articleId) throws SocialObjectNotLikableException {
		User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
		Article article = Article.findById(articleId);
		article.onUnlikedBy(localUser);
		localUser.doUnLike(articleId, article.objectType);
		return ok();
	}
	
	@Transactional
	public static Result onBookamrk(Long articleId) {
		User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
		Article article = Article.findById(articleId);
		article.onBookmarkedBy(localUser);
		return ok();
	}
	
	@Transactional
	public static Result onUnBookmark(Long articleId) {
		User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
		Article article = Article.findById(articleId);
		localUser.unBookmarkOn(articleId, article.objectType);
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
