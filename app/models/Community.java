package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Play;
import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.i18n.Messages;

import com.mnt.exception.SocialObjectNotJoinableException;

import domain.Joinable;
import domain.Likeable;
import domain.PostType;
import domain.Postable;
import domain.SocialObjectType;

@Entity
public class Community extends SocialObject  implements Likeable, Postable, Joinable {
	
	@OneToMany(cascade=CascadeType.REMOVE, fetch = FetchType.LAZY)
	public Set<Post> posts = new HashSet<Post>();
	
	@Enumerated(EnumType.ORDINAL)
	public CommunityType communityType;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JsonIgnore
	public Folder albumPhotoProfile;
	
	public String description;
	
	public String tagetDistrict;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date createDate;

	public static enum CommunityType {
		OPEN,
		CLOSE,
		PRIVATE
	}

	@OneToMany(cascade = CascadeType.REMOVE)
	public List<Folder> folders;
	
	public Community(){
		this.objectType = SocialObjectType.COMMUNITY;
	}
	
	public Community(String name, String description, User owner) {
		this();
		this.name = name;
		this.owner = owner;
		this.description = description;
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
		//recordPostOn(user);
		return post;
		
	}
	
	@Transactional
	public void ownerAsMember(User user)
			throws SocialObjectNotJoinableException {
		beMemberOfCommunity(user);
	}
	
	@Override
	public void onJoinRequest(User user)
			throws SocialObjectNotJoinableException {
		if (communityType != CommunityType.OPEN) {
			recordJoinRequest(user);
		} 
		else{
			beMemberOfCommunity(user);
		}
	}

	@Override
	@Transactional
	public void onJoinRequestAccepted(User user)
			throws SocialObjectNotJoinableException {
		recordJoinRequestAccepted(user);
	}
	
	@JsonIgnore
	public List<User> getMembers() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<SocialRelation> q = cb.createQuery(SocialRelation.class);
		Root<SocialRelation> c = q.from(SocialRelation.class);
		q.select(c);
		q.where(cb.and(cb.equal(c.get("target"), this)),
				cb.equal(c.get("action"), SocialRelation.Action.MEMBER));

		List<SocialRelation> result = JPA.em().createQuery(q).getResultList();
		List<User> Members = new ArrayList<>();
		for (SocialRelation rslt : result) {
			Members.add(User.findById(rslt.actor.id));
		}
		return Members;
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

		//if (ensureFolderExistWithGivenName(name)) {
			Folder folder = createFolder(name, description,
					SocialObjectType.FOLDER, system);
			//folders.add(folder);
			this.merge(); // Add folder to existing User as new albumn
			return folder;
		//}
		//return null;
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

	private boolean ensureFolderExistWithGivenName(String name) {

		if (folders != null && folders.contains(new Folder(name))) {
			return false;
		}

		folders = new ArrayList<>();
		return true;
	}
	
	public static Community findById(Long id) {
		Query q = JPA.em().createQuery("SELECT c FROM Community c where id = ?1");
		q.setParameter(1, id);
		return (Community) q.getSingleResult();
	}

	public File getDefaultThumbnailCoverPhoto()  throws FileNotFoundException {
		return new File(Play.application().configuration().getString("storage.community.cover.thumbnail.noimage"));
	}
	
	public File getDefaultMiniCoverPhoto()  throws FileNotFoundException {
		return new File(Play.application().configuration().getString("storage.community.cover.mini.noimage"));
	}
	
	public File getDefaultCoverPhoto()  throws FileNotFoundException {
		 return new File(Play.application().configuration().getString("storage.community.cover.noimage"));
	}
	
	public boolean checkCommunityNameExists(User owner) {
		Query q = JPA.em().createQuery("Select so from SocialObject so where owner = ?1 and objectType = ?2 and name= ?3");
		q.setParameter(1, owner);
		q.setParameter(2, SocialObjectType.COMMUNITY);
		q.setParameter(3, this.name);
		
		Community community = null;
		try {
			community = (Community) q.getSingleResult();
		}
		catch(NoResultException nre) {
		}
		
		return (community == null);
	}
	
	@JsonIgnore
	public List<Post> getPostsOfCommunity(int offset, int limit) {
		Query q = JPA.em().createQuery("Select p from Post p where community=?1 order by createdDate desc");
		q.setParameter(1, this);
		q.setFirstResult(offset);
		q.setMaxResults(limit);
		return (List<Post>)q.getResultList();
	}
}
