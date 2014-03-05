package domain;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverrides;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@MappedSuperclass
public class Entity
{
  @Embedded
  @AttributeOverrides({@javax.persistence.AttributeOverride(name="createdBy", column=@javax.persistence.Column(name="CREATED_BY")), @javax.persistence.AttributeOverride(name="createdDate", column=@javax.persistence.Column(name="CREATED_DATE")), @javax.persistence.AttributeOverride(name="updatedBy", column=@javax.persistence.Column(name="UPDATED_BY")), @javax.persistence.AttributeOverride(name="updatedDate", column=@javax.persistence.Column(name="UPDATED_DATE"))})
  private AuditFields auditFields;
  
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
  
  public void postSave() {
	  
  }
  
 
  
}