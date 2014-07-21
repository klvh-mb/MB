package models;

import common.utils.NanoSecondStopWatch;
import indexing.CommentIndex;
import indexing.PostIndex;

import java.io.File;
import java.io.IOException;
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
    private static final play.api.Logger logger = play.api.Logger.apply(Post.class);

    public Post() {}
    
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
    
    public int noOfLikes = 0;

	public Date socialUpdatedDate = new Date();
    
    @Override
    public void onLikedBy(User user) {
        recordLike(user);
        this.noOfLikes++;
        user.likesCount++;
        
        // update affinity
        UserCommunityAffinity.onCommunityActivity(user.id, getCommunity().id);
    }
    
    @Override
    public void onUnlikedBy(User user) {
        this.noOfLikes--;
        user.likesCount--;
    }
    
    public void onBookmarkedBy(User user) {
        recordBookmark(user);
        // update affinity
        UserCommunityAffinity.onCommunityActivity(user.id, getCommunity().id);
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
        this.socialUpdatedDate = new Date();

        // push to community
        FeedProcessor.pushToCommunity(this);
        
        if (this.postType == PostType.SIMPLE) {
            owner.postsCount++;
        } else if (this.postType == PostType.QUESTION) {
            owner.questionsCount++;
        }
    }
    
    public static Post findById(Long id) {
        try {
            Query q = JPA.em().createQuery("SELECT p FROM Post p where id = ?1 and deleted = false");
            q.setParameter(1, id);
            return (Post) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    public static int deleteById(Long id) {
        Query q = JPA.em().createQuery("Update Post p set deleted = true where id = ?1");
        q.setParameter(1, id);
        return q.executeUpdate();
    }
    
    @Override
    public SocialObject onComment(User user, String body, CommentType type)
            throws SocialObjectNotCommentableException {
        // update last socialUpdatedDate in Post
        this.socialUpdatedDate = new Date();

        Comment comment = new Comment(this, user, body);
        
        if (comments == null) {
            comments = new HashSet<Comment>();
        }
        if (type == CommentType.ANSWER) {
            comment.commentType = type;
            recordAnswerOnCommunityPost(user);
            // update affinity
            UserCommunityAffinity.onCommunityActivity(user.id, getCommunity().id);
            // push to community
            FeedProcessor.pushToCommunity(this);
        }
        if (type == CommentType.SIMPLE) {
            comment.commentType = type;
            recordCommentOnCommunityPost(user);
            // update affinity
            UserCommunityAffinity.onCommunityActivity(user.id, getCommunity().id);
            // push to community
            FeedProcessor.pushToCommunity(this);
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

            if (logger.underlyingLogger().isDebugEnabled()) {
                logger.underlyingLogger().debug("[ElasticSearch] onComment index took "+sw.getElapsedMS()+"ms");
            }
        } catch(Exception e) {
            logger.underlyingLogger().error("Error in onComment() - Elastic search index", e);
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
    public List<Comment> getCommentsOfPost() {
        Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 and deleted = false");
        q.setParameter(1, this.id);
        return (List<Comment>)q.getResultList();
    }
    
    @JsonIgnore
    public List<Comment> getCommentsOfPost(int limit) {
        Query q = JPA.em().createQuery("Select c from Comment c where socialObject=?1 and deleted = false order by date desc" );
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

    public Date getSocialUpdatedDate() {
        return socialUpdatedDate;
    }

    public void setSocialUpdatedDate(Date socialUpdatedDate) {
        this.socialUpdatedDate = socialUpdatedDate;
    }

    public boolean isLikedBy(User user) {
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
        SecondarySocialRelation sr = null;
        try {
            sr = (SecondarySocialRelation)q.getSingleResult();
        }
        catch(NoResultException nre) {
            return false;
        }
        return true;
    }
}
