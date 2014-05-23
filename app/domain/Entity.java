package domain;

import java.util.Date;

import javax.persistence.AttributeOverrides;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@MappedSuperclass
public class Entity
{
  @Embedded
  @AttributeOverrides({@javax.persistence.AttributeOverride(name="createdBy", column=@javax.persistence.Column(name="CREATED_BY")), @javax.persistence.AttributeOverride(name="createdDate", column=@javax.persistence.Column(name="CREATED_DATE")), @javax.persistence.AttributeOverride(name="updatedBy", column=@javax.persistence.Column(name="UPDATED_BY")), @javax.persistence.AttributeOverride(name="updatedDate", column=@javax.persistence.Column(name="UPDATED_DATE"))})
  public AuditFields auditFields;
  
  public Entity()
  {
    this.auditFields = new AuditFields();
  }
  
  public void setCreatedBy(String createdBy)
  {
    this.auditFields.setCreatedBy(createdBy);
  }
  
  public void setCreatedDate(Date createdDate)
  {
    this.auditFields.setCreatedDate(createdDate);
  }
  
  public void setUpdatedBy(String updatedBy)
  {
    this.auditFields.setUpdatedBy(updatedBy);
  }
  
  public void setUpdatedDate(Date updatedDate)
  {
    this.auditFields.setUpdatedDate(updatedDate);
  }
  
  public Date getCreatedDate() {
	  return this.auditFields.getCreatedDate();
  }
  
  @Transactional
  public void save() {
	  JPA.em().persist(this);
	  JPA.em().flush();
	  postSave();
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
  
  @Transactional
  public void remove() {
	  JPA.em().remove(this);
	  
  }
  
  public void postSave() {
	  
  }
  
 
  
}