package models;

import domain.Commentable;
import domain.Likeable;
import domain.SocialObjectType;
import play.db.jpa.JPA;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 10/5/15
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class PlayRoom extends SocialObject implements Likeable, Commentable {
    private static final play.api.Logger logger = play.api.Logger.apply(PlayRoom.class);

    public Long communityId;
    public Long regionId;
    public Long districtId;

    // name is inherited
    private String nameEn;
    private String icon;
    private String description;

    private String phoneText;
    private String url;
    private String email;
    private String address;
    private String mapUrlSuffix;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private Folder folder;             // album photos
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Folder directionPhotos;   // directions

    // stats
    public int noOfPosts = 0;
    public int noOfLikes = 0;
    public int noOfViews = 0;
    public int noOfBookmarks = 0;


    // Ctor
    public PlayRoom() {
        this.objectType = SocialObjectType.PLAYROOM;
    }


    public Resource addPhotoToAlbum(File source) throws IOException {
        //ensureAlbumExist();
        Resource photo = this.folder.addFile(source,
                SocialObjectType.POST_PHOTO);
        photo.save();
        return photo;
    }

    public Resource addDirectionPhoto(File source) throws IOException {
        //ensureAlbumExist();
        Resource photo = this.directionPhotos.addFile(source,
                SocialObjectType.POST_PHOTO);
        photo.save();
        return photo;
    }

    ///////////////////// GET SQLs /////////////////////
    public static PlayRoom findById(Long id) {
        Query q = JPA.em().createQuery("SELECT p FROM PlayRoom pn where p.id=?1");
        q.setParameter(1, id);
        try {
            return (PlayRoom) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<PlayRoom> searchByName(String nameSubStr) {
        Query q = JPA.em().createQuery("SELECT p FROM PlayRoom p where p.name like ?1 or UPPER(p.nameEn) like ?2");
        q.setParameter(1, "%"+nameSubStr+"%");
        q.setParameter(2, "%"+nameSubStr.toUpperCase()+"%");
        return (List<PlayRoom>)q.getResultList();
    }

    public static List<PlayRoom> getPNsByDistrict(Long districtId) {
        Query q = JPA.em().createQuery("SELECT p FROM PlayRoom p where p.districtId = ?1 order by p.name");
        q.setParameter(1, districtId);
        return (List<PlayRoom>)q.getResultList();
    }

    ///////////////////// Getters /////////////////////
    public String getNameEn() {
        return nameEn;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoneText() {
        return phoneText;
    }

    public String getUrl() {
        return url;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getMapUrlSuffix() {
        return mapUrlSuffix;
    }

    public Folder getFolder() {
        return folder;
    }

    public Folder getDirectionPhotos() {
        return directionPhotos;
    }
}
