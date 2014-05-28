package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.FetchType;

import models.Community.CommunityType;
import models.Notification.NotificationType;
import models.SocialRelation.Action;
import models.SocialRelation.ActionType;
import models.TokenAction.Type;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Play;
import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Content;
import processor.FeedProcessor;
import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;

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

import domain.CommentType;
import domain.PostType;
import domain.SocialObjectType;
import domain.Socializable;

@Entity
public class User extends SocialObject implements Subject, Socializable {

	public String firstName;
	public String lastName;
	public String displayName;
	public String username;
	public String email;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonIgnore
	public Date lastLogin;

	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date date_of_birth;

	public String gender;
	
	@Lob
	public String aboutMe;
	
	public String location;
	
	@JsonIgnore
	public boolean active;

	@JsonIgnore
	public boolean emailValidated;

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

	public User(String firstName, String lastName, String displayName,
			String username) {
		this();
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = displayName;
		this.name = firstName;
		this.username = username;
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

	// TODO: Write Test
	public SocialObject commentedOn(SocialObject target, String comment)
			throws SocialObjectNotCommentableException {

		return target.onComment(this, comment, CommentType.SIMPLE);
	}

	// TODO: Write Test
	public void answeredOn(SocialObject target, String comment)
			throws SocialObjectNotCommentableException {
		target.onComment(this, comment, CommentType.ANSWER);
	}

	public void requestedToJoin(SocialObject target)
			throws SocialObjectNotJoinableException {
		if(!this.isJoinRequestPendingFor((Community) target))
		target.onJoinRequest(this);
	}

	public void joinRequestAccepted(SocialObject target, User toBeMemeber)
			throws SocialObjectNotJoinableException {
		target.onJoinRequestAccepted(toBeMemeber);
	}

	public void inviteRequestAccepted(SocialObject target, User toBeMemeber)
			throws SocialObjectNotJoinableException {
		target.onInviteRequestAccepted(toBeMemeber);
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
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<SocialRelation> q = cb.createQuery(SocialRelation.class);
		Root<SocialRelation> c = q.from(SocialRelation.class);
		q.select(c);
		q.where(cb.and(
				cb.or(cb.equal(c.get("target"), this.id),
						cb.equal(c.get("actor"), this.id)),
				cb.equal(c.get("action"), SocialRelation.Action.FRIEND)));

		List<SocialRelation> result = JPA.em().createQuery(q).getResultList();
		List<User> frndList = new ArrayList<>();
		for (SocialRelation rslt : result) {
			if (rslt.actor.equals(this.id)) {
				frndList.add((User) rslt.getTargetObject(User.class));
			}
			else if (rslt.target.equals(this.id)) {
				frndList.add((User) rslt.getActorObject(User.class));
			}
		}
		return frndList;
	}
	
	@JsonIgnore
	public List<User> getSuggestedFriends() {
		Query q = JPA.em().createNativeQuery("Select * from User u where u.id not in (select sr.target from  SocialRelation sr where sr.action = ?2 or sr.actionType = ?3 and sr.actor = ?1 union select sr1.actor from  SocialRelation sr1 where sr1.action = ?2 or sr1.actionType = ?3 and sr1.target = ?1 union select user.id from user where user.id = ?1 )",User.class);
		q.setParameter(1, this.id);
		q.setParameter(2, SocialRelation.Action.FRIEND.name());
		q.setParameter(3, SocialRelation.ActionType.FRIEND_REQUESTED.name());
	    List<User> frndList = ( List<User> )q.getResultList();
		
		return frndList;
	}
	
	@JsonIgnore
	public Long _getFriendsCount() {
		Query query = JPA.em().createQuery("SELECT count(*) from SocialRelation where (target = ?1 or actor = ?1) and action = ?2");
		query.setParameter(1, this.id);
		query.setParameter(2, SocialRelation.Action.FRIEND);
		Long result = (Long) query.getSingleResult();
		return result;
	}
	
	@JsonIgnore
	public List<Community> getListOfJoinedCommunities() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<SocialRelation> q = cb.createQuery(SocialRelation.class);
		Root<SocialRelation> c = q.from(SocialRelation.class);
		q.select(c);
		q.where(cb.and(cb.equal(c.get("actor"), this.id),
				cb.equal(c.get("action"), Action.MEMBER)));

		List<SocialRelation> result = JPA.em().createQuery(q).getResultList();

		List<Community> communityList = new ArrayList<>();
		for (SocialRelation rslt : result) {
			if (rslt.actor.equals(this.id)
					&& rslt.targetType == SocialObjectType.COMMUNITY) {
				communityList.add((Community) rslt.getTargetObject(Community.class));
			}
		}
		return communityList;
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
				communityList.add((Community) rslt.getTargetObject(Community.class));
			}
		}
		return communityList;
	}

	@JsonIgnore
	public List<Community> getListOfNotJoinedCommunities() {
		
		Query q = JPA.em().createQuery("Select c from Community c where c.id not in (select sr.target from  SocialRelation sr where sr.action = ?2 and sr.actor = ?1)");
		q.setParameter(1, this.id);
		q.setParameter(2, Action.MEMBER);
		List<Community> communityList = q.getResultList();
		return communityList;
	}

	@JsonIgnore
	public List<Community> getListOfNotJoinedCommunities(int offset, int limit) {
		
		Query q = JPA.em().createQuery("Select c from Community c where c.id not in (select sr.target from  SocialRelation sr where sr.action = ?2 and sr.actor = ?1)");
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
		Predicate predicate = builder.or(
				builder.like(builder.upper(root.<String> get("displayName")), "%" + q.toUpperCase() + "%"),
				builder.like(builder.upper(root.<String> get("firstName")), "%" + q.toUpperCase() + "%"),
				builder.like(builder.upper(root.<String> get("lastName")), "%" + q.toUpperCase() + "%"));
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

	public static User searchUsername(String username) {
		CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		criteria.select(root);
		Predicate predicate = (builder.equal(root.get("username"), username));
		criteria.where(predicate);
		return JPA.em().createQuery(criteria).getSingleResult();
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

	public Resource setPhotoProfile(java.io.File file) throws IOException {
		ensureAlbumPhotoProfileExist();
		Resource newPhoto = this.albumPhotoProfile.addFile(file,
				SocialObjectType.PROFILE_PHOTO);
		this.albumPhotoProfile.setHighPriorityFile(newPhoto);
		newPhoto.save();
		return newPhoto;
	}
	
	public Resource setCoverPhoto(File source) throws IOException {
		ensureCoverPhotoProfileExist();
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
			this.albumPhotoProfile = createAlbum("profile",
					Messages.get("album.photo-profile.description"), true);
			this.merge();
		}
	}
	
	/**
	 * ensure the existence of the system folder: albumPhotoProfile
	 */
	private void ensureCoverPhotoProfileExist() {

		if (this.albumCoverProfile == null) {
			this.albumCoverProfile = createAlbum("cover",
					Messages.get("album.photo-cover.description"), true);
			this.merge();
		}
	}
	
	@Transactional
	public Community createCommunity(String name, String description, CommunityType type, String iconName){

		if(Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(description) || Strings.isNullOrEmpty(iconName) || type == null) {
			return null;
		}
		Community community = new Community(
				name, description, this, type);
		community.iconName = iconName;
		community.save();
		return community;
		
	}

	/**
	 * create a folder with the type: IMG (contain only image Resource types)
	 * 
	 * @param name
	 * @param description
	 * @param privacy
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
		Query q = JPA
				.em()
				.createQuery(
						"SELECT u FROM User u, IN (u.linkedAccounts) l where active = ?1 and l.providerUserId = ?2 and l.providerKey = ?3");
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

		Query q = JPA
				.em()
				.createQuery(
						"SELECT u FROM User u, IN (u.linkedAccounts) l where active = ?1 and email = ?2 and  l.providerKey = ?3");
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
		user.lastLogin = new Date();

		if (authUser instanceof EmailIdentity) {
			final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
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

		user.save();
		user.linkedAccounts = Collections.singletonList(LinkedAccount.create(
				authUser).addUser(user));
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
		Query q = JPA.em().createQuery(
				"SELECT u FROM User u where active = ?1 and email = ?2");
		q.setParameter(1, true);
		q.setParameter(2, email);
		return (User) q.getSingleResult();
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
		Query q = JPA.em().createQuery("SELECT u FROM User u where id = ?1");
		q.setParameter(1, id);
		return (User) q.getSingleResult();
	}

	public File getDefaultUserPhoto() throws FileNotFoundException {
		 return new File(Play.application().configuration().getString("storage.user.noimage"));
	}

	public File getDefaultCoverPhoto()  throws FileNotFoundException {
		 return new File(Play.application().configuration().getString("storage.user.cover.noimage"));
	}
	
	
	@JsonIgnore
	public List<Notification> getAllFriendRequestNotification() {
		
		Query q = JPA.em().createQuery(
						"SELECT n from Notification n where recipetent = ?1 and socialAction.actionType = ?2 " +
						"and readed = ?3 ");
		q.setParameter(1, this.id);
		q.setParameter(2, ActionType.FRIEND_REQUESTED);
		q.setParameter(3, false);
		List<Notification> notifications = q.getResultList();
		return notifications;
	}
	
	@JsonIgnore
	public List<Notification> getAllJoinRequestNotification() {
		
		Query q = JPA.em().createQuery(
						"SELECT n from Notification n where recipetent = ?1 and notificationType in (?2,?3,?4) " +
						" and readed = ?5 ");
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
		Query query = JPA.em().createQuery("SELECT count(*) from SocialRelation where ((target = ?1 and actor = ?2) or (actor = ?1 and target = ?2)) and action = ?3");
		query.setParameter(1, this.id);
		query.setParameter(2, localUser.id);
		query.setParameter(3, SocialRelation.Action.FRIEND);
		Long result = (Long) query.getSingleResult();
		return result == 1;
	}
	

	@JsonIgnore
	public boolean isMemberOf(Community community) {
		Query query = JPA.em().createQuery("SELECT count(*) from SocialRelation where actor = ?1 and target = ?2 and action = ?3");
		query.setParameter(1, this.id);
		query.setParameter(2, community.id);
		query.setParameter(3, SocialRelation.Action.MEMBER);
		Long result = (Long) query.getSingleResult();
		return result == 1;
	}
	
	@JsonIgnore
	public boolean isJoinRequestPendingFor(Community community) {
		Query query = JPA.em().createQuery("SELECT count(*) from SocialRelation where (actor = ?1 and target = ?2) " +
				"and actionType = ?3");
		query.setParameter(1, this.id);
		query.setParameter(2, community.id);
		query.setParameter(3, SocialRelation.ActionType.JOIN_REQUESTED);
		Long result = (Long) query.getSingleResult();
		return result == 1;
	}
	
	@JsonIgnore
	public boolean isFriendRequestPendingFor(User user) {
		Query query = JPA.em().createQuery("SELECT count(*) from SocialRelation where ((target = ?1 and actor = ?2) or (actor = ?1 and target = ?2)) " +
				"and actionType = ?3");
		query.setParameter(1, this.id);
		query.setParameter(2, user.id);
		query.setParameter(3, SocialRelation.ActionType.FRIEND_REQUESTED);
		Long result = (Long) query.getSingleResult();
		return result == 1;
	}
	
	public int doUnFriend(User toBeUnfriend) {
				
		Query query = JPA.em().createQuery("SELECT sr FROM SocialRelation sr " +
				" where sr.actionType=?1 And  sr.action = ?4 And " +
				" ((sr.target = ?2 and sr.actor = ?3) or (sr.actor = ?2 and sr.target = ?3))", SocialRelation.class
				);
		query.setParameter(1, SocialRelation.ActionType.GRANT);
		query.setParameter(2, this.id);
		query.setParameter(3, toBeUnfriend.id);
		query.setParameter(4, SocialRelation.Action.FRIEND);
		
		SocialRelation sr= (SocialRelation) query.getSingleResult();
		query = JPA.em().createQuery("DELETE  Notification n where socialAction =?1");
		query.setParameter(1, sr);
		query.executeUpdate();
		
		sr.remove();
		
		return 1;
	}
	
	public int doUnLike(Long id, SocialObjectType type) {
        
        Query query = JPA.em().createQuery("SELECT sr FROM SocialRelation sr " +
                        " where  sr.targetType = ?4 and sr.action = ?3 And " +
                        " ((sr.target = ?1 and sr.actor = ?2))", SocialRelation.class
                        );
        query.setParameter(1, id);
        query.setParameter(2, this.id);
        query.setParameter(3, SocialRelation.Action.LIKED);
        query.setParameter(4, type);
        
        SocialRelation sr= (SocialRelation) query.getSingleResult();
        
        sr.remove();
        
        return 1;
	}
	
	public int leaveCommunity(Community community) {
		Query query = JPA.em().createQuery("SELECT sr FROM SocialRelation sr " +
				" where sr.actionType=?1 And  sr.action = ?4 And " +
				" sr.actor = ?2 and sr.target = ?3", SocialRelation.class
				);
		query.setParameter(1, SocialRelation.ActionType.GRANT);
		query.setParameter(2, this.id);
		query.setParameter(3, community.id);
		query.setParameter(4, SocialRelation.Action.MEMBER);
		
		SocialRelation sr= (SocialRelation) query.getSingleResult();
		query = JPA.em().createQuery("DELETE  Notification n where socialAction =?1");
		query.setParameter(1, sr);
		query.executeUpdate();
		
		sr.remove();
		
		return 1;
	}
	
	public List<Post> getMyUpdates(Long timestamp) {
		Query query = JPA.em().createQuery("SELECT p from Post p where p.community in (select sr.target " +
				"from SocialRelation sr where sr.actor=?1 and sr.action = ?2) order by p.auditFields.createdDate desc");
		query.setParameter(1, this.id);
		query.setParameter(2, SocialRelation.Action.MEMBER);
		query.setFirstResult(0);
		query.setMaxResults(5);
		return (List<Post>)query.getResultList();
	}
	
	public List<Post> getNewsfeeds(int offset, int page) {
		List<String> ids = FeedProcessor.getUserFeedIds(this, offset, page);
		String idsForIn = ids.toString().replace("[", "").replace("]", "");
		Query query = JPA.em().createQuery("SELECT p from Post p where p.id in (" + idsForIn + ") order by p.auditFields.createdDate desc");
		if(ids.size()== 0){
			return null;
		}
		return (List<Post>)query.getResultList();
	}
	
	@JsonIgnore
	public List<Post> getUserNewsfeeds(int offset, int limit) {
		Query query = JPA.em().createQuery("Select p from Post p where p.id in (select sr.target from  SocialRelation sr where sr.action = ?1 and sr.actor = ?2)");	
		query.setParameter(1, SocialRelation.Action.POSTED);
		query.setParameter(2, this.id);
		System.out.println(limit+ " :: "+offset +":: (List<Post>)query.getResultList(); :: "+query.getResultList().size());
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		
		return (List<Post>)query.getResultList();
	}
	
	public List<Post> getMyLiveUpdates(Long timestamp) {
		Query query = JPA.em().createQuery("SELECT p from Post p where p.community in (select sr.target " +
				"from SocialRelation sr where sr.actor=?1 and sr.action = ?2) and "+(timestamp-60)+" < UNIX_TIMESTAMP(p.auditFields.createdDate) and  "+ timestamp+" > UNIX_TIMESTAMP(p.auditFields.createdDate) order by p.auditFields.createdDate desc");
		query.setParameter(1, this.id);
		query.setParameter(2, SocialRelation.Action.MEMBER);
		query.setMaxResults(5);
		return (List<Post>)query.getResultList();
	}

	public List<Post> getMyNextNewsFeeds(Long timestamp) {
		Query query = JPA.em().createQuery("SELECT p from Post p where p.community in (select sr.target " +
				"from SocialRelation sr where sr.actor=?1 and sr.action = ?2) and  "+ timestamp+" > UNIX_TIMESTAMP(p.auditFields.createdDate) order by p.auditFields.createdDate desc");
		query.setParameter(1, this.id);
		query.setParameter(2, SocialRelation.Action.MEMBER);
		query.setMaxResults(5);
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public Date getDate_of_birth() {
		return date_of_birth;
	}

	public void setDate_of_birth(Date date_of_birth) {
		this.date_of_birth = date_of_birth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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
}
