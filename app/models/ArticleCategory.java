package models;

import java.io.File;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Play;
import play.db.jpa.JPA;

/*
 * No UI Crud operation for this model. this Model will be populated by Admin directly in DB.
 */
@Entity
public class ArticleCategory extends domain.Entity {

	private static String CATEGORY_PATH = Play.application().configuration().getString("storage.categoty.path");
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
	
	@Lob
	public String description;
	
	public String pictureName;
	
	public ArticleCategory(){}
	
	public ArticleCategory( String name, String description, String pictureName){
		this.name = name;
		this.description = description;
		this.pictureName = pictureName;
	}
	
	// TODO: Add more Attributes when required. 
	
	@JsonIgnore
	public File getPicture() {
		File file  = new File(CATEGORY_PATH + this.pictureName);
		
		if(file.exists()) {
			return file;
		}
		return null;
	}
	
	public static List<ArticleCategory> getAllCategory() {
		Query q = JPA.em().createQuery("Select a from ArticleCategory a");
		return (List<ArticleCategory>)q.getResultList();
	}
	
	public static ArticleCategory getCategoryById(long id) {
		Query q = JPA.em().createQuery("Select a from ArticleCategory a where id = ?1");
		q.setParameter(1, id);
		return (ArticleCategory)q.getSingleResult();
	}
}
