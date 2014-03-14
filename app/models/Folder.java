package models;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;


import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.google.common.base.Objects;

import play.data.validation.Constraints.Required;
import domain.SocialObjectType;

/**
 * Represent a folder containins a set of Resources
 * 
 */
@Entity
public class Folder extends SocialObject {

	public Folder() {}
	
	public Folder(String name) {
		this.name = name;
	}

	

	@Required
	public String name;

	@Lob
	public String description;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "folder")
	public List<Resource> resources = new ArrayList<Resource>();

	@Required
	public Boolean system = false; // system albums not generate socialAction
									// onCreate and should be always public (the
									// privacy is setted on the single ineer
									// elements)

	@Override
	public String toString() {
		return super.toString() + " " + name;
	}

	public Resource addFile(java.io.File source, String description,
			SocialObjectType type) throws IOException {
		
		
		Resource resource = new Resource(type);
		resource.resourceName = source.getName();
		resource.description = description;
		resource.folder = this;
		resource.owner = this.owner;
		resource.save();
		FileUtils.copyFile(source, new java.io.File(resource.getPath()));
		if(type == SocialObjectType.PHOTO) {
			Thumbnails.of(source).height(100).keepAspectRatio(true).toFiles( new java.io.File(resource.getPath()).getParentFile(), Rename.PREFIX_DOT_THUMBNAIL);
		}
		this.resources.add(resource);
		merge();
		recordAddedPhoto(owner);
		return resource;
	}

	public Resource addFile(java.io.File file, SocialObjectType type)
			throws IOException {
		return addFile(file, description, type);
	}

	public Resource addExternalFile(URL url, String description,
			SocialObjectType type) throws IOException {
		Resource file = new Resource(type);
		file.resourceName = url.toString();
		file.description = description;
		file.folder = this;
		file.owner = this.owner;
		this.resources.add(file);
		return file;
	}

	public Resource addExternalFile(URL url, SocialObjectType type)
			throws IOException {
		return addExternalFile(url, null, type);
	}

	public void removeFile(Resource resource) {
		File file = new File(resource.getPath());
		try {
			FileUtils.cleanDirectory(file.getParentFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		this.resources.remove(resource);
	}

	public void setHighPriorityFile(Resource high) {
		int max = Integer.MIN_VALUE;
		for (Resource file : resources) {
			if (file.priority > max) {
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
			if (file.priority > max) {
				highest = file;
				max = file.priority;
			}
		}
		return highest;
	}
	
	@Override
	public int hashCode(){
	    return Objects.hashCode(name);
	}
	
	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof SocialObject){
	        final SocialObject other = (SocialObject) obj;
	        return new EqualsBuilder()
	            .append(name, other.name)
	            .isEquals();
	    } else{
	        return false;
	    }
	}

}
