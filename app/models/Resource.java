package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.apache.commons.io.FileUtils;

import play.Play;
import play.data.validation.Constraints.Required;
import domain.SocialObjectType;

/**
 * A resource can be a file or an external url,
 * is contained always in a Folder
 *
 */

@Entity
public class Resource extends SocialObject  {
	  
	
	  public Resource() {}
	  
	  @Required
	  @ManyToOne
	  public Folder folder;
	  
	  @Required 
	  public String resourceName;

	  @Lob
	  public String description;

	  @Required
	  public Integer priority = 0;
	  
	  public Resource(SocialObjectType objectType){
			this.objectType = objectType;
	  }
	  
	  
	  public Boolean isImage() {
	    return com.mnt.utils.FileUtils.isImage(resourceName);
	  }
	  
	  public Boolean isExtrenal() {
	    return com.mnt.utils.FileUtils.isExternal(resourceName);
	  }
	  
	  @Override
	  public String toString() {
	    return super.toString() + " " + resourceName;
	  }

	  
	  
	  public String getPath() {
	    if(isExtrenal()) {
	      return resourceName;
	    } else {
	      return Play.application().configuration().getString("storage.path") + owner.id + "/" + folder.id + "/" + id + "/" + resourceName;
	    }
	  }

	  public java.io.File getRealFile() {
	    java.io.File f = new java.io.File(getPath());
	    if(f.exists()) {
	      return f;
	    }
	    return null;
	  }
	  
	  public Long getSize(){
	    if(isExtrenal()) {
	      return null;
	    } else {
	      return FileUtils.sizeOf(getRealFile());
	    }
	  }
	 
	 
}
