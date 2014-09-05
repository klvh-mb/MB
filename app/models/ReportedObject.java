package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import play.data.DynamicForm;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import domain.SocialObjectType;

@Entity
public class ReportedObject {

	public ReportedObject() {
		this.reportedDate = new Date();
	}
	
	

	public ReportedObject(DynamicForm form, Long userID) {
		// TODO Auto-generated constructor stub
		this();
		 String socialObjectID = form.get("socialObjectID");
	        String objectType = form.get("objectType");
	        String category = form.get("category");
	        String comment = form.get("comment");
	        System.out.println("objectType :::::: "+objectType);
	        
	        this.socialObjectID = Long.parseLong(socialObjectID);
	        this.Comment = comment;
	        this.setObjectType(objectType);
	        this.setCategory(category);
	        this.reportedBy = userID;
	        this.save();
	}



	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Enumerated(EnumType.STRING)
	public SocialObjectType objectType;
	
	public Long socialObjectID;

	public String Comment;
	
	public enum categoryType{
		SPAM,
		UNATHORIZED_AD,
		INAPPROPRIATE;
	}
	
	@Enumerated(EnumType.STRING)
	public categoryType category;
	
	public Long reportedBy;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SocialObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		switch(objectType) {
		case "POST":
			this.objectType = SocialObjectType.POST;
			break;
		case "COMMUNITY":
			this.objectType = SocialObjectType.COMMUNITY;
			break;
		case "USER":
			this.objectType = SocialObjectType.USER;
			break;
		case "COMMENT":
			this.objectType = SocialObjectType.COMMENT;
			break;
		case "QUESTION":
			this.objectType = SocialObjectType.QUESTION;
			break;
		case "ANSWER":
			this.objectType = SocialObjectType.ANSWER;
			break;
		}
			
	}

	public Long getSocialObjectID() {
		return socialObjectID;
	}

	public void setSocialObjectID(Long socialObjectID) {
		this.socialObjectID = socialObjectID;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public categoryType getCategory() {
		return category;
	}

	public void setCategory(String category) {
		switch(category){
		case "INAPPROPRIATE":
			this.category = categoryType.INAPPROPRIATE;
			break;
		case "SPAM":
			this.category = categoryType.SPAM;
			break;
		case "UNATHORIZED_AD":
			this.category = categoryType.UNATHORIZED_AD;
			break;
		}
		
	}

	public Date getReportedDate() {
		return reportedDate;
	}

	public void setReportedDate(Date reportedDate) {
		this.reportedDate = reportedDate;
	}

	public Long getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(Long reportedBy) {
		this.reportedBy = reportedBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date reportedDate;

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
