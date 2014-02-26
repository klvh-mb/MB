package models;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;

/**
 * Represent a folder containins a set of Resources
 *
 */
@Entity
public class Folder extends SocialObject  {
 
  public static final String ANY = "any";
  public static final String IMG = "img";
  
  @Required
  public String name;
  
  @Lob
  public String description;
  
  @Required
  public String type;
  
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "folder")
  public Set<Resource> resources = new TreeSet<Resource>();
  
  @Required
  public Boolean system = false; //system albums not generate socialAction onCreate and should be always public (the privacy is setted on the single ineer elements)
  
  
  
  
  public void setHighPriorityFile(Resource high) {
    int max = Integer.MIN_VALUE;
    for (Resource file : resources) {
      if(file.priority > max) {
        max = file.priority;
      }
    }
    high.priority = max + 1;
    high.save();
  }

  public Resource getHighPriorityFile() {
    int max = Integer.MIN_VALUE;
    Resource highest = null;
    for (Resource file : resources) {
      if(file.priority > max) {
        highest = file;
        max = file.priority;
      }
    }
    return highest;
  }

  
  
}
