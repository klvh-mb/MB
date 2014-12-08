package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.NonUniqueResultException;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class FeaturedTopic extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(FeaturedTopic.class);

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
	public String name;
	
	@Lob
	public String description;
	
	public String image;
	
	public String url;
	
	public int noClicks = 0;
	
	public Date publishedDate;

	public Boolean active = false;
	
    public Boolean deleted = false; 
    
    @Enumerated(EnumType.STRING)
	public FeaturedType featuredType;
	
	public static enum FeaturedType {  // FEATURED, PROMO, AD ??
        FEATURED
    }
	
	public FeaturedTopic() {}
	
	public static FeaturedTopic findById(Long id) {
		Query q = JPA.em().createQuery("SELECT f FROM FeaturedTopic f where id = ?1 and deleted = false");
		q.setParameter(1, id);
		return (FeaturedTopic) q.getSingleResult();
	}
	
	@Transactional
	public static FeaturedTopic getActiveFeaturedTopic(FeaturedType featuredType) {
        Query q = JPA.em().createQuery("SELECT f FROM FeaturedTopic f where featuredType = ?1 and active = true and deleted = false");
        q.setParameter(1, featuredType);
        try {
            return (FeaturedTopic) q.getSingleResult();
        } catch (NonUniqueResultException e) {
            return ((List<FeaturedTopic>) q.getResultList()).get(0);
        } catch (NoResultException e) {
            return null;
        }
    }
	
	@Override
    public boolean equals(Object o) {
        if (o != null && o instanceof FeaturedTopic) {
            final FeaturedTopic other = (FeaturedTopic) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
	
    @Override
    public String toString() {
        return "FeaturedTopic{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "clicks='" + noClicks + '\'' +
                "published='" + publishedDate + '\'' +
                '}';
    }
}
