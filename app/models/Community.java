package models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
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

import common.image.FaceFinder;
import common.utils.ImageFileUtil;

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
public class Community extends TargetingSocialObject implements Likeable, Postable, Joinable, Comparable<Community> {
    private static play.api.Logger logger = play.api.Logger.apply(Community.class);
    
    private static final String STORAGE_COMMUNITY_COVER_THUMBNAIL_NOIMAGE = 
            Play.application().configuration().getString("storage.community.cover.thumbnail.noimage");

    private static final String STORAGE_COMMUNITY_COVER_MINI_NOIMAGE = 
            Play.application().configuration().getString("storage.community.cover.mini.noimage");

    private static final String STORAGE_COMMUNITY_COVER_NOIMAGE = 
            Play.application().configuration().getString("storage.community.cover.noimage");
    
    public static enum CommunityType {
		OPEN,
		CLOSE,
		PRIVATE
	}

	@OneToMany(cascade=CascadeType.REMOVE, fetch = FetchType.LAZY)
	public List<Post> posts = new ArrayList<Post>();
	
	@Enumerated(EnumType.ORDINAL)
	public CommunityType communityType;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JsonIgnore
	public Folder albumPhotoProfile;
	
	@Column(length=2000)
	public String description;
	
	public String tagetDistrict;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date createDate;

	public boolean adminPostOnly = false;
	
	public boolean excludeFromNewsfeed = false;

	@OneToMany(cascade = CascadeType.REMOVE)
	public List<Folder> folders;
	
	public String icon;

	public Community() {
		this.objectType = SocialObjectType.COMMUNITY;
	}
	
	public Community(String name, String description, User owner, CommunityType type) {
		this();
		this.name = name;
		this.owner = owner;
		this.description = description;
		this.communityType =type;
	}
	
	@Override
	@Transactional
	public SocialObject onPost(User user, String title, String body, PostType type) {
		Post post = null;
		
		if (type == PostType.QUESTION) {
		    post = new Post(user, title, body, this);
			post.objectType = SocialObjectType.QUESTION;
			post.postType = type;
			post.setUpdatedDate(new Date());
		} else if (type == PostType.SIMPLE) {
		    post = new Post(user, body, this);
			post.objectType = SocialObjectType.POST;
			post.postType = type;
			post.setUpdatedDate(new Date());
		} else {
		    throw new RuntimeException("Post type is not recognized");
		}
		post.save();
		
		JPA.em().merge(this);

        // record affinity
        UserCommunityAffinity.onCommunityActivity(user.id, this.id);

		//recordPostOn(user);
		return post;
	}
	
	@Transactional
	public void ownerAsMember(User user)
			throws SocialObjectNotJoinableException {
		ownerMemberOfCommunity(user);
	}
	
	@Override
	public void onJoinRequest(User user)
			throws SocialObjectNotJoinableException {
		if (communityType != CommunityType.OPEN) {
			recordJoinRequest(user);
		} else{
			beMemberOfOpenCommunity(user);
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
		List<User> members = new ArrayList<>();
		for (Long memId : getMemberIds()) {
			members.add(User.findById(memId));
		}
		return members;
	}

    @JsonIgnore
    public List<Long> getMemberIds() {
        Query query = JPA.em().createNativeQuery(
            "select sr.actor from SocialRelation sr where sr.target = ?1 and sr.action = ?2"
        );
        query.setParameter(1, this.id);
        query.setParameter(2, SocialRelation.Action.MEMBER.name());

        List<BigInteger> memIds = query.getResultList();

        List<Long> result = new ArrayList<>();
		for (BigInteger memId : memIds) {
			result.add(memId.longValue());
		}
		return result;
    }
	
	public static List<Community> search(String q) {
		CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Community> criteria = builder
				.createQuery(Community.class);
		Root<Community> root = criteria.from(Community.class);
		criteria.select(root);
		Predicate predicate = 
		        builder.and(builder.or(
		                builder.like(root.<String> get("name"), "%" + q + "%")),
		                builder.equal(root.get("deleted"), false));
		criteria.where(predicate);
		return JPA.em().createQuery(criteria).getResultList();
	}

	public Resource setCoverPhoto(File source) throws IOException {
		ensureAlbumPhotoProfileExist();

        // Pre-process file to have face centered.
        BufferedImage croppedImage = FaceFinder.getRectPictureWithFace(source, 2.29d);
        ImageFileUtil.writeFileWithImage(source, croppedImage);

		Resource cover_photo = this.albumPhotoProfile.addFile(source,
				SocialObjectType.COVER_PHOTO);
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
		Query q = JPA.em().createQuery("SELECT c FROM Community c where id = ?1 and deleted = false");
		q.setParameter(1, id);
		Object o = q.getSingleResult();
		return o == null? null : (Community)o;
	}

	public static List<Community> findByTargetingType(TargetingSocialObject.TargetingType targetingType) {
        Query q = JPA.em().createQuery("SELECT c FROM Community c where system = ?1 and targetingType = ?2 and deleted = false");
        q.setParameter(1, true);
        q.setParameter(2, targetingType);
        try {
            return (List<Community>)q.getResultList();
        } catch (NoResultException e) {
        }
        return null;
    }
	
	public static Community findByTargetingTypeTargetingInfo(
	        TargetingSocialObject.TargetingType targetingType, String targetingInfo) {
	    Query q = JPA.em().createQuery("SELECT c FROM Community c where system = ?1 and targetingType = ?2 and targetingInfo = ?3 and deleted = false");
	    q.setParameter(1, true);
        q.setParameter(2, targetingType);
        q.setParameter(3, targetingInfo);
        try {
            return (Community)q.getSingleResult();
        } catch (NoResultException e) {
        }
        return null;
	}
	
	public File getDefaultThumbnailCoverPhoto()  throws FileNotFoundException {
		return new File(STORAGE_COMMUNITY_COVER_THUMBNAIL_NOIMAGE);
	}
	
	public File getDefaultMiniCoverPhoto()  throws FileNotFoundException {
		return new File(STORAGE_COMMUNITY_COVER_MINI_NOIMAGE);
	}
	
	public File getDefaultCoverPhoto()  throws FileNotFoundException {
		 return new File(STORAGE_COMMUNITY_COVER_NOIMAGE);
	}
	
	public boolean checkCommunityNameExists() {
		Query q = JPA.em().createQuery("Select c from Community c where name = ?1 and deleted = false");
		q.setParameter(1, this.name);
		//q.setParameter(2, SocialObjectType.COMMUNITY);
	
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
		Query q = JPA.em().createQuery("Select p from Post p where community=?1 and postType=1 and deleted = false order by p.socialUpdatedDate desc");
		q.setParameter(1, this);
		q.setFirstResult(offset);
		q.setMaxResults(limit);
		return (List<Post>)q.getResultList();
	}
	
	@JsonIgnore
	public List<Post> getQuestionsOfCommunity(int offset, int limit) {
		Query q = JPA.em().createQuery("Select p from Post p where community=?1 and postType=0 and deleted = false order by p.socialUpdatedDate desc");
		q.setParameter(1, this);
		q.setFirstResult(offset);
		q.setMaxResults(limit);
		return (List<Post>)q.getResultList();
	}
	
	@JsonIgnore
	public List<User> getNonMembersOfCommunity(String query) {
		Query q = JPA.em().createQuery("Select u from User u where lower(u.displayName) LIKE '%"+ query.toLowerCase() +"%' AND " +
				"u.id not in (select sr.actor from " +
				"SocialRelation sr where sr.target = ?1 and (sr.action = 'MEMBER' OR sr.actionType = 'INVITE_REQUESTED')) and u.deleted = false");
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

    public boolean isExcludeFromNewsfeed() {
        return excludeFromNewsfeed;
    }

    public void setExcludeFromNewsfeed(boolean excludeFromNewsfeed) {
        this.excludeFromNewsfeed = excludeFromNewsfeed;
    }
    
    @Override
    public int compareTo(Community o) {
        if (this.system != o.system) {
            return o.system.compareTo(this.system);     // system communities on top
        }
        if (this.targetingType != o.targetingType) {
            return this.targetingType.compareTo(o.targetingType);
        }
        if (this.communityType != o.communityType) {
            return this.communityType.compareTo(o.communityType);
        }
        return this.name.compareTo(o.name);
    }
}
