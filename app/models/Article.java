package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

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
	
	@Transactional
	public static List<Article> getAllArticles() {
		Query q = JPA.em().createQuery("Select a from Article a order by CREATED_DATE desc");
		return (List<Article>)q.getResultList();
	}
	
	public static Article findById(Long id) {
		Query q = JPA.em().createQuery("SELECT u FROM Article u where id = ?1");
		q.setParameter(1, id);
		return (Article) q.getSingleResult();
	}
	
	public static int deleteByID(Long id) {
		Query q = JPA.em().createQuery("DELETE FROM Article u where id = ?1");
		q.setParameter(1, id);
		return q.executeUpdate();
	}
	
	public Article findById() {
		// TODO
		return null;
	}
	
	public void updateById()
	{
		this.merge();
	}
	public void saveArticle()
	{
		this.save();
	}
	
}
