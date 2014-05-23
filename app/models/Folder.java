package models;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.base.Objects;

import play.data.validation.Constraints.Required;
import domain.AuditListener;
import domain.Creatable;
import domain.SocialObjectType;
import domain.Updatable;

/**
 * Represent a folder containins a set of Resources
 * 
 */
@Entity

public class Folder extends  SocialObject implements
Serializable, Creatable, Updatable{

	public Folder() {}
	
	
	
	public Folder(String name) {
		this.name = name;
	}


	
	
	

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
		if (type == SocialObjectType.PROFILE_PHOTO) {
			Thumbnails
					.of(source)
					.height(70)
					.width(70)
					.keepAspectRatio(true)
					.toFiles(
							new java.io.File(resource.getPath())
									.getParentFile(),
							Rename.PREFIX_DOT_THUMBNAIL);
			
			Thumbnails
					.of(source)
					.height(40)
					.width(40)
					.keepAspectRatio(true)
					.toFile(new java.io.File(resource.getPath()).getParentFile()
									+"/mini."+new java.io.File(resource.getPath()).getName());
			
			Thumbnails
					.of(source)
					.height(32)
					.width(32)
					.keepAspectRatio(true)
					.toFile(new java.io.File(resource.getPath()).getParentFile()
							+"/miniComment."+new java.io.File(resource.getPath()).getName());
		}
		if (type == SocialObjectType.COVER_PHOTO) {
			Thumbnails
					.of(source)
					.height(216)
					.width(94)
					.keepAspectRatio(true)
					.toFiles(
							new java.io.File(resource.getPath())
									.getParentFile(),
							Rename.PREFIX_DOT_THUMBNAIL);
			
			Thumbnails
					.of(source)
					.height(102)
					.width(45)
					.keepAspectRatio(true)
					.toFile(new java.io.File(resource.getPath()).getParentFile()
									+"/mini."+new java.io.File(resource.getPath()).getName());
		}
		this.resources.add(resource);
		merge();
		//recordAddedPhoto(owner);
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
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof SocialObject) {
			final SocialObject other = (SocialObject) obj;
			return new EqualsBuilder().append(name, other.name).isEquals();
		} else {
			return false;
		}
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public List<Resource> getResources() {
		return resources;
	}



	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}



	public Boolean getSystem() {
		return system;
	}



	public void setSystem(Boolean system) {
		this.system = system;
	}

}
