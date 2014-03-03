package models;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import org.apache.commons.io.FileUtils;

import play.data.validation.Constraints.Required;
import domain.SocialObjectType;

/**
 * Represent a folder containins a set of Resources
 *
 */
@Entity
public class Folder extends SocialObject  {
 
		
	  public Folder() {}
	  @Required
	  public String name;
	  
	  @Lob
	  public String description;
	  
	  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "folder")
	  public Set<Resource> resources = new HashSet<Resource>();
	  
	  @Required
	  public Boolean system = false; //system albums not generate socialAction onCreate and should be always public (the privacy is setted on the single ineer elements)
	  
	  @Override
	  public String toString() {
	    return super.toString() + " " + name;
	  }

	  public Resource addFile(java.io.File source, String description, SocialObjectType type) throws IOException {
	    Resource resource = new Resource(type);
	    resource.resourceName = source.getName();
	    resource.description = description;
	    resource.folder = this;
	    resource.owner = this.owner;
        resource.save();

        FileUtils.copyFile(source, new java.io.File(resource.getPath()));
        
        this.resources.add(resource);
        merge();
        recordAddedPhoto(owner);
        return resource; 
	  }
	  
	  public Resource addFile(java.io.File file, SocialObjectType type) throws IOException {
	    return addFile(file, null,type);
	  }
	  
	  public Resource addExternalFile(URL url, String description, SocialObjectType type) throws IOException {
	    Resource file = new Resource(type);
	    file.resourceName = url.toString();
	    file.description = description;
	    file.folder = this;
	    file.owner = this.owner;
        this.resources.add(file);
	    return file; 
	  }
	  
	  public Resource addExternalFile(URL url, SocialObjectType type) throws IOException {
	    return addExternalFile(url, null,type);
	  }

	  public Boolean removeFile(Resource file) {
	    if(this.resources.contains(file)){
	      if(!file.isExtrenal()) {
	        if(!file.getRealFile().delete()) {
	          return false;
	        }
	      }
	      this.resources.remove(file);
	    }
	    return false;
	  }
	  

	  public void setHighPriorityFile(Resource high) {
	    int max = Integer.MIN_VALUE;
	    for (Resource file : resources) {
	      if(file.priority > max) {
	        max = file.priority;
	      }
	    }
	    high.priority = max + 1;
	    high.save();
	  }

	  public Resource getHighPriorityFile() {
	    int max = Integer.MIN_VALUE;
	    Resource highest = null;
	    for (Resource file : resources) {
	      if(file.priority > max) {
	        highest = file;
	        max = file.priority;
	      }
	    }
	    return highest;
	  }
	  
	  

  
}
