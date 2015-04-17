package models;

import common.thread.ThreadLocalOverride;
import common.utils.NanoSecondStopWatch;
import common.utils.StringUtil;
import indexing.CommentIndex;
import indexing.PostIndex;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
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
    private static final play.api.Logger logger = play.api.Logger.apply(Post.class);

    public String title;
       
    @Required
    @Column(length=2000)
    public String body;
    
    @ManyToOne(cascade=CascadeType.REMOVE)
    public Community community;
    
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public Set<Comment> comments;
    
    @Required
    public PostType postType;
    
    @ManyToOne(cascade = CascadeType.REMOVE)
    public Folder folder;
    
    public int noOfComments = 0;
    public int noOfLikes = 0;
    public int noWantAns = 0;
    public int noOfViews = 0;
    public int shortBodyCount = 0;

	@ManyToOne
	public User socialUpdatedBy;
	public Date socialUpdatedDate = new Date();

	public boolean mobile = false;
	public boolean android = false;
	public boolean ios = false;
	
    /**
     * Ctor
     */
    public Post() {}

    /**
     * Ctor
     * @param actor
     * @param title
     * @param post
     * @param community
     */
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
    public void onLikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][c="+community.id+"][p="+this.id+"] Post onLikedBy");
        }

        recordLike(user);
        this.noOfLikes++;
        user.likesCount++;
        
        // update affinity
        UserCommunityAffinity.onCommunityActivity(user.id, getCommunity().id);
    }
    
    @Override
    public void onUnlikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][c="+community.id+"][p="+this.id+"] Post onUnlikedBy");
        }

        this.noOfLikes--;
        user.likesCount--;
    }

    public void onWantAnswerBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][c="+community.id+"][q="+this.id+"] Question onWantAnswerBy");
        }

        // update last socialUpdatedDate in Question
        this.socialUpdatedBy = user;
        this.socialUpdatedDate = new Date();
        this.community.socialUpdatedDate = new Date();

        recordWantAnswer(user);
        this.noWantAns++;
        user.wantAnsCount++;

        // update affinity
        UserCommunityAffinity.onCommunityActivity(user.id, getCommunity().id);
        // push to community
        FeedProcessor.pushToCommunity(this);
    }

    public void onUnwantAnswerBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][c="+community.id+"][q="+this.id+"] Question onUnwantAnswerBy");
        }

        this.noWantAns--;
        user.wantAnsCount--;
    }
    
    public void onBookmarkedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][c="+community.id+"][p="+this.id+"] Post onBookmarkedBy");
        }

        recordBookmark(user);
        // update affinity
        UserCommunityAffinity.onCommunityActivity(user.id, getCommunity().id);
    }

    @Override
    public void save() {
        super.save();
        
        if (this.socialUpdatedBy == null) {
        	this.socialUpdatedBy = this.owner;
        }
        Date override = ThreadLocalOverride.getSocialUpdatedDate();
        this.socialUpdatedDate = (override == null) ? new Date() : override;

        // push to / remove from community
        if (!this.deleted) {
            FeedProcessor.pushToCommunity(this);
            switch(this.postType) {
                case SIMPLE: {
                    recordPost(owner);
                    owner.postsCount++;
                    break;
                }
                case QUESTION: {
                    recordQnA(owner);
                    owner.questionsCount++;
                    break;
                }
            }
        } else {
            FeedProcessor.removeFromCommunity(this);
            switch(this.postType) {
                case SIMPLE: {
                    owner.postsCount--;
                    break;
                }
                case QUESTION: {
                    owner.questionsCount--;
                    break;
                }
            }
        }
    }
    
    public void delete(User deletedBy) {
        for (Comment comment : this.comments) {
            comment.deleted = true;
            comment.deletedBy = deletedBy;
            comment.save();
        }
        this.deleted = true;
        this.deletedBy = deletedBy;
        save();

        // Community statistics
        CommunityStatistics.onDeletePost(community.id, community.targetingType);
        // Game statistics
        GameAccountStatistics.recordDeletePost(deletedBy.id);
    }

    @Override
    public SocialObject onComment(User user, String body, CommentType type)
            throws SocialObjectNotCommentableException {
        // update last socialUpdatedDate in Post
    	this.socialUpdatedBy = user;
        Date override = ThreadLocalOverride.getSocialUpdatedDate();
        this.socialUpdatedDate = (override == null) ? new Date() : override;
        if (override == null) {
            this.community.socialUpdatedDate = new Date();
        } else if (override.getTime() > this.community.socialUpdatedDate.getTime()) {
            this.community.socialUpdatedDate = override;
        }

        // create Comment object
        Comment comment = new Comment(this, user, body);
        comment.commentType = type;
        if(this.objectType == SocialObjectType.POST) {
            comment.objectType = SocialObjectType.COMMENT;
        }
        if(this.objectType == SocialObjectType.QUESTION) {
            comment.objectType = SocialObjectType.ANSWER;
        }
        comment.save();

        // merge into Post
        if (comments == null) {
            comments = new HashSet<>();
        }
        this.comments.add(comment);
        this.noOfComments++;
        JPA.em().merge(this);

        // record for notifications
        if (type == CommentType.ANSWER) {
            recordAnswerOnCommunityPost(user, comment);
        }
        if (type == CommentType.SIMPLE) {
            recordCommentOnCommunityPost(user, comment);
        }

        // update community stats
        CommunityStatistics.onNewComment(this.community.id);
        // update affinity
        UserCommunityAffinity.onCommunityActivity(user.id, getCommunity().id);
        // push to community NF
        FeedProcessor.pushToCommunity(this);
        
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
            if (logger.underlyingLogger().isDebugEnabled()) {
                logger.underlyingLogger().debug("[ElasticSearch] onComment index took "+sw.getElapsedMS()+"ms");
            }
        } catch(Exception e) {
            logger.underlyingLogger().error("Error in onComment() - Elastic search index", e);
        }

        return comment;
    }

    public SocialObject onCommentPkView(User user, String body, String attribute)
        throws SocialObjectNotCommentableException {
        // update last socialUpdatedDate in Post
    	this.socialUpdatedBy = user;
        this.socialUpdatedDate = new Date();

        // create Comment object
        Comment comment = new Comment(this, user, body);
        comment.commentType = CommentType.VIEW;
        comment.objectType = SocialObjectType.PK_VIEW;
        comment.setAttribute(attribute);
        comment.save();

        // merge into Post
        if (comments == null) {
            comments = new HashSet<>();
        }
        this.comments.add(comment);
        this.noOfComments++;
        JPA.em().merge(this);

        // TODO: record for notifications?

        // update affinity
        UserCommunityAffinity.onCommunityActivity(user.id, getCommunity().id);

        return comment;
    }

    @Override
    public void onDeleteComment(User user, String body, CommentType type) 
            throws SocialObjectNotCommentableException {
        // TODO delete comment logic
        this.noOfComments--;
    }
    
    public void indexPost(boolean withPhotos) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        PostIndex postIndex = new PostIndex();
        postIndex.post_id = this.id;
        postIndex.community_id = this.community.id;
        postIndex.owner_id = this.owner.id;
        postIndex.description = this.body;
        postIndex.postedBy = (this.owner.displayName != null) ?  this.owner.displayName : "NA";
        postIndex.postedOn = this.getCreatedDate();
    
        if(withPhotos){
            postIndex.hasImages = true;
            postIndex.folder_id = this.folder.id;
        }
        
        postIndex.index();

        sw.stop();

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[ElasticSearch] indexPost took "+sw.getElapsedMS()+"ms");
        }
    }

    @JsonIgnore
    public long getNumCommentsOfPost() {
        Query q = JPA.em().createQuery("Select count(c.id) from Comment c where socialObject=?1 and deleted = false");
        q.setParameter(1, this.id);
        return (Long) q.getSingleResult();
    }

    @JsonIgnore
    public Set<Long> getCommentUserIdsOfPost() {
        Query q = JPA.em().createNativeQuery("Select c.owner_id from Comment c where socialObject=?1 and deleted = 0");
        q.setParameter(1, this.id);
        List<BigInteger> qRets = (List<BigInteger>) q.getResultList();
        Set<Long> ret = new HashSet<>();
        for (BigInteger qRet : qRets) {
            ret.add(qRet.longValue());
        }
        return ret;
    }

    @JsonIgnore
    public List<Comment> getCommentsOfPost() {
        Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 and deleted = false order by date");
        q.setParameter(1, this.id);
        return (List<Comment>)q.getResultList();
    }
    
    @JsonIgnore
    public List<Comment> getCommentsOfPost(int limit) {
    	/*
    	// NOTE: order by date desc to show latest comments first on landing page
        Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 and deleted = false order by date desc" );
        */
    	Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 and deleted = false order by date" );
        q.setParameter(1, this.id);
        return (List<Comment>)q.setMaxResults(limit).getResultList();
        
    }
    
    @JsonIgnore
    public List<Comment> getCommentsOfPost(int offset, int limit) {
        Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 and deleted = false order by date" );
        q.setParameter(1, this.id);
        q.setFirstResult(offset * limit);
        q.setMaxResults(limit);
        return (List<Comment>)q.getResultList();
    }

    @JsonIgnore
    public List<Comment> getCommentsOfPostByAttribute(String attribute) {
        Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 and attribute = ?2 and deleted = false order by date desc");
        q.setParameter(1, this.id);
        q.setParameter(2, attribute);
        return (List<Comment>)q.getResultList();
    }

    @JsonIgnore
    public List<Comment> getCommentsOfPostByAttribute(int limit, String attribute) {
        Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 and attribute = ?2 and deleted = false order by date desc");
        q.setParameter(1, this.id);
        q.setParameter(2, attribute);
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
            this.folder = Folder.createAlbum(this.owner, "post-ps", "", true);
            this.merge();
        }
    }

    public String getShortenedTitle() {
        if (title == null || title.startsWith("http")) {
            return "";
        } else {
            return StringUtil.truncateWithDots(title, 12);
        }
    }

    ///////////////////// Query APIs /////////////////////
    public static Post findById(Long id) {
        try {
            Query q = JPA.em().createQuery("SELECT p FROM Post p where id = ?1 and deleted = false");
            q.setParameter(1, id);
            return (Post) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    ///////////////////// Getters /////////////////////
    public String getBody() {
        return body;
    }

    public Community getCommunity() {
        return community;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public PostType getPostType() {
        return postType;
    }

    public Folder getFolder() {
        return folder;
    }

    public Date getSocialUpdatedDate() {
        return socialUpdatedDate;
    }
}
