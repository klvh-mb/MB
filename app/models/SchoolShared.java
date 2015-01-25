package models;

import common.model.SchoolType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Capturing the sharing relationship between users.
 *
 * Created by IntelliJ IDEA.
 * Date: 25/1/15
 * Time: 7:08 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class SchoolShared extends domain.Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private long shareFromUserId;
    private long shareToUserId;         // read-only view

    private SchoolType schoolType;      // type of schools being shared

    // Ctor
    public SchoolShared() {}
}
