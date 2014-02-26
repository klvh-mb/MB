package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;

/**
 * A resource can be a file or an external url,
 * is contained always in a Folder
 *
 */

@Entity
public class Resource extends SocialObject  {
  
  @Required
  @ManyToOne
  public Folder folder;
  
  @Required
  public String resourceName;

  @Lob
  public String description;

  @Required
  public Integer priority = 0;
  
  
}
