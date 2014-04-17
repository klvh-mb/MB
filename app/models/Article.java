package models;

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

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Article extends SocialObject implements Commentable {

	public Article() {}
	
	public String name;
	
	@Lob
	public String description;
	
	public Boolean isFeatured;
	
	public Integer targetAge;
	
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	public Set<Comment> comments;
	
	@ManyToOne
	public ArticleCategory category;
	
	public Article(String name, String description, Boolean isFeatured, Integer targetAge, ArticleCategory category) {
		this.name = name;
		this.description = description;
		this.isFeatured = isFeatured;
		this.targetAge = targetAge;
		this.category = category;
		
	}

	@Override
	public SocialObject onComment(User user, String body, CommentType type)
			throws SocialObjectNotCommentableException {
		Comment comment = new Comment(this, user, body);
		
		if (comments == null) {
			comments = new HashSet<Comment>();
		}
		if (type == CommentType.ANSWER) {
			comment.commentType = type;
			recordAnswerOnCommunityPost(user);
		}
		if (type == CommentType.SIMPLE) {
			comment.commentType = type;
			recordCommentOnCommunityPost(user);
		}
		comment.save();
		this.comments.add(comment);
		JPA.em().merge(this);
		return comment;
	}
	
	@JsonIgnore
	public List<Comment> getCommentsOfPost() {
		Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 order by date desc");
		q.setParameter(1, this);
		return (List<Comment>)q.getResultList();
	}
	
	@Transactional
	public static List<Article> getAllArticles() {
		Query q = JPA.em().createQuery("Select a from Article a order by CREATED_DATE desc");
		return (List<Article>)q.getResultList();
	}
	
	@Transactional
	public static List<Article> getEightArticles() {
		//  Select * from Article where featured = 1 limit <= 8
		Query q = JPA.em().createQuery("Select a from Article a where a.isFeatured  = ?1 order by CREATED_DATE desc");
		q.setParameter(1, true);
		q.setFirstResult(0);
		q.setMaxResults(8);
		return (List<Article>)q.getResultList();
	}
	
	public static Article findById(Long id) {
		Query q = JPA.em().createQuery("SELECT u FROM Article u where id = ?1");
		q.setParameter(1, id);
		return (Article) q.getSingleResult();
	}
	
	public static int deleteByID(Long id) {
		Query q = JPA.em().createQuery("DELETE FROM Article u where id = ?1");
		q.setParameter(1, id);
		return q.executeUpdate();
	}
	
	public Article findById() {
		// TODO
		return null;
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
