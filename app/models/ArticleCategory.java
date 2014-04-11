package models;

import java.io.File;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Play;

/*
 * No UI Crud operation for this model. this Model will be populated by Admin directly in DB.
 */
@Entity
public class ArticleCategory {

	private static String CATEGORY_PATH = Play.application().configuration().getString("storage.categoty.path");
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
	
	@Lob
	public String description;
	
	public String pictureName;
	
	// TODO: Add more Attributes when required. 
	
	@JsonIgnore
	public File getPicture() {
		File file  = new File(CATEGORY_PATH + this.pictureName);
		
		if(file.exists()) {
			return file;
		}
		return null;
	}
	
	
	
}
