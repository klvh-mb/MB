package models;

import java.io.File;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Query;

import common.cache.ArticleCategoryCache;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Play;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * update articlecategory set description='育兒小百科',name='育兒小百科',seq=1 where id=2;
 * update articlecategory set description='小寶寶教養',name='小寶寶教養',seq=2 where id=4;
 * update articlecategory set description='親子好關係',name='親子好關係',seq=3 where id=3;
 * update articlecategory set description='健康靚媽咪',name='健康靚媽咪',seq=4 where id=5;
 * update articlecategory set description='準媽媽教室',name='準媽媽教室',seq=5 where id=1;
 * update articlecategory set description='開心小分享',name='開心小分享',seq=6 where id=6;
 */
@Entity
public class ArticleCategory  {

	private static String CATEGORY_PATH = Play.application().configuration().getString("storage.categoty.path");
	
	public static final ArticleCategoryGroup DEFAULT_CATEGORY_GROUP = ArticleCategoryGroup.HOT_ARTICLES;
	        
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
	
	@Lob
	public String description;
	
	public String pictureName;
	
	@Enumerated(EnumType.STRING)
	public ArticleCategoryGroup categoryGroup;
	
	public int seq;
	
	public boolean deleted = false;
	
    public static enum ArticleCategoryGroup {
        HOT_ARTICLES, 
        SOON_TO_BE_MOMS_ARTICLES,
        NEW_MOMS_ARTICLES
    }
    
	public ArticleCategory(){}
	
	public ArticleCategory(String name, String description, String pictureName, ArticleCategoryGroup categoryGroup, int seq){
		this.name = name;
		this.description = description;
		this.pictureName = pictureName;
		this.categoryGroup = categoryGroup;
		this.seq = seq;
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

    /**
     * Load from database. The rest should be from cache.
     * @return
     */
    public static List<ArticleCategory> loadAllCategories() {
		Query q = JPA.em().createQuery("Select a from ArticleCategory a where deleted = false order by seq");
		return (List<ArticleCategory>)q.getResultList();
    }

	public static List<ArticleCategory> getAllCategories() {
		return ArticleCategoryCache.getAllCategories();
	}

	public static List<ArticleCategory> getCategories(ArticleCategoryGroup categoryGroup) {
        return ArticleCategoryCache.getCategories(categoryGroup);
    }
	
	public static ArticleCategory getCategoryById(long id) {
        ArticleCategory cat = ArticleCategoryCache.getCategoryById(id);
		return cat;
	}
	
	public static ArticleCategoryGroup getCategoryGroup(long id) {
	    ArticleCategory cat = ArticleCategoryCache.getCategoryById(id);
	    if (cat == null) {
	        return DEFAULT_CATEGORY_GROUP;
	    }
	    return cat.categoryGroup;
	}
	
    @Override
    public String toString() {
        return "ArticleCategory{" +
                "name='" + name + '\'' +
                "categoryGroup='" + categoryGroup.name() + '\'' +
                '}';
    }
}
