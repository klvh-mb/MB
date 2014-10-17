package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public User user;
    
    @OneToOne
    public VotingQuestion question;
    
    @OneToOne
    public VotingAnswer answer;
    
    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    public Date createdDate = new Date();

    public Vote(){}
    
    public Vote(User user, VotingQuestion question, VotingAnswer answer) {
        this.user = user;
        this.question = question;
        this.answer = answer;
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
