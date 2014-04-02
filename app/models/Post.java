package models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

import com.mnt.exception.SocialObjectNotCommentableException;

import domain.CommentType;
import domain.Commentable;
import domain.Likeable;
import domain.PostType;

@Entity
public class Post extends SocialObject implements Likeable, Commentable {

	public Post() {}
	
	@Required @Lob
	public String body;
	
	@ManyToOne(cascade=CascadeType.REMOVE)
	public Community community;

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	public Set<Comment> comments;

	@Required
	public PostType postType;

	public Date createdDate;
	@Override
	public void onLike(User user) {
		recordLike(user);
	}
	
	public Post(User actor, String post , Community community) {
		this.owner = actor;
		this.body = post;
		this.community = community;
	}
	
	@Override
	public void save() {
		super.save();
		recordPost(owner);
	}
	
	public static Post findById(Long id) {
		Query q = JPA.em().createQuery("SELECT p FROM Post p where id = ?1");
		q.setParameter(1, id);
		return (Post) q.getSingleResult();
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
		Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 order by date");
		q.setParameter(1, this);
		return (List<Comment>)q.getResultList();
	}

}
