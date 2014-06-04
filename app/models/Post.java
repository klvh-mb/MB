package models;

import common.utils.NanoSecondStopWatch;
import indexing.CommentIndex;
import indexing.PostIndex;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import models.SocialRelation.Action;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.i18n.Messages;
import processor.FeedProcessor;

import com.github.cleverage.elasticsearch.IndexQuery;
import com.github.cleverage.elasticsearch.IndexResults;
import com.mnt.exception.SocialObjectNotCommentableException;

import domain.CommentType;
import domain.Commentable;
import domain.Likeable;
import domain.PostType;
import domain.SocialObjectType;

@Entity
public class Post extends SocialObject implements Likeable, Commentable {

    public Post() {}
    
    public String title;
       
    @Required @Lob
    public String body;
    
    @ManyToOne(cascade=CascadeType.REMOVE)
    public Community community;
    
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public Set<Comment> comments;
    
    @Required
    public PostType postType;
    
    @ManyToOne(cascade = CascadeType.REMOVE)
    
    public Folder folder;
    
    public int noOfLikes=0;
	
    @Override
    public void onLikedBy(User user) {
    	recordLike(user);
    }
    
    public void onBookmarkedBy(User user) {
    	recordBookmark(user);
    }
    
    
    public Post(User actor, String title, String post, Community community) {
        this.owner = actor;
        this.title = title;
        this.body = post;
        this.community = community;
    }
       
    public Post(User actor, String post, Community community) {
        this(actor, null, post, community);
    }
	
	@Override
	public void save() {
		super.save();
		recordPost(owner);
		FeedProcessor.pushToMemebes(this);
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
		if(this.objectType == SocialObjectType.POST) {
			comment.objectType = SocialObjectType.COMMENT;
		}
		if(this.objectType == SocialObjectType.QUESTION) {
			comment.objectType = SocialObjectType.ANSWER;
		}
		
		comment.save();
		this.comments.add(comment);
		JPA.em().merge(this);
		try {
            NanoSecondStopWatch sw = new NanoSecondStopWatch();

            IndexQuery<PostIndex> indexQuery = PostIndex.find.query();
            indexQuery.setBuilder(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
                    FilterBuilders.termFilter("post_id", comment.socialObject)));
            IndexResults<PostIndex> postIndex = PostIndex.find.search(indexQuery);

            CommentIndex commentIndex = new CommentIndex();
            commentIndex.post_id = comment.socialObject;
            commentIndex.comment_id = comment.id;
            commentIndex.commentText = comment.body;
            commentIndex.creationDate = comment.date.getTime();
            commentIndex.name = comment.name;
            commentIndex.owner_id = comment.owner.id;
            commentIndex.index();

            //hard-coding
            if(postIndex.getResults().size() > 0) {
                PostIndex pi = postIndex.getResults().get(0);
                pi.noOfComments = Post.findById(comment.socialObject).comments.size();
                pi.comments.add(commentIndex);
                pi.index();
            }

            sw.stop();
            System.out.println("[ElasticSearch] onComment index took "+sw.getElapsedMS()+"ms");
		} catch(Exception e) {
			// Ideally code should not land here. this will happen in case of data inconsistency
			System.out.println("Ideally code should not land here. this will happen in case of data inconsistency " + e.getMessage());
		}
		
		//PostIndex.find.search(indexQuery);
		return comment;
	}
	
	public void indexPost(boolean withPhotos) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		PostIndex postIndex = new PostIndex();
		postIndex.post_id = this.id;
		postIndex.community_id = this.community.id;
		postIndex.owner_id = this.owner.id;
		postIndex.description = this.body;
		postIndex.postedBy = (this.owner.name != null) ?  this.owner.name : "No Name";
		postIndex.postedOn = this.getCreatedDate();
	
		if(withPhotos){
			postIndex.hasImages = true;
			postIndex.folder_id = this.folder.id;
		}
		
		postIndex.index();

        sw.stop();
        System.out.println("[ElasticSearch] indexPost took "+sw.getElapsedMS()+"ms");
	}
	
	@JsonIgnore
	public List<Comment> getCommentsOfPost() {
		Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 ");
		q.setParameter(1, this.id);
		return (List<Comment>)q.getResultList();
	}
	
	@JsonIgnore
	public List<Comment> getCommentsOfPost(int limit) {
		Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 order by date desc" );
		q.setParameter(1, this.id);
		return (List<Comment>)q.setMaxResults(limit).getResultList();
	}
	
	public Resource addPostPhoto(File source) throws IOException {
		ensureAlbumExist();
		Resource cover_photo = this.folder.addFile(source,
				SocialObjectType.POST_PHOTO);
		cover_photo.save();
		return cover_photo;
	}
	
	public void ensureAlbumExist() {

		if (this.folder == null) {
			this.folder = createAlbum("post-photos",
					Messages.get("post-photos.photo-profile.description"), true);
			this.merge();
		}
	}
	
	private Folder createAlbum(String name, String description, Boolean system) {
			Folder folder = createFolder(name, description,
					SocialObjectType.FOLDER, system);
			return folder;
	}
	
	private Folder createFolder(String name, String description,
			SocialObjectType type, Boolean system) {

		Folder folder = new Folder(name);
		folder.owner = this.owner;
		folder.name = name;
		folder.description = description;
		folder.objectType = type;
		folder.system = system;
		folder.save();
		return folder;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public PostType getPostType() {
		return postType;
	}

	public void setPostType(PostType postType) {
		this.postType = postType;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	public boolean isLikedBy(User user) {
		Query q = JPA.em().createQuery("Select sr from SocialRelation sr where sr.action=?1 and sr.actor=?2 " +
				"and sr.target=?3 and sr.targetType=?4");
		q.setParameter(1, Action.LIKED);
		q.setParameter(2, user.id);
		q.setParameter(3, this.id);
		q.setParameter(4, this.objectType);
		SocialRelation sr = null;
		try {
			sr = (SocialRelation)q.getSingleResult();
		}
		catch(NoResultException nre) {
			System.out.println("No Result For SR");
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
		SecondarySocialRelation sr = null;
		try {
			sr = (SecondarySocialRelation)q.getSingleResult();
		}
		catch(NoResultException nre) {
			System.out.println("No Result For SR");
			return false;
		}
		return true;
	}
	

}
