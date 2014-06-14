package models;

import javax.persistence.*;

import play.db.jpa.JPA;

import common.model.TargetGender;

@Entity
public class UserChild {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

    @Enumerated(EnumType.STRING)
    public TargetGender gender;
    
    public String birthYear;
    
    public String birthMonth;
    
    public String birthDay;
    
    @OneToOne
    public User user;
    
    public void save() {
		JPA.em().persist(this);
		JPA.em().flush();
	}
}
