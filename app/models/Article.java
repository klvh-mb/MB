package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Article extends domain.Entity {

	public Article() {}
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
	
	@Lob
	public String description;
	
	public Boolean isFeatured;
	
	public Integer targetAge;
	
	@ManyToOne
	public ArticleCategory category;
	
	public List<Article> all() {
		// TODO
		return null;
	}
	
	public Article findById() {
		// TODO
		return null;
	}
	
	public void saveArticle()
	{
		this.save();
	}
	
}
