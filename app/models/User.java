package models;

import java.io.IOException;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import play.db.jpa.JPA;
import play.i18n.Messages;

import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.exception.SocialObjectNotLikableException;
import com.mnt.exception.SocialObjectNotPostableException;

import domain.CommentType;
import domain.SocialObjectType;

@Entity
public class User extends SocialObject {

	public String firstName;
	public String lastName;
	public String displayName;
	public String username;
	@ManyToOne(cascade=CascadeType.REMOVE)
	public Folder albumPhotoProfile;
	
	
	public User(){
		this.objectType = SocialObjectType.USER;
	}
	
	public User(String firstName, String lastName, String displayName, String username) {
		this();
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = displayName;
		this.name = firstName;
		this.username = username;
	}
	
	
	
	public void likesOn(SocialObject target) throws SocialObjectNotLikableException {
		target.onLike(this);
	}
	
	public void postedOn(SocialObject target, String post) throws SocialObjectNotPostableException {
		target.onPost(this, post);
	}
	
	public void requestedToJoin(SocialObject target) throws SocialObjectNotJoinableException {
		target.onJoinRequest(this);
	}
	
	//TODO: Write Test
	public void commentedOn(SocialObject target, String comment) throws SocialObjectNotCommentableException {
		target.onComment(this, comment, CommentType.SIMPLE);
	}
	
	//TODO: Write Test
	public void answeredOn(SocialObject target, String comment) throws SocialObjectNotCommentableException {
		target.onComment(this, comment, CommentType.ANSWER);
	}
	
	//TODO: Write Test
	public void questionedOn(SocialObject target, String comment) throws SocialObjectNotCommentableException {
		target.onComment(this, comment, CommentType.QUESTION);
	}
	
	public void joinRequestAccepted(SocialObject target, User toBeMemeber) throws SocialObjectNotJoinableException {
		target.onJoinRequestAccepted(toBeMemeber);
	}
	
	public void markNotificationRead(Notification notification) {
		notification.markNotificationRead();
	}
	
	
	public static List<User> searchLike(String q) {
		CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from( User.class );
		criteria.select(root);
		Predicate predicate = builder.or(
				builder.like(root.<String>get("displayName"), "%"+q+"%"),
				builder.like(root.<String>get("firstName"), "%"+q+"%"),
				builder.like(root.<String>get("lastName"), "%"+q+"%"));
		criteria.where(predicate);
		return JPA.em().createQuery(criteria).getResultList();
	}
	
	public static User searchUsername(String username) {
		CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from( User.class );
		criteria.select(root);
		Predicate predicate = (builder.equal(root.get("username"), username));
		criteria.where(predicate);
		return JPA.em().createQuery(criteria).getSingleResult();
	}
	
	 public Resource setPhotoProfile(java.io.File file) throws IOException {
		 ensureAlbumPhotoProfileExist();
		 Resource newPhoto = this.albumPhotoProfile.addFile(file,SocialObjectType.PHOTO);
		 this.albumPhotoProfile.setHighPriorityFile(newPhoto);
		 newPhoto.save();
		 return newPhoto;
	 }
	 
	 /**
	   * get the photo profile
	   * @return the resource, null if not exist
	   */
	  public Resource getPhotoProfile() {
	    if(this.albumPhotoProfile != null) {
	      Resource file = this.albumPhotoProfile.getHighPriorityFile();
	      if(file != null){
	        return file;
	      }
	    }
	    return null;
	  }
	 
	 /**
	   * ensure the existence of the system folder: albumPhotoProfile
	   */
	  private void ensureAlbumPhotoProfileExist() {
		  
	    if(this.albumPhotoProfile == null) {
	      this.albumPhotoProfile = createAlbum("profile", Messages.get("album.photo-profile.description"), true);
	      this.merge();
	    }
	  }
	  
	  /**
	   * create a folder with the type: IMG (contain only image Resource types)
	   * @param name
	   * @param description
	   * @param privacy
	   * @param system
	   * @return
	   */
	  public Folder createAlbum(String name, String description, Boolean system) {
	    return createFolder(name, description, SocialObjectType.FOLDER,system);
	  }
	  
	  private Folder createFolder(String name, String description, SocialObjectType type,  Boolean system) {
		    Folder folder = new Folder();
		    folder.owner = this;
		    folder.name = name;
		    folder.description = description;
		    folder.objectType = type;
		    folder.system = system;
		    folder.save();
		    return folder;
		    
	   }
	

}
