package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonIgnore;

import common.cache.IconCache;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class VotingQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    @Required
    @Column(length=500)
    public String title;
    
    @Required
    @Column(length=2000)
    public String description;
    
    @Required
    public int numAns;
    
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "question")
    public List<VotingAnswer> answers = new ArrayList<>();
    
    @Required
    public Boolean active = true;
    
    @Required
    public Boolean deleted = false;
    
    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    public Date createdDate = new Date();
    
    public VotingQuestion(){}
    
    public VotingQuestion(String title, String description, int numAns) {
        this.title = title;
        this.description = description;
        this.numAns = numAns;
    }
    
    public static List<Emoticon> loadEmoticons() {
        Query q = JPA.em().createQuery("Select i from Emoticon i order by seq");
        return (List<Emoticon>)q.getResultList();
    }
    
    public static List<Emoticon> getEmoticons() {
        return IconCache.getEmoticons();
    }
    
    public static String replace(String text) {
        if(text != null){
            for(Emoticon emoticon : Emoticon.getEmoticons()){
                text = text.replace(emoticon.code, String.format("<img class='emoticon' src='%s'>", emoticon.url));
            }
        }
        return text;
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
