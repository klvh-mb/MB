package models;

import indexing.PostIndex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
	public List<Post> posts = new ArrayList<Post>();
	
	@Enumerated(EnumType.ORDINAL)
	public CommunityType communityType;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JsonIgnore
	public Folder albumPhotoProfile;
	
	@Column(length=8192)
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
	
	public String iconName;

	public Community(){
		this.objectType = SocialObjectType.COMMUNITY;
	}
	
	public Community(String name, String description, User owner,CommunityType type) {
		this();
		this.name = name;
		this.owner = owner;
		this.description = description;
		this.communityType =type;
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
		
		JPA.em().merge(this);
		
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
		
		PostIndex postIndex = new PostIndex();
		postIndex.post_id = post.id;
		postIndex.community_id = post.community.id;
		postIndex.owner_id = post.owner.id;
		postIndex.description = post.body;
		postIndex.postedBy = (post.owner.name != null) ?  post.owner.name : "No Name";
		postIndex.postedOn = post.getCreatedDate();
		postIndex.index();
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
	
	@Override
	@Transactional
	public void onInviteRequestAccepted(User user)
			throws SocialObjectNotJoinableException {
		recordInviteRequestAccepted(user);
	}
	
	@JsonIgnore
	public List<User> getMembers() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<SocialRelation> q = cb.createQuery(SocialRelation.class);
		Root<SocialRelation> c = q.from(SocialRelation.class);
		q.select(c);
		q.where(cb.and(cb.equal(c.get("target"), this.id)),
				cb.equal(c.get("action"), SocialRelation.Action.MEMBER));

		List<SocialRelation> result = JPA.em().createQuery(q).getResultList();
		List<User> Members = new ArrayList<>();
		for (SocialRelation rslt : result) {
			Members.add(User.findById(rslt.actor));
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
		Query q = JPA.em().createQuery("Select so from Community so where owner = ?1  and name= ?2");
		q.setParameter(1, owner);
		//q.setParameter(2, SocialObjectType.COMMUNITY);
		q.setParameter(2, this.name);
	
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
		Query q = JPA.em().createQuery("Select p from Post p where community=?1 and postType= 1 order by p.auditFields.createdDate desc");
		q.setParameter(1, this);
		q.setFirstResult(offset);
		q.setMaxResults(limit);
		return (List<Post>)q.getResultList();
	}
	
	@JsonIgnore
	public List<Post> getQuestionsOfCommunity(int offset, int limit) {
		Query q = JPA.em().createQuery("Select p from Post p where community=?1 and postType= 0 order by p.auditFields.createdDate desc");
		q.setParameter(1, this);
		q.setFirstResult(offset);
		q.setMaxResults(limit);
		return (List<Post>)q.getResultList();
	}
	
	@JsonIgnore
	public List<User> getNonMembersOfCommunity(String query) {
		Query q = JPA.em().createQuery("Select u from User u where lower(u.displayName) LIKE '%"+ query.toLowerCase() +"%' AND " +
				"u.id not in (select sr.actor from " +
				"SocialRelation sr where sr.target = ?1 and (sr.action = 'MEMBER' OR sr.actionType = 'INVITE_REQUESTED'))");
		q.setParameter(1, this.id);
		return (List<User>)q.getResultList();
	}
	

	public void sendInviteToJoin(User invitee)
			throws SocialObjectNotJoinableException {
		recordInviteRequestByCommunity(invitee);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public CommunityType getCommunityType() {
		return communityType;
	}

	public void setCommunityType(CommunityType communityType) {
		this.communityType = communityType;
	}

	public Folder getAlbumPhotoProfile() {
		return albumPhotoProfile;
	}

	public void setAlbumPhotoProfile(Folder albumPhotoProfile) {
		this.albumPhotoProfile = albumPhotoProfile;
	}

	public String getTagetDistrict() {
		return tagetDistrict;
	}

	public void setTagetDistrict(String tagetDistrict) {
		this.tagetDistrict = tagetDistrict;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public List<Folder> getFolders() {
		return folders;
	}

	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
}
