package models;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/9/14
 * Time: 11:53 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class CommunityCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	public String name;

    // TODO: need to revise
//    @ManyToMany
//    public List<Community> communities;


    public CommunityCategory() { }

    public CommunityCategory(String name) {
        this.name = name;
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
}
