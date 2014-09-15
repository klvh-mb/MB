package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.Commentable;
import domain.DefaultValues;
import domain.Likeable;
import domain.SocialObjectType;

@Entity
public class Article extends TargetingSocialObject implements Commentable, Likeable {
    private static final play.api.Logger logger = play.api.Logger.apply(Article.class);

	public Article() {}

	@Lob
	public String description;
	
	public int noOfLikes = 0;
    
	public int noOfViews = 0;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date publishedDate;

	@ManyToOne
	public ArticleCategory category;
	
	public Article(String name, String description, Boolean isFeatured, Integer targetAge, ArticleCategory category) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.publishedDate = new Date();
		this.objectType = SocialObjectType.ARTICLE;
	}
	
	@Transactional
	public static List<Article> getAllArticles() {
		Query q = JPA.em().createQuery("Select a from Article a order where deleted = false by publishedDate desc,id desc");
		return (List<Article>)q.getResultList();
	}
	
	@Transactional
	public static List<Article> getArticlesByCategory(Long id, int offset) {
		Query q;
		if (id == 0){
			q = JPA.em().createQuery("Select a from Article a where deleted = false order by publishedDate desc,id desc");
		} else {
			q = JPA.em().createQuery("Select a from Article a where category_id = ?1 and deleted = false order by publishedDate desc,id desc");
			q.setParameter(1, id);
		}
		q.setFirstResult(offset);
		q.setMaxResults(DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);

        List<Article> ret = (List<Article>)q.getResultList();
		return ret;
	}
	
	@Transactional
	public static List<Article> relatedArticles(long id, long catId, int n) {
		Query q = JPA.em().createQuery("Select a from Article a where id != ?1 AND a.category.id = ?2 and deleted = false order by publishedDate desc,id desc");
		q.setParameter(1, id);
		q.setParameter(2, catId);
		q.setMaxResults(n);
		return (List<Article>)q.getResultList();
	}
	
	@Transactional
	public static List<Article> getArticles(long catId, int n) {
		Query q = JPA.em().createQuery("Select a from Article a where " + 
		        "category_id in (select c.id from ArticleCategory c where c.categoryGroup = ?1) and deleted = false order by publishedDate desc,id desc");
		q.setParameter(1, ArticleCategory.getCategoryGroup(catId));
		q.setFirstResult(0);
		q.setMaxResults(n);
		return (List<Article>)q.getResultList();
	}

    @Transactional
    public static List<Article> getMostViewsArticles(long catId, int n) {
        Query q = JPA.em().createQuery("Select a from Article a where " + 
                "category_id in (select c.id from ArticleCategory c where c.categoryGroup = ?1) and deleted = false order by noOfViews desc");
        q.setParameter(1, ArticleCategory.getCategoryGroup(catId));
        q.setFirstResult(0);
        q.setMaxResults(n);
        return (List<Article>)q.getResultList();
    }
       
    @Transactional
    public static List<Article> getMostLikesArticles(long catId, int n) {
    	Query q = JPA.em().createQuery("Select a from Article a where " +
    	        "category_id in (select c.id from ArticleCategory c where c.categoryGroup = ?1) and deleted = false order by noOfLikes desc");
    	q.setParameter(1, ArticleCategory.getCategoryGroup(catId));
    	q.setFirstResult(0);
    	q.setMaxResults(n);
    	return (List<Article>)q.getResultList();
    }
	
	public static Article findById(Long id) {
		Query q = JPA.em().createQuery("SELECT a FROM Article a where id = ?1 and deleted = false");
		q.setParameter(1, id);
		return (Article) q.getSingleResult();
	}
	
	public static void delete(Long id, User deletedBy) {
	    Article article = Article.findById(id);
	    article.deleted = true;
	    article.deletedBy = deletedBy;
	    article.save();
	}
	
	public String getShortDescription(String description) {
		Document document = Jsoup.parse(description);
		document.select("img").remove();
		return document.toString();
	}
	
	public String getLinesFromDescription(String description) {
		
		String noHTMLString = description.replaceAll("\\<.*?>","");
		noHTMLString = noHTMLString.replaceAll("&nbsp;", " ");
		noHTMLString = noHTMLString.replaceAll("&mdash;", "-");
		noHTMLString = noHTMLString.replaceAll("[\\\r\\\n]+", " ");
		noHTMLString = noHTMLString.replaceAll("&ldquo;", "\"");
		noHTMLString = noHTMLString.replaceAll("&rdquo;", "\"");
		if(noHTMLString.length() > 104) {
			return noHTMLString.substring(0, 105);
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

	public void updateById()
	{
		this.merge();
	}
	
	public void saveArticle()
	{
		this.save();
	}

    public void delete(User deletedBy) {
        this.deleted = true;
        this.deletedBy = deletedBy;
        save();
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
	
	public boolean isLikedBy(User user) throws SocialObjectNotLikableException {
		Query q = JPA.em().createQuery("Select sr from PrimarySocialRelation sr where sr.action=?1 and sr.actor=?2 " +
				"and sr.target=?3 and sr.targetType=?4");
		q.setParameter(1, PrimarySocialRelation.Action.LIKED);
		q.setParameter(2, user.id);
		q.setParameter(3, this.id);
		q.setParameter(4, this.objectType);
		PrimarySocialRelation sr = null;
		try {
			sr = (PrimarySocialRelation)q.getSingleResult();
		}
		catch(NoResultException nre) {
			return false;
		}
		return true;
	}
	
	public boolean isBookmarkedBy(User user) {
		Query q = JPA.em().createQuery("Select sr from SecondarySocialRelation sr where sr.action=?1 and sr.actor=?2 " +
				"and sr.target=?3 and sr.targetType=?4");
		q.setParameter(1, SecondarySocialRelation.Action.BOOKMARKED);
		q.setParameter(2, user.id);
		q.setParameter(3, this.id);
		q.setParameter(4, this.objectType);
		try {
		    SecondarySocialRelation sr = (SecondarySocialRelation)q.getSingleResult();
		}
		catch(NoResultException nre) {
			return false;
		}
		return true;
	}
	
	@Override
	public void setUpdatedBy(String updatedBy) {
		
	}
	
	@Override
	public void setUpdatedDate(Date updatedDate) {
		
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
                "name='" + name + '\'' +
                '}';
    }
}
