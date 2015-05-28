package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.Commentable;
import domain.DefaultValues;
import domain.Likeable;

@Entity
public class Article extends TargetingSocialObject implements Commentable, Likeable {
    private static final play.api.Logger logger = play.api.Logger.apply(Article.class);

	public Article() {}

	@Lob
	public String description;
	
	public int noOfLikes = 0;
    
	public int noOfViews = 0;
	
	public Date publishedDate;

	@ManyToOne
	public ArticleCategory category;
	
	@Transactional
	public static List<Article> getArticlesByCategory(Long catId, int offset) {
		Query q;
		if (catId == 0){
			q = JPA.em().createQuery("Select a from Article a where " + 
			        "a.category.id in (select c.id from ArticleCategory c where c.categoryGroup = ?1) and a.deleted = false order by publishedDate desc,id desc");
			q.setParameter(1, ArticleCategory.getCategoryGroup(catId));
		} else {
			q = JPA.em().createQuery("Select a from Article a where a.category.id = ?1 and a.deleted = false order by publishedDate desc,id desc");
			q.setParameter(1, catId);
		}
		q.setFirstResult(offset);
		q.setMaxResults(DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);

        List<Article> ret = (List<Article>)q.getResultList();
		return ret;
	}
	
	@Transactional
	public static List<Article> relatedArticles(long id, long catId, int n) {
		Query q = JPA.em().createQuery("Select a from Article a where id != ?1 AND a.category.id = ?2 and a.deleted = false order by publishedDate desc,id desc");
		q.setParameter(1, id);
		q.setParameter(2, catId);
		q.setMaxResults(n);
		return (List<Article>)q.getResultList();
	}
	
	@Transactional
	public static List<Article> getArticles(long catId, int n) {
		Query q = JPA.em().createQuery("Select a from Article a where " + 
		        "a.category.id in (select c.id from ArticleCategory c where c.categoryGroup = ?1) and a.deleted = false order by publishedDate desc,id desc");
		q.setParameter(1, ArticleCategory.getCategoryGroup(catId));
		q.setFirstResult(0);
		q.setMaxResults(n);
		return (List<Article>)q.getResultList();
	}

    @Transactional
    public static List<Article> getMostViewsArticles(long catId, int n) {
        Query q = JPA.em().createQuery("Select a from Article a where " + 
                "a.category.id in (select c.id from ArticleCategory c where c.categoryGroup = ?1) and a.deleted = false order by noOfViews desc");
        q.setParameter(1, ArticleCategory.getCategoryGroup(catId));
        q.setFirstResult(0);
        q.setMaxResults(n);
        return (List<Article>)q.getResultList();
    }
       
    @Transactional
    public static List<Article> getMostLikesArticles(long catId, int n) {
    	Query q = JPA.em().createQuery("Select a from Article a where " +
    	        "a.category.id in (select c.id from ArticleCategory c where c.categoryGroup = ?1) and a.deleted = false order by noOfLikes desc");
    	q.setParameter(1, ArticleCategory.getCategoryGroup(catId));
    	q.setFirstResult(0);
    	q.setMaxResults(n);
    	return (List<Article>)q.getResultList();
    }
	
	public static Article findById(Long id) {
		Query q = JPA.em().createQuery("SELECT a FROM Article a where id = ?1 and a.deleted = false");
		q.setParameter(1, id);
		return (Article) q.getSingleResult();
	}
	
	public String getShortDescription(String description) {
		Document document = Jsoup.parse(description);
	 	document.select("img").remove();
	 	String desc = document.toString();
	 	if (desc.length() > DefaultValues.DEFAULT_PREVIEW_CHARS) {
	 	    return desc.substring(0, DefaultValues.DEFAULT_PREVIEW_CHARS);
	 	}
	 	return desc;
	}
	
	public String getLinesFromDescription(String description) {
		
		String noHTMLString = description.replaceAll("\\<.*?>","");
		noHTMLString = noHTMLString.replaceAll("&nbsp;", " ");
		noHTMLString = noHTMLString.replaceAll("&mdash;", "-");
		noHTMLString = noHTMLString.replaceAll("[\\\r\\\n]+", " ");
		noHTMLString = noHTMLString.replaceAll("&ldquo;", "\"");
		noHTMLString = noHTMLString.replaceAll("&rdquo;", "\"");
		if(noHTMLString.length() > DefaultValues.DEFAULT_PREVIEW_CHARS) {
			return noHTMLString.substring(0, DefaultValues.DEFAULT_PREVIEW_CHARS);
		}
		return noHTMLString;
		
	}
	public String getFirstImageFromDescription(String description) {
		Document document = Jsoup.parse(description);
		Elements links = document.select("img");
		if(links.size()>0) {
			return links.get(0).attr("src");
		}
		return "No Image";
	}

	@Override
	public void onLikedBy(User user) {
		recordLike(user);
		this.noOfLikes++;
		user.likesCount++;
	}

    @Override
    public void onUnlikedBy(User user) throws SocialObjectNotLikableException {
        this.noOfLikes--;
        user.likesCount--;
    }
    
	public void onBookmarkedBy(User user) {
		recordBookmark(user);
	}
	
	@Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Article) {
            final Article other = (Article) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
	
    @Override
    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "views='" + noOfViews + '\'' +
                "likes='" + noOfLikes + '\'' +
                "published='" + publishedDate + '\'' +
                "category='" + category.toString() + '\'' +
                '}';
    }
}
