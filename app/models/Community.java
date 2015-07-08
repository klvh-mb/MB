package models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import common.cache.CommunityMetaCache;
import common.collection.Pair;
import common.image.FaceFinder;
import common.utils.ImageFileUtil;
import common.utils.StringUtil;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.LocalDate;

import play.Play;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import com.mnt.exception.SocialObjectNotJoinableException;

import domain.Joinable;
import domain.Likeable;
import domain.PostSubType;
import domain.PostType;
import domain.Postable;
import domain.SocialObjectType;
import targeting.community.PNCommTargetingEngine;

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
		BUSINESS
	}

	@OneToMany(cascade=CascadeType.REMOVE, fetch = FetchType.LAZY)
	public List<Post> posts = new ArrayList<Post>();

    @ManyToMany
    public List<CommunityCategory> communityCategories;

	@Enumerated(EnumType.ORDINAL)
	public CommunityType communityType;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JsonIgnore
	public Folder albumPhotoProfile;
	
	@Column(length=2000)
	public String description;
	
    public Date socialUpdatedDate = new Date();

	public boolean adminPostOnly = false;
	
	public boolean excludeFromNewsfeed = false;

	@OneToMany(cascade = CascadeType.REMOVE)
	public List<Folder> folders;
	
	public String icon;

	public boolean promoted = false;


    /**
     * Ctor
     */
	public Community() {
		this.objectType = SocialObjectType.COMMUNITY;
	}

    /**
     * Ctor
     * @param name
     * @param description
     * @param owner
     * @param type
     */
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
		Post post;
		if (type == PostType.QUESTION) {
			post = new Post(user, title, body, this);
			post.objectType = SocialObjectType.QUESTION;
			post.postType = type;
            post.postSubType = resolvePostSubType(post);
			post.setUpdatedDate(new Date());
		}
        else if (type == PostType.SIMPLE) {
		    post = new Post(user, body, this);
			post.objectType = SocialObjectType.POST;
			post.postType = type;
			post.postSubType = PostSubType.COMMUNITY;
			post.setUpdatedDate(new Date());
		}
        else {
		    throw new RuntimeException("Invalid PostType");
		}
		post.save();

        this.socialUpdatedDate = new Date();
		JPA.em().merge(this);

        // update community stats
        CommunityStatistics.onNewPost(this.id, this.targetingType);
        // record affinity
        UserCommunityAffinity.onCommunityActivity(user.id, this.id);

		return post;
	}

    private static PostSubType resolvePostSubType(Post post) {
        Long pnId = CommunityMetaCache.getPNIdFromCommunity(post.community.id);
        Long kgId = CommunityMetaCache.getKGIdFromCommunity(post.community.id);
        if (pnId != null && kgId != null)
            return PostSubType.PN_KG;
        else if (pnId != null)
            return PostSubType.PN;
        else if (kgId != null)
            return PostSubType.KG;
        else
            return PostSubType.COMMUNITY;
    }

    /**
     * Post process on feed queue updated.
     */
    public void onFeedQueueUpdated() {
        if (targetingType != null) {
            if (targetingType == TargetingType.PRE_NURSERY) {
                PNCommTargetingEngine.indexPNNewsfeed();
            }
            else if (targetingType == TargetingType.KINDY) {
                PNCommTargetingEngine.indexKGNewsfeed();
            }
        }
    }

	@Transactional
	public void ownerAsMember(User user)
			throws SocialObjectNotJoinableException {
		ownerMemberOfCommunity(user);
	}
	
	@Override
	public void onJoinRequest(User user) throws SocialObjectNotJoinableException {
		if (communityType == CommunityType.CLOSE) {
			recordJoinRequest(user);
		} else {
			beMemberOfOpenCommunity(user, shouldJoinNotifyProfileInclude());
		}
	}

    public boolean shouldJoinNotifyProfileInclude() {
        return targetingType == null ||
               (targetingType != TargetingType.PLAYGROUP &&
                targetingType != TargetingType.PRE_NURSERY &&
                targetingType != TargetingType.KINDY);
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
		return getMembers(-1);
	}
	
	@JsonIgnore
	public List<User> getMembers(int limit) {
        Query query = JPA.em().createQuery(
            "select u from User u where u.id in (select sr.actor from SocialRelation sr where sr.target = ?1 and sr.action = ?2) and u.deleted = false order by u.id desc"
        );
        query.setParameter(1, this.id);
        query.setParameter(2, SocialRelation.Action.MEMBER);
        if (limit != -1)
        	query.setMaxResults(limit);
        return (List<User>) query.getResultList();
	}

    @JsonIgnore
	public List<Long> getMemberIds() {
        Query query = JPA.em().createQuery(
            "select u.id from User u where u.id in (select sr.actor from SocialRelation sr where sr.target = ?1 and sr.action = ?2) and u.deleted = false order by u.id"
        );
        query.setParameter(1, this.id);
        query.setParameter(2, SocialRelation.Action.MEMBER);
        return (List<Long>) query.getResultList();
	}

    @JsonIgnore
    public Long getMemberCount() {
        Query query = JPA.em().createNativeQuery(
            "select count(*) from User u where u.id in (select sr.actor from SocialRelation sr where sr.target = ?1 and sr.action = ?2) and u.deleted = false"
        );
        query.setParameter(1, this.id);
        query.setParameter(2, SocialRelation.Action.MEMBER.name());
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    @JsonIgnore
	public List<User> getMembersIn(List<Long> userIds) {
        if (userIds == null || userIds.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        Query query = JPA.em().createQuery(
            "select u from User u where "+
            "u.id in (select sr.actor from SocialRelation sr where sr.target = ?1 and sr.action = ?2) and "+
            "u.id in ("+StringUtil.collectionToString(userIds, ",")+") and "+
            "u.deleted = false"
        );
        query.setParameter(1, this.id);
        query.setParameter(2, SocialRelation.Action.MEMBER);
        return (List<User>) query.getResultList();
	}

    /**
     * Return (isPendingJoin, isMember)
     * @param userId
     * @return
     */
    public Pair<Boolean, Boolean> getMemberStatusForUser(Long userId) {
        boolean isPendingJoin = false, isMember = false;

        Query query = JPA.em().createQuery(
            "SELECT actionType, action from SocialRelation where actor = ?1 and target = ?2 and (actionType = ?3 or action = ?4)"
        );
        query.setParameter(1, userId);
        query.setParameter(2, this.id);
        query.setParameter(3, SocialRelation.ActionType.JOIN_REQUESTED);
        query.setParameter(4, SocialRelation.Action.MEMBER);

        List<Object[]> results = query.getResultList();
        for (Object[] result : results) {
            SocialRelation.ActionType actionType = (SocialRelation.ActionType) result[0];
            SocialRelation.Action action = (SocialRelation.Action) result[1];
            if (actionType == SocialRelation.ActionType.JOIN_REQUESTED) {
                isPendingJoin = true;
            }
            if (action == SocialRelation.Action.MEMBER) {
                isMember = true;
            }
        }
        return new Pair<>(isPendingJoin, isMember);
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
			this.albumPhotoProfile = createAlbum("profile", "", true);
			this.merge();
		}
	}

	public Folder createAlbum(String name, String description, Boolean system) {
		//if (ensureFolderExistWithGivenName(name)) {
			Folder folder = Folder.createFolder(this.owner, name, description,
					SocialObjectType.FOLDER, system);
			//folders.add(folder);
			this.merge(); // Add folder to existing User as new albumn
			return folder;
		//}
		//return null;
	}



    ///////////////////////// Find Community APIs /////////////////////////

	public static Community findById(Long id) {
	    try {
	        Query q = JPA.em().createQuery("SELECT c FROM Community c where id = ?1 and deleted = false");
	        q.setParameter(1, id);
	        return (Community)q.getSingleResult();
	    } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}

    public static List<Community> findOpenCommsByIds(List<Long> ids, int maxResults) {
        if (ids == null || ids.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        String idsForIn = StringUtil.collectionToString(ids, ",");
        Query q = JPA.em().createQuery("SELECT c FROM Community c "+
                "where c.id in ("+idsForIn+") and c.communityType = ?1 and c.deleted = false "+
                "order by FIELD(c.id ,"+idsForIn+")");
        q.setParameter(1, CommunityType.OPEN);
        q.setMaxResults(maxResults);
        return (List<Community>) q.getResultList();
    }


    public static List<Long> findIdsByCommunityType(CommunityType commType) {
        List<Long> result = new ArrayList<>();

        Query q = JPA.em().createQuery("SELECT c.id FROM Community c where c.communityType = ?1 and c.deleted = false");
        q.setParameter(1, commType);
        try {
            result = q.getResultList();
        } catch (NoResultException e) {
        }
        return result;
    }

    public static List<Long> findIdsByTargetingType(TargetingSocialObject.TargetingType targetingType) {
        List<Long> result = new ArrayList<>();

        Query q = JPA.em().createQuery("SELECT c.id FROM Community c where c.targetingType = ?1 and c.deleted = false");
        q.setParameter(1, targetingType);
        try {
            result = q.getResultList();
        } catch (NoResultException e) {
        }
        return result;
    }

    public static List<Long> findBusinessCommIdsByCategory(Long commCategoryId) {
        Query q = JPA.em().createNativeQuery("SELECT c.id FROM Community c where c.communityType = ?1 and c.deleted = false "+
                "and c.id in (select Community_id from Community_CommunityCategory where communityCategories_id = ?2)");
        q.setParameter(1, CommunityType.BUSINESS.ordinal());
        q.setParameter(2, commCategoryId.longValue());

        final List<Long> result = new ArrayList<>();
        try {
            List<BigInteger> commIds = q.getResultList();
            for (BigInteger commId : commIds) {
                result.add(commId.longValue());
            }
        } catch (NoResultException e) {
        }
		return result;
    }

    /**
     * Open community, not deleted, not excluded from newsfeed.
     * @return
     */
    public static List<Long> findSocialOpenCommIdsForNf(LocalDate socialUpdatedSince) {
        List<Long> result = new ArrayList<>();

        Query q = JPA.em().createQuery("SELECT c.id FROM Community c " +
                "where c.communityType = ?1 and c.deleted = false and c.excludeFromNewsfeed = false " +
                "and c.socialUpdatedDate > ?2");
        q.setParameter(1, CommunityType.OPEN);
        q.setParameter(2, socialUpdatedSince.toDate());

        try {
            result = q.getResultList();
        } catch (NoResultException e) {
        }
        return result;
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

    public static Community findByName(String name) {
        Query q = JPA.em().createQuery("SELECT c FROM Community c where name = ?1 and deleted = false");
	    q.setParameter(1, name);
        try {
            return (Community)q.getSingleResult();
        } catch (NoResultException e) {
        }
        return null;
	}

    public static Community findByNameTargetingTypeInfo(String name,
                                                        TargetingSocialObject.TargetingType targetingType,
                                                        String targetingInfo) {
        Query q;
        if (targetingInfo != null) {
	        q = JPA.em().createQuery("SELECT c FROM Community c where system=?1 and targetingType=?2 and name = ?3 and targetingInfo=?4 and deleted = false");
        } else {
            q = JPA.em().createQuery("SELECT c FROM Community c where system=?1 and targetingType=?2 and name = ?3 and deleted = false");
        }
	    q.setParameter(1, true);
        q.setParameter(2, targetingType);
        q.setParameter(3, name);
        if (targetingInfo != null) {
            q.setParameter(4, targetingInfo);
        }
        try {
            return (Community)q.getSingleResult();
        } catch (NoResultException e) {
        }
        return null;
	}

	public static List<Community> findByCategory(CommunityCategory category) {
        Query q = JPA.em().createNativeQuery("SELECT * FROM Community c where c.deleted = false" + 
                " and c.id in (select cc.Community_id from Community_CommunityCategory cc where cc.communityCategories_id = ?1)", 
                Community.class);
        q.setParameter(1, category.id);
        try {
            return (List<Community>)q.getResultList();
        } catch (NoResultException e) {
        }
        return null;
    }


	public static File getDefaultThumbnailCoverPhoto() throws FileNotFoundException {
		return new File(STORAGE_COMMUNITY_COVER_THUMBNAIL_NOIMAGE);
	}
	
	public static File getDefaultMiniCoverPhoto() throws FileNotFoundException {
		return new File(STORAGE_COMMUNITY_COVER_MINI_NOIMAGE);
	}
	
	public static File getDefaultCoverPhoto() throws FileNotFoundException {
		 return new File(STORAGE_COMMUNITY_COVER_NOIMAGE);
	}
	
	public boolean doesCommunityNameExist() {
		Query q = JPA.em().createQuery("Select c.id from Community c where c.name = ?1 and c.deleted = false");
		q.setParameter(1, this.name);
		try {
			q.getSingleResult();
            return true;
		} catch (NoResultException nre) {
            return false;
		}
	}

    ///////////////////////// Find Posts, Questions APIs /////////////////////////

	@JsonIgnore
	public List<Post> getPostsOfCommunity(int offset, int limit) {
		Query q = JPA.em().createQuery("Select p from Post p where community=?1 and postType=?2 and deleted = false order by socialUpdatedDate desc");
		q.setParameter(1, this);
        q.setParameter(2, PostType.SIMPLE);
		q.setFirstResult(offset);
		q.setMaxResults(limit);
		return (List<Post>)q.getResultList();
	}
	
	@JsonIgnore
	public List<Post> getPostsOfCommunityByTime(Long time, int limit) {
		Query q = JPA.em().createQuery("Select p from Post p where community=?1 and postType=?2 and deleted = false and (socialUpdatedDate < ?3) order by socialUpdatedDate desc");
		q.setParameter(1, this);
        q.setParameter(2, PostType.SIMPLE);
		q.setParameter(3, new Date(time));
		q.setMaxResults(limit);
		return (List<Post>)q.getResultList();
	}
	
	@JsonIgnore
	public List<Post> getQuestionsOfCommunityByTime(Long time, int limit) {
		Query q = JPA.em().createQuery("Select p from Post p where community=?1 and postType=?2 and deleted = false and (socialUpdatedDate < ?3) order by socialUpdatedDate desc");
		q.setParameter(1, this);
        q.setParameter(2, PostType.QUESTION);
		q.setParameter(3, new Date(time));
		q.setMaxResults(limit);
		return (List<Post>)q.getResultList();
	}
	
	@JsonIgnore
	public List<Post> getQuestionsOfCommunity(int offset, int limit) {
		Query q = JPA.em().createQuery("Select p from Post p where community=?1 and postType=?2 and deleted = false order by socialUpdatedDate desc");
		q.setParameter(1, this);
        q.setParameter(2, PostType.QUESTION);
		q.setFirstResult(offset);
		q.setMaxResults(limit);
		return (List<Post>)q.getResultList();
	}

    @JsonIgnore
    public Long getQuestionsCount() {
        Query query = JPA.em().createQuery("select count(p.id) from Post p where community=?1 and postType=?2 and deleted = false");
        query.setParameter(1, this.id);
        query.setParameter(2, PostType.QUESTION);
        return (Long) query.getSingleResult();
    }
	
	@JsonIgnore
	public List<User> getNonMembersOfCommunity(String query) {
		Query q = JPA.em().createQuery("Select u from User u where lower(u.displayName) LIKE '%"+ query.toLowerCase() +"%' AND " +
				"u.id not in (select sr.actor from SocialRelation sr where sr.target = ?1 and " + 
		        "(sr.action = 'MEMBER' OR sr.actionType = 'INVITE_REQUESTED')) and u.deleted = false");
		q.setParameter(1, this.id);
		return (List<User>)q.getResultList();
	}



	public void sendInviteToJoin(User invitee)
			throws SocialObjectNotJoinableException {
		recordInviteRequestByCommunity(invitee);
	}


    public static CommunityType getCommunityTypeById(Long commId) {
        Query q = JPA.em().createNativeQuery("SELECT c.communityType FROM Community c where c.id = ?1");
        q.setParameter(1, commId.longValue());
        Integer commTypeInt = (Integer) q.getSingleResult();
        if (commTypeInt != null) {
            return CommunityType.values()[commTypeInt];
        } else {
            return null;
        }
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

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Community) {
            final Community other = (Community) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
    
    @Override
    public int compareTo(Community o) {
        if (this.system != o.system) {
            return this.system.compareTo(o.system);
        }
        if (this.targetingType != null && o.targetingType != null && 
        		this.targetingType != o.targetingType) {
        	return this.targetingType.compareTo(o.targetingType);
        }
        if (this.communityType != null && o.communityType != null && 
        		this.communityType != o.communityType) {
            return this.communityType.compareTo(o.communityType);
        }
        return this.name.compareTo(o.name);
    }
}
