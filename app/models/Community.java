package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Play;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Content;

import com.mnt.exception.SocialObjectNotJoinableException;

import domain.Joinable;
import domain.Likeable;
import domain.PostType;
import domain.Postable;
import domain.SocialObjectType;

@Entity
public class Community extends SocialObject  implements Likeable, Postable, Joinable {
	
	@OneToMany(cascade=CascadeType.REMOVE)
	public Set<Post> posts = new HashSet<Post>();

	@OneToMany(cascade = CascadeType.REMOVE)
	public Set<User> members = new HashSet<User>();

	@Enumerated(EnumType.ORDINAL)
	public CommunityType communityType = CommunityType.CLOSE;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JsonIgnore
	public Folder albumPhotoProfile;

	public static enum CommunityType {
		OPEN,
		CLOSE,
		PRIVATE
	}

	@OneToMany(cascade = CascadeType.REMOVE)
	public List<Folder> folders;
	
	

	public Community() {
		this.objectType = SocialObjectType.COMMUNITY;
	}

	public Community(String name, User owner) {
		this();
		this.name = name;
		this.owner = owner;
	}

	@Override
	public void onLike(User user) {
		recordLike(user);
	}

	@Override
	@Transactional
	public SocialObject onPost(User user, String body, PostType type) {
		Post post = new Post(user, body, this);

		if (type == PostType.QUESTION) {
			post.objectType = SocialObjectType.QUESTION;
			post.postType = type;
		}

		if (type == PostType.SIMPLE) {
			post.objectType = SocialObjectType.POST;
			post.postType = type;
		}
		post.save();
		this.posts.add(post);
		JPA.em().merge(this);
		// recordPostOn(user);
		return post;

	}
	
	public void ownerAsMember(User user)
			throws SocialObjectNotJoinableException {
		this.members.add(user);
		JPA.em().merge(this);
		beMemberForOwner(user);
	}
	
	@Override
	public void onJoinRequest(User user)
			throws SocialObjectNotJoinableException {
		if (communityType != CommunityType.OPEN) {
			recordJoinRequest(user);
		} else {
			this.members.add(user);
			JPA.em().merge(this);
		}
	}

	@Override
	@Transactional
	public void onJoinRequestAccepted(User user)
			throws SocialObjectNotJoinableException {
		this.members.add(user);
		JPA.em().merge(this);
		recordJoinRequestAccepted(user);
	}

	public static List<Community> search(String q) {
		CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Community> criteria = builder
				.createQuery(Community.class);
		Root<Community> root = criteria.from(Community.class);
		criteria.select(root);
		Predicate predicate = builder.or(builder.like(
				root.<String> get("name"), "%" + q + "%"));
		criteria.where(predicate);
		return JPA.em().createQuery(criteria).getResultList();
	}

	public Resource setCoverPhoto(File source) throws IOException {
		ensureAlbumPhotoProfileExist();
		Resource cover_photo = this.albumPhotoProfile.addFile(source,
				SocialObjectType.PHOTO);
		this.albumPhotoProfile.setHighPriorityFile(cover_photo);
		cover_photo.save();
		return cover_photo;
	}
	
	@JsonIgnore
	public Resource getPhotoProfile() {
		if (this.albumPhotoProfile != null) {
			Resource file = this.albumPhotoProfile.getHighPriorityFile();
			if (file != null) {
				return file;
			}
		}
		return null;
	}
	
	
	public String getPhotoProfileURL() {
		Resource resource = getPhotoProfile();
		if (resource == null) {
			return "";
		}
		return resource.getPath();
	}
	

	private void ensureAlbumPhotoProfileExist() {

		if (this.albumPhotoProfile == null) {
			this.albumPhotoProfile = createAlbum("profile",
					Messages.get("album.photo-profile.description"), true);
			this.merge();
		}
	}

	public Folder createAlbum(String name, String description, Boolean system) {

		if (ensureFolderExistWithGivenName(name)) {
			Folder folder = createFolder(name, description,
					SocialObjectType.FOLDER, system);
			folders.add(folder);
			this.merge(); // Add folder to existing User as new albumn
			return folder;
		}
		return null;
	}

	private Folder createFolder(String name, String description,
			SocialObjectType type, Boolean system) {

		Folder folder = new Folder(name);
		folder.owner = this;
		folder.name = name;
		folder.description = description;
		folder.objectType = type;
		folder.system = system;
		folder.save();
		return folder;
	}

	private boolean ensureFolderExistWithGivenName(String name) {

		if (folders != null && folders.contains(new Folder(name))) {
			return false;
		}

		folders = new ArrayList<>();
		return true;
	}
	
	public static Community findById(Long id) {
		Query q = JPA.em().createQuery("SELECT u FROM Community u where id = ?1");
		q.setParameter(1, id);
		return (Community) q.getSingleResult();
	}

	public File getDefaultThumbnailCoverPhoto()  throws FileNotFoundException {
		return new File(Play.application().configuration().getString("storage.community.cover.thumbnail.noimage"));
	}
	
	public File getDefaultCoverPhoto()  throws FileNotFoundException {
		 return new File(Play.application().configuration().getString("storage.community.cover.noimage"));
	}
}
