package models;

import play.data.format.Formats;

import javax.persistence.*;
import java.util.Date;

@Entity
public class UserChild {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

    @Column(nullable=false)
	public String gender;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date date_of_birth;
}
