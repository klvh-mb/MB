package models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import common.cache.FriendCache;
import common.collection.Pair;
import common.image.FaceFinder;
import common.utils.DateTimeUtil;
import common.utils.ImageFileUtil;
import common.utils.NanoSecondStopWatch;
import common.utils.StringUtil;
import models.Community.CommunityType;
import models.Notification.NotificationType;
import models.SocialRelation.Action;
import models.SocialRelation.ActionType;
import models.TargetingSocialObject.TargetingType;
import models.TokenAction.Type;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Play;
import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import processor.FeedProcessor;
import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;

import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthProvider;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.FirstLastNameIdentity;
import com.feth.play.module.pa.user.NameIdentity;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.exception.SocialObjectNotLikableException;
import com.mnt.exception.SocialObjectNotPostableException;

import controllers.Application;
import domain.CommentType;
import domain.DefaultValues;
import domain.PostType;
import domain.SocialObjectType;
import domain.Socializable;

@Entity
public class User extends SocialObject implements Subject, Socializable {
    private static final play.api.Logger logger = play.api.Logger.apply(User.class);

    private static User SUPER_ADMIN;
    
    public String firstName;
    public String lastName;
    public String displayName;
    public String email;
    
    // Targeting info
    
    @OneToOne
    public UserInfo userInfo;
    
    @OneToMany
    public List<UserChild> children;
   
    // fb info
    
    public boolean fbLogin;
    
    @OneToOne
    public FbUserInfo fbUserInfo;
    
    @OneToMany
    public List<FbUserFriend> fbUserFriends;
    
    // stats
    
    public int questionsCount = 0;
    
    public int answersCount = 0;
    
    public int postsCount = 0;
    
    public int commentsCount = 0;
    
    public int likesCount = 0;
    
    // system
    
    @JsonIgnore
    public boolean active;

    @JsonIgnore
    public boolean emailValidated;

    @JsonIgnore
    public boolean newUser;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    public Date lastLogin;
       
    @ManyToMany
    public List<SecurityRole> roles;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    public List<LinkedAccount> linkedAccounts;

    @ManyToMany
    @JsonIgnore
    public List<UserPermission> permissions;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JsonIgnore
    public Folder albumPhotoProfile;
    
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JsonIgnore
    public Folder albumCoverProfile;

    @Override
    @JsonIgnore
    public String getIdentifier() {
        return Long.toString(id);
    }

    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    public List<Folder> folders;

    @OneToMany(cascade = CascadeType.REMOVE)
    public List<Album> album;

    @OneToMany
    @JsonIgnore
    public List<Conversation> conversation = new ArrayList<Conversation>();

    @Override
    @JsonIgnore
    public List<? extends Role> getRoles() {
        return roles;
    }

    @Override
    public List<? extends Permission> getPermissions() {
        return permissions;
    }

    public User() {
        this.objectType = SocialObjectType.USER;
    }

    public User(String firstName, String lastName, String displayName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.name = firstName;
    }

    public void likesOn(SocialObject target)
            throws SocialObjectNotLikableException {
        target.onLikedBy(this);
    }

    public SocialObject postedOn(SocialObject target, String post)
            throws SocialObjectNotPostableException {
        return target.onPost(this, null, post, PostType.SIMPLE);
    }

    public SocialObject questionedOn(SocialObject target, String title, String question)
            throws SocialObjectNotPostableException {
        return target.onPost(this, title, question, PostType.QUESTION);
    }

    public Conversation sendMessage(User user, String msg) {
        Conversation conver = Conversation.findBetween(this, user);

        if (conversation == null || conver == null) {
            conver = new Conversation(this, user);
            conversation = Lists.newArrayList();
            conversation.add(conver);
            user.conversation.add(conver);
        }
        conver.addMessage(this, msg);
        return conver;
    }

    public SocialObject commentedOn(SocialObject target, String comment)
            throws SocialObjectNotCommentableException {
        return target.onComment(this, comment, CommentType.SIMPLE);
    }

    public void answeredOn(SocialObject target, String comment)
            throws SocialObjectNotCommentableException {
        target.onComment(this, comment, CommentType.ANSWER);
    }

    public void requestedToJoin(SocialObject target)
            throws SocialObjectNotJoinableException {
        // (pendingJoin, isMember)
        Pair<Boolean, Boolean> memStatus = ((Community) target).getMemberStatusForUser(this.id);
        if (!memStatus.first && !memStatus.second) {
            target.onJoinRequest(this);
        }
    }

    public void joinRequestAccepted(SocialObject target, User toBeMember)
            throws SocialObjectNotJoinableException {
        target.onJoinRequestAccepted(toBeMember);
    }

    public void inviteRequestAccepted(SocialObject target, User toBeMember)
            throws SocialObjectNotJoinableException {
        target.onInviteRequestAccepted(toBeMember);
    }
    
    public void markNotificationRead(Notification notification) {
        notification.markNotificationRead();
    }

    @Override
    public void sendFriendInviteTo(User invitee)
            throws SocialObjectNotJoinableException {
        recordFriendRequest(invitee);
    }

    @Override
    @Transactional
    public void onFriendRequestAccepted(User user)
            throws SocialObjectNotJoinableException {
        JPA.em().merge(this);
        recordFriendRequestAccepted(user);
    }

    public void onRelationShipRequest(User user, Action relation)
            throws SocialObjectNotJoinableException {
        recordRelationshipRequest(user, relation);
    }

    @Transactional
    public void onRelationShipRequestAccepted(User user, Action action)
            throws SocialObjectNotJoinableException {
        JPA.em().merge(this);
        recordRelationshipRequestAccepted(user, action);
    }

    @JsonIgnore
    public List<User> getFriends() {
        return  getFriends(-1);
    }
    
    @JsonIgnore
    public List<User> getFriends(int limit) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<SocialRelation> q = cb.createQuery(SocialRelation.class);
        Root<SocialRelation> c = q.from(SocialRelation.class);
        q.select(c);
        q.where(cb.and(
                cb.or(cb.equal(c.get("target"), this.id),
                        cb.equal(c.get("actor"), this.id)),
                cb.equal(c.get("action"), SocialRelation.Action.FRIEND)));

        List<SocialRelation> result;
        if(limit == -1) {
            result = JPA.em().createQuery(q).getResultList();
        } else {
            result = JPA.em().createQuery(q).setMaxResults(limit).getResultList();
        }
        List<User> frndList = new ArrayList<>();
        for (SocialRelation rslt : result) {
            if (rslt.actor.equals(this.id)) {
                User user = (User) rslt.getTargetObject(User.class);
                if (user != null) {
                    frndList.add(user);
                }
            }
            else if (rslt.target.equals(this.id)) {
                User user = (User) rslt.getActorObject(User.class);
                if (user != null) {
                    frndList.add(user);
                }
            }
        }
        return frndList;
    }
    
    @JsonIgnore
    public Long getFriendsSize() {
        int count = FriendCache.getFriendsIds(this.id).size();
        return new Long(count);
    }
    
    @JsonIgnore
    public List<User> getSuggestedFriends(int limit) {
        Query q = JPA.em().createNativeQuery(
                "Select * from User u where u.id <> ?1 and u.id not in (" + 
                        "select sr.target from SocialRelation sr where (sr.action = ?2 or sr.actionType = ?3) and sr.actor = ?1 union " + 
                        "select sr1.actor from SocialRelation sr1 where (sr1.action = ?2 or sr1.actionType = ?3) and sr1.target = ?1" + 
                        ") and u.emailValidated = true and u.system = 0 and u.userInfo_id is not NULL and u.deleted = false", User.class);
        q.setParameter(1, this.id);
        q.setParameter(2, SocialRelation.Action.FRIEND.name());
        q.setParameter(3, SocialRelation.ActionType.FRIEND_REQUESTED.name());
        List<User> frndList = (List<User>)q.setMaxResults(limit).getResultList();
        return frndList;
    }

    /**
     * Return list of communities this use has joined.
     * @return
     */
    @JsonIgnore
    public List<Community> getListOfJoinedCommunities() {
        Query query = JPA.em().createNativeQuery(
                "select * from Community c where"+
                " c.id in (select sr.target from SocialRelation sr where sr.actor = ?1 and sr.action = ?2 and sr.targetType = ?3) and c.deleted = false",
                Community.class);
        query.setParameter(1, this.id);
        query.setParameter(2, Action.MEMBER.name());
        query.setParameter(3, SocialObjectType.COMMUNITY.name());

        List<Community> communityList = (List<Community>)query.getResultList();

        // sort by name
        Collections.sort(communityList);
        return communityList;
    }

    /**
     * Return list of community ids this use has joined - used by Newsfeed.
     * @return
     */
    @JsonIgnore
    public List<Long> getListOfJoinedCommunityIds() {
        Query query = JPA.em().createNativeQuery(
                "select c.id from Community c where"+
                " c.id in (select sr.target from SocialRelation sr where sr.actor = ?1 and sr.action = ?2 and sr.targetType = ?3) and c.deleted = false");
        query.setParameter(1, this.id);
        query.setParameter(2, Action.MEMBER.name());
        query.setParameter(3, SocialObjectType.COMMUNITY.name());

        List<BigInteger> commIds = query.getResultList();

        List<Long> result = new ArrayList<>();
		for (BigInteger commId : commIds) {
			result.add(commId.longValue());
		}
		return result;
    }
    
    @JsonIgnore
    public List<Community> getListOfJoinedCommunities(int offset, int limit) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<SocialRelation> q = cb.createQuery(SocialRelation.class);
        Root<SocialRelation> c = q.from(SocialRelation.class);
        q.select(c);
        q.where(cb.and(cb.equal(c.get("actor"), this.id),
                cb.equal(c.get("action"), Action.MEMBER)));
        
        List<SocialRelation> result = JPA.em().createQuery(q).setFirstResult(offset)
                    .setMaxResults(limit).getResultList();

        List<Community> communityList = new ArrayList<>();
        for (SocialRelation rslt : result) {
            if (rslt.actor.equals(this.id)
                    && rslt.targetType == SocialObjectType.COMMUNITY) {
                Community community = (Community) rslt.getTargetObject(Community.class);
                if (community != null) {
                    communityList.add(community);
                }
            }
        }
        return communityList;
    }

    /**
     * Get list of communities which friends have joined, but that this user has not.
     * @return
     */
    @JsonIgnore
    public List<Community> getListOfNotJoinedCommunities() {
        // return the list of comm ids which friends have joined, number of rows based on number of friends.
        Query commIdListQuery = JPA.em().createNativeQuery(
                "select sr2.target from SocialRelation sr2 where sr2.actor in " + 
                        "(select sr.target from SocialRelation sr where (sr.action = ?2 or sr.actionType = ?3) and sr.actor = ?1 union " + 
                        "select sr1.actor from SocialRelation sr1 where (sr1.action = ?2 or sr1.actionType = ?3) and sr1.target = ?1 ) and " + 
                        "sr2.action = ?4 and sr2.targetType = ?5 and sr2.target not in (select c.id from Community c where c.system=true and c.targetingType in (?6, ?7, ?8, ?9, ?10))");
        commIdListQuery.setParameter(1, this.id);
        commIdListQuery.setParameter(2, SocialRelation.Action.FRIEND.name());
        commIdListQuery.setParameter(3, SocialRelation.ActionType.FRIEND_REQUESTED.name());
        commIdListQuery.setParameter(4, SocialRelation.Action.MEMBER.name());
        commIdListQuery.setParameter(5, SocialObjectType.COMMUNITY.name());
        commIdListQuery.setParameter(6, TargetingType.ZODIAC_YEAR.name());
        commIdListQuery.setParameter(7, TargetingType.ZODIAC_YEAR_MONTH.name());
        commIdListQuery.setParameter(8, TargetingType.LOCATION_DISTRICT.name());
        commIdListQuery.setParameter(9, TargetingType.LOCATION_REGION.name());
        commIdListQuery.setParameter(10, TargetingType.PRE_NURSERY.name());

        List<BigInteger> commIds = commIdListQuery.getResultList();
        List<Community> result = Collections.EMPTY_LIST;

        if (commIds.size() > 0) {
            final Map<Long, AtomicInteger> commFrdsCount = new HashMap<>();
            for (BigInteger cid : commIds) {
                Long commId = cid.longValue();
                AtomicInteger count = commFrdsCount.get(commId);
                if (count == null) {
                    count = new AtomicInteger();
                    commFrdsCount.put(commId, count);
                }
                count.incrementAndGet();
            }

            // return the list of communities which friends belong, but that user doesn't belong to.
            Query q = JPA.em().createNativeQuery(
                    "select * from Community c where"+
                    " not exists (select * from SocialRelation sr where sr.actor = ?1 and target = c.id and sr.action = ?2 and sr.targetType = ?3)"+
                    " and c.id in ("+ StringUtil.collectionToString(commFrdsCount.keySet(), ",")+") and c.communityType = ?4 and c.deleted = false",
                    Community.class);
            q.setParameter(1, this.id);
            q.setParameter(2, SocialRelation.Action.MEMBER.name());
            q.setParameter(3, SocialObjectType.COMMUNITY.name());
            q.setParameter(4, CommunityType.OPEN.ordinal());    // Open only

            result = (List<Community>)q.getResultList();

            // sort by friends count
            Collections.sort(result, new Comparator<Community>() {
                @Override
                public int compare(Community o1, Community o2) {
                    int ret = 0;

                    AtomicInteger frdCount1 = commFrdsCount.get(o1.id);
                    AtomicInteger frdCount2 = commFrdsCount.get(o2.id);

                    if (frdCount1 != null && frdCount2 != null) {
                        ret = -1 * (frdCount1.intValue() - frdCount2.intValue());
                    }
                    /*
                    if (ret == 0) {
                        ret = -1 * (o1.getMemberIds().size() - o2.getMemberIds().size());
                    }
                    */
                    return ret;
                }
            });
        }

        return result;
    }

    @JsonIgnore
    public List<Community> getListOfNotJoinedCommunities(int offset, int limit) {
        
        Query q = JPA.em().createQuery("Select c from Community c where c.id not in (select sr.target from SocialRelation sr where sr.action = ?2 and sr.actor = ?1) and c.deleted = false");
        q.setParameter(1, this.id);
        q.setParameter(2, Action.MEMBER);
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        List<Community> communityList = q.getResultList();
        return communityList;
    }
    
    public static List<User> searchLike(String q) {
        CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        criteria.select(root);
        Predicate predicate = 
                builder.and(builder.or(
                        builder.like(builder.upper(root.<String>get("displayName")), "%" + q.toUpperCase() + "%"),
                        builder.like(builder.upper(root.<String>get("firstName")), "%" + q.toUpperCase() + "%"),
                        builder.like(builder.upper(root.<String>get("lastName")), "%" + q.toUpperCase() + "%")),
                        builder.equal(root.get("deleted"), false));
        criteria.where(predicate);
        return JPA.em().createQuery(criteria).getResultList();
    }

    public static List<Community> searchCommunity(String string) {
        CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Community> criteria = builder
                .createQuery(Community.class);
        Root<Community> root = criteria.from(Community.class);
        criteria.select(root);
        Predicate predicate = builder.like(root.<String> get("name"), "%"
                + string + "%");
        criteria.where(predicate);
        return JPA.em().createQuery(criteria).getResultList();
    }

    public static User searchEmail(String email) {
        CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        criteria.select(root);
        Predicate predicate = (builder.equal(root.get("email"), email));
        criteria.where(predicate);
        return JPA.em().createQuery(criteria).getSingleResult();
    }

    public Resource setPhotoProfile(File file) throws IOException {
        ensureAlbumPhotoProfileExist();

        // Pre-process file to have face centered.
        BufferedImage croppedImage = FaceFinder.getSquarePictureWithFace(file);
        ImageFileUtil.writeFileWithImage(file, croppedImage);

        Resource newPhoto = this.albumPhotoProfile.addFile(file,
                SocialObjectType.PROFILE_PHOTO);
        this.albumPhotoProfile.setHighPriorityFile(newPhoto);
        newPhoto.save();
        return newPhoto;
    }
    
    public Resource setCoverPhoto(File source) throws IOException {
        ensureCoverPhotoProfileExist();

        // Pre-process file to have face centered.
        BufferedImage croppedImage = FaceFinder.getRectPictureWithFace(source, 2.29d);
        ImageFileUtil.writeFileWithImage(source, croppedImage);

        Resource cover_photo = this.albumCoverProfile.addFile(source,
                SocialObjectType.COVER_PHOTO);
        this.albumCoverProfile.setHighPriorityFile(cover_photo);
        cover_photo.save();
        return cover_photo;
    }

    public void removePhotoProfile(Resource resource) throws IOException {
        this.albumPhotoProfile.removeFile(resource);
    }

    /**
     * get the photo profile
     * 
     * @return the resource, null if not exist
     */
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
    
    @JsonIgnore
    public Resource getCoverProfile() {
        if (this.albumCoverProfile != null) {
            Resource file = this.albumCoverProfile.getHighPriorityFile();
            if (file != null) {
                return file;
            }
        }
        return null;
    }

    @JsonIgnore
    public Resource getMiniProfileImage() {
        if (this.albumPhotoProfile != null) {
            Resource file = this.albumPhotoProfile.getHighPriorityFile();
            if (file != null) {
                return file;
            }
        }
        return null;
    }
    
    public String getCoverProfileURL() {
        Resource resource = getCoverProfile();
        if (resource == null) {
            return "";
        }
        return resource.getPath();
    }

    /**
     * ensure the existence of the system folder: albumPhotoProfile
     */
    private void ensureAlbumPhotoProfileExist() {

        if (this.albumPhotoProfile == null) {
            this.albumPhotoProfile = createAlbum("profile", "", true);
            this.merge();
        }
    }
    
    /**
     * ensure the existence of the system folder: albumPhotoProfile
     */
    private void ensureCoverPhotoProfileExist() {

        if (this.albumCoverProfile == null) {
            this.albumCoverProfile = createAlbum("cover", "", true);
            this.merge();
        }
    }
    
    @Transactional
    public Community createCommunity(String name, String description, CommunityType type, String icon) 
            throws SocialObjectNotJoinableException {
        if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(description) || 
                Strings.isNullOrEmpty(icon) || type == null) {
            logger.underlyingLogger().warn("Missing parameters to createCommunity");
            return null;
        }
        Community community = new Community(name, description, this, type);
        community.icon = icon;
        community.save();
        community.ownerAsMember(this);
        return community;
    }

    /**
     * create a folder with the type: IMG (contain only image Resource types)
     * 
     * @param name
     * @param description
     * @param system
     * @return
     */
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

    public Album createAlbum(String name, String description, Boolean system,
            SocialObjectType type) {

        if (ensureAlbumExistWithGivenName(name)) {
            Album _album = createAlbum(name, description,
                    SocialObjectType.ALBUMN, system);
            album.add(_album);
            this.merge();
            return _album;
        }
        return null;
    }

    private boolean ensureAlbumExistWithGivenName(String name) {

        if (album == null) {
            album = new ArrayList<>();
        }

        if (album.contains(new Album(name))) {
            return false;
        }

        return true;
    }

    private Album createAlbum(String name, String description,
            SocialObjectType type, Boolean system) {
        Folder folder = createFolder(name, description,
                SocialObjectType.FOLDER, system);

        Album _album = new Album(name);
        _album.owner = this;
        _album.name = name;
        _album.description = description;
        _album.objectType = type;
        _album.system = system;
        _album.folder = folder;
        _album.save();
        return _album;
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

        if (album != null && album.contains(new Folder(name))) {
            return false;
        }

        album = new ArrayList<>();
        return true;
    }

    public static boolean existsByAuthUserIdentity(
            final AuthUserIdentity identity) {
        final Query exp;
        if (identity instanceof UsernamePasswordAuthUser) {
            exp = getUsernamePasswordAuthUserFind((UsernamePasswordAuthUser) identity);
        } else {
            exp = getAuthUserFind(identity);
        }
        return exp.getResultList().size() > 0;
    }

    private static Query getAuthUserFind(final AuthUserIdentity identity) {
        Query q = JPA.em().createQuery(
                "SELECT u FROM User u, IN (u.linkedAccounts) l where active = ?1 and l.providerUserId = ?2 and l.providerKey = ?3 and u.deleted = false");
        q.setParameter(1, true);
        q.setParameter(2, identity.getId());
        q.setParameter(3, identity.getProvider());
        return q;
    }

    public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
        if (identity == null) {
            return null;
        }
        if (identity instanceof UsernamePasswordAuthUser) {
            return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
        } else {
            try {
                return (User) getAuthUserFind(identity).getSingleResult();
            } catch(javax.persistence.NoResultException e ) {
                return null;
            }
        }
    }

    @Transactional
    public static User findByUsernamePasswordIdentity(
            final UsernamePasswordAuthUser identity) {
        try {
            return (User) getUsernamePasswordAuthUserFind(identity)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    @JsonIgnore
    private static Query getUsernamePasswordAuthUserFind(
            final UsernamePasswordAuthUser identity) {

        Query q = JPA.em().createQuery(
                "SELECT u FROM User u, IN (u.linkedAccounts) l where active = ?1 and email = ?2 and  l.providerKey = ?3 and u.deleted = false");
        q.setParameter(1, true);
        q.setParameter(2, identity.getEmail());
        q.setParameter(3, identity.getProvider());
        return q;
    }

    public void merge(final User otherUser) {
        for (final LinkedAccount acc : otherUser.linkedAccounts) {
            this.linkedAccounts.add(LinkedAccount.create(acc));
        }
        // do all other merging stuff here - like resources, etc.

        // deactivate the merged user that got added to this one
        otherUser.active = false;
        this.merge();
        otherUser.merge();
    }

    public static User create(final AuthUser authUser) {
        final User user = new User();
        user.roles = Collections.singletonList(SecurityRole
                .findByRoleName(controllers.Application.USER_ROLE));
        user.active = true;
        user.newUser = true;
        user.lastLogin = new Date();
        user.fbLogin = false;
        
        if (authUser instanceof EmailIdentity) {
            final EmailIdentity identity = (EmailIdentity) authUser;
            user.email = identity.getEmail();
            user.emailValidated = false;
        }

        if (authUser instanceof NameIdentity) {
            final NameIdentity identity = (NameIdentity) authUser;
            final String name = identity.getName();
            if (name != null) {
                user.name = name;
                user.displayName = name;
            }
        }

        if (authUser instanceof FirstLastNameIdentity) {
            final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;
            final String firstName = identity.getFirstName();
            final String lastName = identity.getLastName();
            if (firstName != null) {
                user.firstName = firstName;
            }
            if (lastName != null) {
                user.lastName = lastName;
            }
        }

        if (authUser instanceof FacebookAuthUser) {
            final FacebookAuthUser fbAuthUser = (FacebookAuthUser) authUser;
            FbUserInfo fbUserInfo = new FbUserInfo(fbAuthUser);
            fbUserInfo.save();
            
            // TODO - keith
            // save FbUserFriend here
            
            user.fbLogin = true;
            user.fbUserInfo = fbUserInfo;
            user.emailValidated = fbAuthUser.isVerified();
        }
        
        user.save();
        user.linkedAccounts = Collections.singletonList(
                LinkedAccount.create(authUser).addUser(user));
        // user.saveManyToManyAssociations("roles");
        // user.saveManyToManyAssociations("permissions");
        return user;
    }

    public static void merge(final AuthUser oldUser, final AuthUser newUser) {
        User.findByAuthUserIdentity(oldUser).merge(
                User.findByAuthUserIdentity(newUser));
    }

    @JsonIgnore
    public Set<String> getProviders() {
        final Set<String> providerKeys = new HashSet<String>(
                linkedAccounts.size());
        for (final LinkedAccount acc : linkedAccounts) {
            providerKeys.add(acc.providerKey);
        }
        return providerKeys;
    }

    public static void addLinkedAccount(final AuthUser oldUser,
            final AuthUser newUser) {
        final User u = User.findByAuthUserIdentity(oldUser);
        u.linkedAccounts.add(LinkedAccount.create(newUser));
        u.save();
    }

    public static void setLastLoginDate(final AuthUser knownUser) {
        final User u = User.findByAuthUserIdentity(knownUser);
        u.lastLogin = new Date();
        u.save();
    }

    public static User findByEmail(final String email) {
        try {
            Query q = JPA.em().createQuery(
                    "SELECT u FROM User u where active = ?1 and email = ?2 and deleted = false");
            q.setParameter(1, true);
            q.setParameter(2, email);
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static User findByFbEmail(final String email) {
        try {
            Query q = JPA.em().createQuery(
                    "SELECT u FROM User u where active = ?1 and email = ?2 and providerKey = ?3 and deleted = false");
            q.setParameter(1, true);
            q.setParameter(2, email);
            q.setParameter(3, FacebookAuthProvider.PROVIDER_KEY);
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Transactional
    public boolean isSuperAdmin() {
        for (SecurityRole role : roles) {
            if (Application.SUPER_ADMIN_ROLE.equals(role.roleName)) {
                return true;
            }
        }
        return false;
    }
    
    @Transactional
    public static User getSuperAdmin() {
        if (SUPER_ADMIN != null)
            return SUPER_ADMIN;
        
        Query q = JPA.em().createQuery("SELECT u FROM User u where active = ?1 and system = ?2 and deleted = false");
        q.setParameter(1, true);
        q.setParameter(2, true);
        List<User> sysUsers = (List<User>)q.getResultList();
        for (User sysUser : sysUsers) {
            if (sysUser.isSuperAdmin()) {
                SUPER_ADMIN = sysUser;
            }
        }
        return SUPER_ADMIN;
    }
    
    @Transactional
    public static Long getTodaySignupCount() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        Query q = JPA.em().createQuery(
                "SELECT count(u) FROM User u where " +  
                        "system = false and deleted = false and " + 
                        "CREATED_DATE >= ?1 and CREATED_DATE < ?2");
        q.setParameter(1, DateTimeUtil.getToday().toDate());
        q.setParameter(2, DateTimeUtil.getTomorrow().toDate());
        Long count = (Long)q.getSingleResult();

        sw.stop();
        logger.underlyingLogger().info("getTodaySignupCount="+count+". Took "+sw.getElapsedMS()+"ms");
        return count;
    }

    @JsonIgnore
    public LinkedAccount getAccountByProvider(final String providerKey) {
        return LinkedAccount.findByProviderKey(this, providerKey);
    }

    public static void verify(final User unverified) {
        // You might want to wrap this into a transaction
        unverified.emailValidated = true;
        unverified.save();
        TokenAction.deleteByUser(unverified, Type.EMAIL_VERIFICATION);
    }

    public void changePassword(final UsernamePasswordAuthUser authUser,
            final boolean create) {
        LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
        if (a == null) {
            if (create) {
                a = LinkedAccount.create(authUser);
                a.user = this;
            } else {
                throw new RuntimeException(
                        "Account not enabled for password usage");
            }
        }
        a.providerUserId = authUser.getHashedPassword();
        a.save();
    }

    public void resetPassword(final UsernamePasswordAuthUser authUser,
            final boolean create) {
        // You might want to wrap this into a transaction
        this.changePassword(authUser, create);
        TokenAction.deleteByUser(this, Type.PASSWORD_RESET);
    }

    public static User findById(Long id) {
        try { 
            Query q = JPA.em().createQuery("SELECT u FROM User u where id = ?1 and deleted = false");
            q.setParameter(1, id);
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getDefaultUserPhoto() throws FileNotFoundException {
         return new File(Play.application().configuration().getString("storage.user.noimage"));
    }

    public static File getDefaultCoverPhoto() throws FileNotFoundException {
         return new File(Play.application().configuration().getString("storage.user.cover.noimage"));
    }
    
    @JsonIgnore
    public List<Notification> getAllFriendRequestNotification() {
        
        Query q = JPA.em().createQuery(
                "SELECT n from Notification n where recipetent = ?1 and socialAction.actionType = ?2 and readed = ?3 ");
        q.setParameter(1, this.id);
        q.setParameter(2, ActionType.FRIEND_REQUESTED);
        q.setParameter(3, false);
        List<Notification> notifications = q.getResultList();
        return notifications;
    }
    
    @JsonIgnore
    public List<Notification> getAllJoinRequestNotification() {
        
        Query q = JPA.em().createQuery(
                "SELECT n from Notification n where recipetent = ?1 and notificationType in (?2,?3,?4) and readed = ?5 ");
        q.setParameter(1, this.id);
        q.setParameter(2, NotificationType.COMMUNITY_JOIN_REQUEST);
        q.setParameter(3, NotificationType.COMMUNITY_INVITE_REQUEST);
        q.setParameter(4, NotificationType.COMMUNITY_JOIN_APPROVED);
        q.setParameter(5, false);
        List<Notification> notifications = q.getResultList();
        return notifications;
    }

    @JsonIgnore
    public boolean isFriendOf(User localUser) {
        boolean isFriend = FriendCache.areFriends(this.id, localUser.id);
        return isFriend;
    }

    @JsonIgnore
    public boolean isMemberOf(Community community) {
        return isMemberOf(community.id);
    }

    @JsonIgnore
    public boolean isMemberOf(Long communityId) {
        Query query = JPA.em().createQuery(
                "SELECT count(*) from SocialRelation where actor = ?1 and target = ?2 and action = ?3"
        );
        query.setParameter(1, this.id);
        query.setParameter(2, communityId);
        query.setParameter(3, SocialRelation.Action.MEMBER);
        Long result = (Long) query.getSingleResult();
        return result == 1;
    }

    @JsonIgnore
    public boolean isFriendRequestPendingFor(User user) {
        Query query = JPA.em().createQuery(
                "SELECT count(*) from SocialRelation where ((target = ?1 and actor = ?2) or (actor = ?1 and target = ?2)) and actionType = ?3");
        query.setParameter(1, this.id);
        query.setParameter(2, user.id);
        query.setParameter(3, SocialRelation.ActionType.FRIEND_REQUESTED);
        Long result = (Long) query.getSingleResult();
        return result == 1;
    }
    
    public int doUnFriend(User toBeUnfriend) {
        Query query = JPA.em().createQuery(
                "SELECT sr FROM SocialRelation sr where sr.actionType=?1 And sr.action = ?4 And " +
                " ((sr.target = ?2 and sr.actor = ?3) or (sr.actor = ?2 and sr.target = ?3))", SocialRelation.class);
        query.setParameter(1, SocialRelation.ActionType.GRANT);
        query.setParameter(2, this.id);
        query.setParameter(3, toBeUnfriend.id);
        query.setParameter(4, SocialRelation.Action.FRIEND);
        
        SocialRelation sr= (SocialRelation) query.getSingleResult();
        query = JPA.em().createQuery("DELETE  Notification n where socialAction =?1");
        query.setParameter(1, sr);
        query.executeUpdate();

        // delete SocialRelation
        sr.delete();

        // update friends cache
        FriendCache.onUnFriend(this.id, toBeUnfriend.id);
        
        return 1;
    }
    
    public int doUnLike(Long id, SocialObjectType type) {
        
        Query query = JPA.em().createQuery(
                "SELECT sr FROM PrimarySocialRelation sr where  sr.targetType = ?4 and sr.action = ?3 And " + 
                " ((sr.target = ?1 and sr.actor = ?2))", PrimarySocialRelation.class);
        query.setParameter(1, id);
        query.setParameter(2, this.id);
        query.setParameter(3, PrimarySocialRelation.Action.LIKED);
        query.setParameter(4, type);
        
        PrimarySocialRelation sr= (PrimarySocialRelation) query.getSingleResult();
        
        sr.delete();
        
        return 1;
    }
    
    public int unBookmarkOn(Long id, SocialObjectType type) {
        
        Query query = JPA.em().createQuery(
                "SELECT sr FROM SecondarySocialRelation sr where  sr.targetType = ?4 and sr.action = ?3 And " +
                " ((sr.target = ?1 and sr.actor = ?2))", SecondarySocialRelation.class);
        query.setParameter(1, id);
        query.setParameter(2, this.id);
        query.setParameter(3, SecondarySocialRelation.Action.BOOKMARKED);
        query.setParameter(4, type);
        SecondarySocialRelation sr= (SecondarySocialRelation) query.getSingleResult();
        sr.delete();
        
        return 1;
    }
    
    public int leaveCommunity(Community community) {
        Query query = JPA.em().createQuery(
                "SELECT sr FROM SocialRelation sr where sr.actionType=?1 And  sr.action = ?4 And " +
                " sr.actor = ?2 and sr.target = ?3", SocialRelation.class);
        query.setParameter(1, SocialRelation.ActionType.GRANT);
        query.setParameter(2, this.id);
        query.setParameter(3, community.id);
        query.setParameter(4, SocialRelation.Action.MEMBER);
        
        SocialRelation sr= (SocialRelation) query.getSingleResult();
        query = JPA.em().createQuery("DELETE Notification n where socialAction =?1");
        query.setParameter(1, sr);
        query.executeUpdate();

        // delete SocialRelation
        sr.delete();

        // remove community affinity
        UserCommunityAffinity.onLeftCommunity(this.id, community.id);

        return 1;
    }
    
    public List<Post> getMyUpdates(Long timestamp) {
        Query query = JPA.em().createQuery(
                "SELECT p from Post p where p.community in (select sr.target from SocialRelation sr " + 
                "where sr.actor=?1 and sr.action = ?2) and p.deleted = false order by p.auditFields.createdDate desc");
        query.setParameter(1, this.id);
        query.setParameter(2, SocialRelation.Action.MEMBER);
        query.setFirstResult(0);
        query.setMaxResults(DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        return (List<Post>)query.getResultList();
    }
    
    public long getQnABookmarkCount() {
        return getBookmarkCount(SocialObjectType.QUESTION);
    }
    
    public long getPostBookmarkCount() {
        return getBookmarkCount(SocialObjectType.POST);
    }

    public long getArticleBookmarkCount() {
        return getBookmarkCount(SocialObjectType.ARTICLE);
    }
    
    private long getBookmarkCount(SocialObjectType socialObjectType) {
        Query query = JPA.em().createQuery(
                "select count(sr.target) from SecondarySocialRelation sr where sr.action = ?1 and sr.actor = ?2 and sr.targetType = ?3)");
        query.setParameter(1, SecondarySocialRelation.Action.BOOKMARKED);
        query.setParameter(2, this.id);
        query.setParameter(3, socialObjectType);
        return (Long) query.getSingleResult();
    }
    
    public List<Post> getBookmarkedPosts(int offset, int limit) {
        Query query = JPA.em().createQuery(
                "Select p from Post p where p.id in " + 
                "(select sr.target from SecondarySocialRelation sr " + 
                "where sr.action = ?1 and sr.actor = ?2 and sr.targetType in ( ?3, ?4 )) and p.deleted = false order by p.socialUpdatedDate desc");
        query.setParameter(1, SecondarySocialRelation.Action.BOOKMARKED);
        query.setParameter(2, this.id);
        query.setParameter(3, SocialObjectType.POST);
        query.setParameter(4, SocialObjectType.QUESTION);
        query.setFirstResult(offset * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        query.setMaxResults(limit);

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+id+"] getBookmarkedPosts(offset="+offset+", limit="+limit+") - ret="+query.getResultList().size());
        }
        return (List<Post>)query.getResultList();
    }
    
    public List<Article> getBookmarkedArticles(int offset, int limit) {
        Query query = JPA.em().createQuery(
                "Select a from Article a where a.id in " + 
                "(select sr.target from  SecondarySocialRelation sr " + 
                "where sr.action = ?1 and sr.actor = ?2 and sr.targetType = ?3) and a.deleted = false order by a.publishedDate,a.id desc");  
        query.setParameter(1, SecondarySocialRelation.Action.BOOKMARKED);
        query.setParameter(2, this.id);
        query.setParameter(3, SocialObjectType.ARTICLE);
        query.setFirstResult(offset * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        query.setMaxResults(limit);

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+id+"] getBookmarkedArticles(offset="+offset+", limit="+limit+") - ret="+query.getResultList().size());
        }
        return (List<Article>)query.getResultList();
    }
    
    public List<Post> getNewsfeedsAtHomePage(int offset, int limit) {
        final NanoSecondStopWatch sw = new NanoSecondStopWatch();

        List<String> ids = FeedProcessor.getUserFeedIds(this, offset, limit);
        if (ids == null || ids.size() == 0) {
            return null;
        }
        sw.stop();

        final NanoSecondStopWatch sw2 = new NanoSecondStopWatch();

        String idsStr = ids.toString();
        String idsForIn = idsStr.substring(1, idsStr.length() - 1);
        Query query = JPA.em().createQuery(
                "SELECT p from Post p where p.id in (" + idsForIn + ") and p.deleted = false order by FIELD(p.id," + idsForIn + ")");
        List<Post> results = (List<Post>)query.getResultList();
        sw2.stop();

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+getId()+"] getNewsfeedsAtHomePage(offset="+offset+",limit="+limit+") "+
                    "Redis took "+sw.getElapsedMS()+"ms, DB took "+sw2.getElapsedMS()+"ms");
        }
        return results;
    }
    
    @JsonIgnore
    public List<Post> getUserNewsfeeds(int offset, int limit) {
        Query query = JPA.em().createQuery(
                "SELECT p from Post p where p.owner = ?2 and p.deleted = false order by p.socialUpdatedDate desc");
                query.setParameter(2, this);
                query.setFirstResult(offset * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
                query.setMaxResults(limit);
                return (List<Post>)query.getResultList();
    }
    
    
    @JsonIgnore
    public List<Post> getUserNewsfeedsComments(int offset, int limit) {
        Query query = JPA.em().createQuery(
                "SELECT p from Post p where p.id in ("+
                "select sr.target from  PrimarySocialRelation sr "+
                "where sr.actor = ?1 and ((sr.action = ?4 and sr.targetType = ?5) or "+
                "(sr.action = ?6 and sr.targetType = ?7))) and p.deleted = false order by p.socialUpdatedDate desc");
                query.setParameter(4, PrimarySocialRelation.Action.COMMENTED);
                query.setParameter(5, SocialObjectType.POST);
                query.setParameter(6, PrimarySocialRelation.Action.ANSWERED);
                query.setParameter(7, SocialObjectType.QUESTION);
                query.setParameter(1, this.id);
                query.setFirstResult(offset * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
                query.setMaxResults(limit);
                return (List<Post>)query.getResultList();
    }
    
    public List<Post> getMyLiveUpdates(Long timestamp) {
        Query query = JPA.em().createQuery(
                "SELECT p from Post p where p.community in (select sr.target " +
                "from SocialRelation sr where sr.actor=?1 and sr.action = ?2) and " + 
                (timestamp - 60) + " < UNIX_TIMESTAMP(p.auditFields.createdDate) and  " + 
                timestamp + " > UNIX_TIMESTAMP(p.auditFields.createdDate) and p.deleted = false order by p.auditFields.createdDate desc");
        query.setParameter(1, this.id);
        query.setParameter(2, SocialRelation.Action.MEMBER);
        query.setMaxResults(DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        return (List<Post>)query.getResultList();
    }

    public List<Post> getMyNextNewsFeeds(Long timestamp) {
        Query query = JPA.em().createQuery(
                "SELECT p from Post p where p.community in (select sr.target " +
                "from SocialRelation sr where sr.actor=?1 and sr.action = ?2) and " + 
                timestamp + " > UNIX_TIMESTAMP(p.auditFields.createdDate) and p.deleted = false order by p.auditFields.createdDate desc");
        query.setParameter(1, this.id);
        query.setParameter(2, SocialRelation.Action.MEMBER);
        query.setMaxResults(DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        return (List<Post>)query.getResultList();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<UserChild> getChildren() {
        return children;
    }

    public void setChildren(List<UserChild> children) {
        this.children = children;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

   public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }
        
    public List<LinkedAccount> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<LinkedAccount> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public Folder getAlbumPhotoProfile() {
        return albumPhotoProfile;
    }

    public void setAlbumPhotoProfile(Folder albumPhotoProfile) {
        this.albumPhotoProfile = albumPhotoProfile;
    }

    public Folder getAlbumCoverProfile() {
        return albumCoverProfile;
    }

    public void setAlbumCoverProfile(Folder albumCoverProfile) {
        this.albumCoverProfile = albumCoverProfile;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public List<Album> getAlbum() {
        return album;
    }

    public void setAlbum(List<Album> album) {
        this.album = album;
    }

    public List<Conversation> getConversation() {
        return conversation;
    }

    public void setConversation(List<Conversation> conversation) {
        this.conversation = conversation;
    }

    public void setRoles(List<SecurityRole> roles) {
        this.roles = roles;
    }

    public void setPermissions(List<UserPermission> permissions) {
        this.permissions = permissions;
    }
    
    public List<Conversation> findMyAllConversations() {
        return Conversation.findAllConversations(this,20);
    }
    
    public Conversation findMyConversationsWith(User u) {
        return Conversation.findBetween(this, u);
    }
    
    public List<Message> getMessageForConversation(Conversation conversation, Long offset) {
        return Message.findBetween(conversation, offset, this);
    }
    
    public void addMessageToConversation(Conversation conversation, String message) {
        conversation.addMessage(this, message);
    }

    public void startChat(User user2) {
        Conversation.startConversation(this, user2);
    }
    
    public List<User> searchUserFriends(String q) {
        Query query = JPA.em().createNativeQuery(
                "Select * from User u where u.id in " + 
                "(select sr.target from SocialRelation sr where sr.action = ?2 and sr.actor = ?1 union select sr1.actor from SocialRelation sr1 where sr1.action = ?2 and sr1.target = ?1 ) and u.emailValidated = true and u.system = 0 and u.userInfo_id is not NULL and ( upper(u.displayName) like '%"+q.toUpperCase()+"%' or upper(u.firstName) like '%"+q.toUpperCase()+"%' or upper(u.lastName) like '%"+q.toUpperCase()+"%') and u.deleted = false", User.class);
        query.setParameter(1, this.id);
        query.setParameter(2, SocialRelation.Action.FRIEND.name());
        List<User> frndList = (List<User>)query.getResultList();
        return frndList;
    }

    public Long getUnreadMsgCount() {
        Query q = JPA.em().createQuery(
                "Select count(c) from Conversation c where ( c.user1.id = ?1 and (c.user1_time < c.conv_time or c.user1_time is null)) or (c.user2.id = ?1 and (user2_time < c.conv_time or c.user2_time is null ))");
        q.setParameter(1, this.id);
        Long ret = (Long) q.getSingleResult();

        logger.underlyingLogger().info("[u=" + id + "] getUnreadMsgCount=" + ret);
        return ret;
    }
}
