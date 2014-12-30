package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.Commentable;
import domain.Likeable;
import domain.SocialObjectType;

@Entity
public class PKView extends SocialObject implements Commentable, Likeable {
    private static final play.api.Logger logger = play.api.Logger.apply(PKView.class);

	public String image;
	
	@Lob
	public String description;
	
	public int noOfLikes = 0;
    
	public int noOfViews = 0;
	
	@ManyToOne(cascade=CascadeType.REMOVE)
    public Community community;
    
    public PKView() {}
    
	public PKView(String name, String description, Community community) {
		this.name = name;
		this.description = description;
		this.objectType = SocialObjectType.PK_VIEW;
		this.community = community;
	}
	
	@Transactional
	public static List<PKView> getAllPKViews() {
		Query q = JPA.em().createQuery("Select p from PKView p where p.deleted = false order by CREATED_DATE desc,id desc");
		return (List<PKView>)q.getResultList();
	}
	
	public static PKView findById(Long id) {
		Query q = JPA.em().createQuery("SELECT p FROM PKView p where p.id = ?1 and p.deleted = false");
		q.setParameter(1, id);
		try {
		    return (PKView) q.getSingleResult();
		} catch(NoResultException e) {
            return null;
        }
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
        if (o != null && o instanceof PKView) {
            final PKView other = (PKView) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
	
    @Override
    public String toString() {
        return "PKView{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "views='" + noOfViews + '\'' +
                "likes='" + noOfLikes + '\'' +
                "created='" + getCreatedDate() + '\'' +
                '}';
    }
}
