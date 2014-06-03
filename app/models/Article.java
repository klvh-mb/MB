package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import models.SocialRelation.Action;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.Commentable;
import domain.Likeable;
import domain.SocialObjectType;

@Entity
public class Article extends SocialObject implements Commentable, Likeable {

	public Article() {}

	@Lob
	public String description;
	
	
	public int targetAgeMinMonth;
	public int targetAgeMaxMonth;

	public int noOfLikes=0;
	public int targetGender;
    public int targetParentGender;
    public String targetDistrict;
    
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date publishedDate;

    @Column(nullable=false)
	public boolean excludeFromTargeting = false;

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
		Query q = JPA.em().createQuery("Select a from Article a order by publishedDate desc");
		return (List<Article>)q.getResultList();
	}
	
	@Transactional
	public static List<Article> getArticlesByCategory(Long id, int offset) {
		Query q  = null;
		if(id == 0){
			q = JPA.em().createQuery("Select a from Article a order by publishedDate DESC");
			
		}else{
			q = JPA.em().createQuery("Select a from Article a where category_id = ?1 order by publishedDate DESC");
			q.setParameter(1, id);
		}
		q.setFirstResult(offset);
		q.setMaxResults(5);
		System.out.println("OFFSET :: "+offset);
		return (List<Article>)q.getResultList();
	}
	
		@Transactional
	public static List<Article> relatedArticles(long id,long categoy_id) {
		Query q = JPA.em().createQuery("Select a from Article a where id != ?1 AND a.category.id = ?2");
		q.setParameter(1, id);
		q.setParameter(2, categoy_id);
		return (List<Article>)q.getResultList();
	}
	
	@Transactional
	public static List<Article> getArticles(int n) {
		//  Select * from Article where featured = 1 limit <= 8
		Query q = JPA.em().createQuery("Select a from Article a order by publishedDate desc");
		q.setFirstResult(0);
		q.setMaxResults(n);
		return (List<Article>)q.getResultList();
	}
	
	public static Article findById(Long id) {
		Query q = JPA.em().createQuery("SELECT a FROM Article a where id = ?1");
		q.setParameter(1, id);
		return (Article) q.getSingleResult();
	}
	
	public static int deleteByID(Long id) {
		Query q = JPA.em().createQuery("DELETE FROM Article u where id = ?1");
		q.setParameter(1, id);
		return q.executeUpdate();
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

	@Override
	public void onLikedBy(User user) {
		recordLike(user);
	}
	
	public void onBookmarkedBy(User user) {
		recordBookmark(user);
	}

	@Override
	public void onUnlikedBy(User so)
			throws SocialObjectNotLikableException {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isLikedBy(User user) throws SocialObjectNotLikableException {
		Query q = JPA.em().createQuery("Select sr from SocialRelation sr where sr.action=?1 and sr.actor=?2 " +
				"and sr.target=?3 and sr.targetType=?4");
		q.setParameter(1, Action.LIKED);
		q.setParameter(2, user.id);
		q.setParameter(3, this.id);
		q.setParameter(4, this.objectType);
		SocialRelation sr = null;
		try {
			sr = (SocialRelation)q.getSingleResult();
			System.out.println("SR ::"+sr.id);
		}
		catch(NoResultException nre) {
			System.out.println("No Result For SR");
			return false;
		}
		return true;
	}
	
	public boolean isBookmarkedBy(User user) {
		Query q = JPA.em().createQuery("Select sr from SocialRelation sr where sr.action=?1 and sr.actor=?2 " +
				"and sr.target=?3 and sr.targetType=?4");
		q.setParameter(1, Action.BOOKMARKED);
		q.setParameter(2, user.id);
		q.setParameter(3, this.id);
		q.setParameter(4, this.objectType);
		SocialRelation sr = null;
		try {
			sr = (SocialRelation)q.getSingleResult();
		}
		catch(NoResultException nre) {
			System.out.println("No Result For SR");
			return true;
		}
		return false;
	}
	
	@Override
	public void setUpdatedBy(String updatedBy) {
		
	}
	
	@Override
	public void setUpdatedDate(Date updatedDate) {
		
	}

    @Override
    public String toString() {
        return "Article{" +
                "name='" + name + '\'' +
                '}';
    }
}
