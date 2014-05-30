package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserChild {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	public String name;
}
