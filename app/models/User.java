package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import models.TokenAction.Type;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;

import models.LinkedAccount;
import models.SecurityRole;
import models.SocialRelation.Action;
import models.TokenAction;

import models.UserPermission;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.FirstLastNameIdentity;
import com.feth.play.module.pa.user.NameIdentity;
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
	public Date lastLogin;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date date_of_birth;

	public String gender;
	
	@Lob
	public String aboutMe;
	
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

	@OneToMany
	public Set<User> friends = new HashSet<User>();
	
	@Override
	@JsonIgnore
	public String getIdentifier() {
		return Long.toString(id);
	}
	
	@OneToMany(cascade=CascadeType.REMOVE)
	public List<Folder> album;

	@OneToMany
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
		target.onLike(this);
	}

	public SocialObject postedOn(SocialObject target, String post)
			throws SocialObjectNotPostableException {
		return target.onPost(this, post, PostType.SIMPLE);
	}

	public SocialObject questionedOn(SocialObject target, String question)
			throws SocialObjectNotPostableException {
		return target.onPost(this, question, PostType.QUESTION);
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
	public Comment commentedOn(SocialObject target, String comment)
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
		target.onJoinRequest(this);
	}

	public void joinRequestAccepted(SocialObject target, User toBeMemeber)
			throws SocialObjectNotJoinableException {
		target.onJoinRequestAccepted(toBeMemeber);
	}

	public void markNotificationRead(Notification notification) {
		notification.markNotificationRead();
	}
	
	@Override
	public void onFriendRequest(User user)
			throws SocialObjectNotJoinableException {
		recordFriendRequest(user);
	}

	@Override
	@Transactional
	public void onFriendRequestAccepted(User user)
			throws SocialObjectNotJoinableException {
		this.friends.add(user);
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

	
	

	public static List<User> searchLike(String q) {
		CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		criteria.select(root);
		Predicate predicate = builder.or(
				builder.like(root.<String> get("displayName"), "%" + q + "%"),
				builder.like(root.<String> get("firstName"), "%" + q + "%"),
				builder.like(root.<String> get("lastName"), "%" + q + "%"));
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
				SocialObjectType.PHOTO);
		this.albumPhotoProfile.setHighPriorityFile(newPhoto);
		newPhoto.save();
		return newPhoto;
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
		 
		  if(ensureFolderExistWithGivenName(name)) {
			  Folder folder =  createFolder(name, description, SocialObjectType.FOLDER,system);
			  album.add(folder);
			  this.merge(); // Add folder to existing User as new albumn
			  return folder;
		  }
	    return null; // folder canot be added and same name folder already added before
	  }
	  
	  private Folder createFolder(String name, String description, SocialObjectType type,  Boolean system) {

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

		  if(album != null && album.contains(new Folder(name))) {
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
		return exp.getMaxResults() > 0;
	}

	private static Query getAuthUserFind(final AuthUserIdentity identity) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT u FROM User u where active = ?1 and linkedAccounts.providerUserId = ?2 and u.linkedAccounts.providerKey = ?3");
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
			return (User) getAuthUserFind(identity).getSingleResult();
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

}
