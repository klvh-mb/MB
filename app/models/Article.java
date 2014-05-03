package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.mnt.exception.SocialObjectNotCommentableException;

import domain.CommentType;
import domain.Commentable;

import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Article extends SocialObject implements Commentable {

	public Article() {}
	
	
	
	@Lob
	public String description;
	
	public Boolean isFeatured;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date publishedDate;
	
	public Integer targetAge;
	
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	public List<Comment> comments;
	
	@ManyToOne
	public ArticleCategory category;
	
	public Article(String name, String description, Boolean isFeatured, Integer targetAge, ArticleCategory category) {
		this.name = name;
		this.description = description;
		this.isFeatured = isFeatured;
		this.targetAge = targetAge;
		this.category = category;
		this.publishedDate = new Date();
		
	}

	@Override
	public SocialObject onComment(User user, String body, CommentType type)
			throws SocialObjectNotCommentableException {
		Comment comment = new Comment(this, user, body);
		
		if (comments == null) {
			comments = new ArrayList<Comment>();
		}
	
		comment.commentType = type;
		comment.save();
		this.comments.add(comment);
		JPA.em().merge(this);
		return comment;
	}
	
	@JsonIgnore
	public List<Comment> getCommentsOfPost() {
		return (comments);
	}
	
	@Transactional
	public static List<Article> getAllArticles() {
		Query q = JPA.em().createQuery("Select a from Article a order by publishedDate DESC");
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
	public static List<Article> getEightArticles() {
		//  Select * from Article where featured = 1 limit <= 8
		Query q = JPA.em().createQuery("Select a from Article a where a.isFeatured  = ?1 order by publishedDate desc");
		q.setParameter(1, true);
		q.setFirstResult(0);
		q.setMaxResults(8);
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
	

	public void updateById()
	{
		this.merge();
	}
	public void saveArticle()
	{
		this.save();
	}
	
}
