package models;

import common.model.SchoolType;
import play.db.jpa.JPA;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 25/1/15
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class SchoolSaved extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(SchoolSaved.class);

    public static enum Status {
	    Watched, GotForm, Applied, Interviewed, Offered, WaitListed, Rejected
	}

    public static enum ClassSession {
        AM, PM, WD
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private long userId;

    private long schoolId;
    private SchoolType schoolType;
    private Status status;

    @Temporal(TemporalType.TIMESTAMP)
	private Date formStartDateTime;
    @Temporal(TemporalType.TIMESTAMP)
	private Date formEndDateTime;           // optional

    @Temporal(TemporalType.TIMESTAMP)
	private Date applicationStartDateTime;
    @Temporal(TemporalType.TIMESTAMP)
	private Date applicationEndDateTime;    // optional

    @Temporal(TemporalType.TIMESTAMP)
	private Date interviewDateTime;

    @Temporal(TemporalType.TIMESTAMP)
	private Date depositDateTime;
    private int depositFee;

    private ClassSession classSession;
    private int schoolFee;

    private String notes;

    // Ctor
    public SchoolSaved() {}

    public SchoolSaved(Long userId, Long schoolId, SchoolType schoolType) {
        this.userId = userId;
        this.schoolId = schoolId;
        this.schoolType = schoolType;
        this.status = Status.Watched;
    }


    ///////////////////// GET SQLs /////////////////////
    public static List<SchoolSaved> findByUserSchoolId(Long userId, Long schoolId, SchoolType type) {
        Query q = JPA.em().createQuery("SELECT ss FROM SchoolSaved ss where ss.userId=?1 and ss.schoolId=?2 and ss.schoolType=?3");
        q.setParameter(1, userId);
        q.setParameter(2, schoolId);
        q.setParameter(3, type);
        return (List<SchoolSaved>) q.getResultList();
    }
}
