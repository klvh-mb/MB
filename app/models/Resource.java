package models;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.Play;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import domain.SocialObjectType;

/**
 * A resource can be a file or an external url, is contained always in a Folder
 * 
 */

@Entity
public class Resource extends SocialObject {

    public static final String STORAGE_PATH = 
            Play.application().configuration().getString("storage.path");
    public static final int STORAGE_PARTITION_DIR_MAX = 
            Play.application().configuration().getInt("storage.partition.dir.max", 20000);
    
	public Resource() {
	}
	
	@JsonIgnore
	@Required
	@ManyToOne
	public Folder folder;

	@Required
	public String resourceName;

	@Lob
	public String description;

	@Required
	public Integer priority = 0;

	@OneToMany(cascade = CascadeType.REMOVE)
	public Set<Comment> comments;

	public Resource(SocialObjectType objectType) {
		this.objectType = objectType;
	}

	public Boolean isImage() {
		return com.mnt.utils.FileUtils.isImage(resourceName);
	}

	public Boolean isExternal() {
		return com.mnt.utils.FileUtils.isExternal(resourceName);
	}

	@Override
	public String toString() {
		return super.toString() + " " + resourceName;
	}

	public String getPath() {
		return getStoragePath("");
	}

	public String getThumbnail() {
        return getStoragePath("thumbnail.");
	}

	public String getMini() {
        return getStoragePath("mini.");
	}
	
	public String getMiniComment() {
        return getStoragePath("miniComment.");
	}

	public java.io.File getRealFile() {
        return getFileObject(getPath());
	}

    public java.io.File getThumbnailFile() {
        return getFileObject(getThumbnail());
	}

	public Long getSize() {
		if (isExternal()) {
			return null;
		} else {
			return FileUtils.sizeOf(getRealFile());
		}
	}

    private String getStoragePath(String filePrefix) {
		if (isExternal()) {
			return resourceName;
		} else {
            if (filePrefix == null) {
                filePrefix = "";
            }
			return STORAGE_PATH+getStoragePartition()+"/"+
                   owner.id+"/"+folder.id+"/"+id+ "/"+filePrefix+resourceName;
		}
    }

    private String getStoragePartition() {
        return "part"+(owner.id / STORAGE_PARTITION_DIR_MAX);
    }

    public static java.io.File getFileObject(String path) {
		java.io.File f = new java.io.File(path);
		if (f.exists()) {
			return f;
		}
		return null;
	}

    ///////////////////////// SQL Query /////////////////////////
	public static Resource findById(Long id) {
		Query q = JPA.em().createQuery("SELECT r FROM Resource r where id = ?1");
		q.setParameter(1, id);
		return (Resource) q.getSingleResult();
	}
	
	public static List<Resource> findAllResourceOfFolder(Long id) {
		Query q = JPA.em().createQuery("SELECT r FROM Resource r where folder.id = ?1");
		q.setParameter(1, id);
		return (List<Resource>) q.getResultList();
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}
}
