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
import play.db.jpa.Transactional;

/*
 * No UI Crud operation for this model. this Model will be populated by Admin directly in DB.
 */
@Entity
public class ArticleCategory  {

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
	
	  @Transactional
	  public void save() {
		  JPA.em().persist(this);
		  JPA.em().flush();
		  
	  }
	  
	  @Transactional
	  public void delete() {
		  JPA.em().remove(this);
		  
	  }
	  
	  @Transactional
	  public void merge() {
		  JPA.em().merge(this);
		  
	  }
	  @Transactional
	  public void refresh() {
		  JPA.em().refresh(this);
		  
	  }
	
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
	
	public static List<ArticleCategory> getCategories(int limit) {
		Query q = JPA.em().createQuery("Select a from ArticleCategory a");
		q.setMaxResults(limit);
		return (List<ArticleCategory>)q.getResultList();
	}
}
