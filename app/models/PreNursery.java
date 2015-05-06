package models;

import common.cache.CommunityMetaCache;
import common.model.SchoolType;
import domain.*;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 25/7/14
 * Time: 9:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class PreNursery extends SocialObject implements Likeable, Commentable {
    private static final play.api.Logger logger = play.api.Logger.apply(PreNursery.class);

    public Long communityId;
    public Long regionId;
    public Long districtId;

    // name is inherited
    public String nameEn;
    public String icon;

    public String phoneText;
    public String url;
    public String govUrl;
    public String govId;
    public String email;
    public String address;
    public String mapUrlSuffix;

    public String organization;
    public String organizationType;
    public boolean couponSupport = false;
    public String classTimes;       // comma separated (AM,PM,WD)
    @Column(length = 1024)
    public String curriculum;
    public String curriculumType;

    private String annualFeeHD;
    private String annualFeeWD;
    private String numAdmitted;

    // stats
    public int noOfPosts = 0;
    public int noOfLikes = 0;
    public int noOfViews = 0;
    public int noOfBookmarks = 0;

    // date details
    public String applicationDateText = null;

    // Ctor
    public PreNursery() {
        this.objectType = SocialObjectType.PRE_NURSERY;
    }


   ///////////////////// onSocialActions /////////////////////
    @Override
    public void onLikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][pn="+this.id+"] PreNursery onLikedBy");
        }
        recordLike(user);
        this.noOfLikes++;
        user.likesCount++;
    }

    @Override
    public void onUnlikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][pn="+this.id+"] PreNursery onUnlikedBy");
        }
        this.noOfLikes--;
        user.likesCount--;
    }

    @Transactional
    public void onBookmarkedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][pn="+this.id+"] PreNursery onBookmarkedBy");
        }
        // 1) school saved list
        SchoolSaved saved = new SchoolSaved(user.getId(), this.id, SchoolType.PN);
        saved.save();
        // 2) join PN community
        if (communityId != null) {
            try {
                Community pnComm = Community.findById(communityId);
                pnComm.onJoinRequest(user);
            } catch (Exception e) {
                logger.underlyingLogger().error("Error joining PN community: "+communityId, e);
            }
        }
        // 3) record bookmark
        recordBookmark(user);
        this.noOfBookmarks++;
    }

    @Transactional
    public void onUnBookmarkedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][pn="+this.id+"] PreNursery onBookmarkedBy");
        }
        // 1) remove from school saved list
        List<SchoolSaved> savedList = SchoolSaved.findByUserSchoolId(user.getId(), this.id, SchoolType.PN);
        if (savedList != null) {
            for (SchoolSaved saved : savedList) {
                saved.delete();
            }
        }
        // 2) leave PN community
        if (communityId != null) {
            boolean leaveComm = true;

            Long kgId = CommunityMetaCache.getKGIdFromCommunity(communityId);
            if (kgId != null) {
                leaveComm = SchoolSaved.findByUserSchoolId(user.getId(), kgId, SchoolType.KINDY).isEmpty();
            }
            if (leaveComm) {
                try {
                    Community pnComm = Community.findById(communityId);
                    user.leaveCommunity(pnComm);
                } catch (Exception e) {
                    logger.underlyingLogger().error("Error un-joining PN community: "+communityId, e);
                }
            }
        }
        // 3) remove bookmark record
        user.unBookmarkOn(this.id, this.objectType);
        this.noOfBookmarks--;
    }

    ///////////////////// GET SQLs /////////////////////
    public static PreNursery findById(Long id) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.id=?1");
        q.setParameter(1, id);
        try {
            return (PreNursery) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<PreNursery> findAll() {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn");
        return (List<PreNursery>)q.getResultList();
    }

    public static PreNursery findByNameDistrictId(String pnName, Long districtId) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.name=?1 and pn.districtId=?2");
        q.setParameter(1, pnName);
        q.setParameter(2, districtId);
        try {
            return (PreNursery) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static PreNursery findBy(String name, String nameEn, String address) {
        Query q = (address == null) ?
            JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.name=?1 and pn.nameEn=?2 and pn.address is null") :
            JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.name=?1 and pn.nameEn=?2 and pn.address=?3");
        q.setParameter(1, name);
        q.setParameter(2, nameEn);
        if (address != null) {
            q.setParameter(3, address);
        }
        try {
            return (PreNursery) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static Long searchByNameCount(String nameSubStr) {
        Query q = JPA.em().createQuery("SELECT count(pn) FROM PreNursery pn where pn.name like ?1 or UPPER(pn.nameEn) like ?2");
        q.setParameter(1, "%"+nameSubStr+"%");
        q.setParameter(2, "%"+nameSubStr.toUpperCase()+"%");
        return (Long)q.getSingleResult();
    }
    
    public static List<PreNursery> searchByName(String nameSubStr) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.name like ?1 or UPPER(pn.nameEn) like ?2");
        q.setParameter(1, "%"+nameSubStr+"%");
        q.setParameter(2, "%"+nameSubStr.toUpperCase()+"%");
        return (List<PreNursery>)q.getResultList();
    }

    public static List<PreNursery> getPNsByRegion(Long regionId) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.regionId = ?1 order by pn.districtId, pn.name");
        q.setParameter(1, regionId);
        return (List<PreNursery>)q.getResultList();
    }

    public static List<PreNursery> getPNsByDistrict(Long districtId) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.districtId = ?1 order by pn.name");
        q.setParameter(1, districtId);
        return (List<PreNursery>)q.getResultList();
    }

    public static List<String> getOrganizationsByDistrict(Long districtId) {
        Query q = JPA.em().createQuery("SELECT distinct pn.organization FROM PreNursery pn where pn.districtId=?1 and pn.organization is NOT NULL");
        q.setParameter(1, districtId);
        return (List<String>)q.getResultList();
    }

    public static List<PreNursery> getTopViews(int num) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn order by pn.noOfViews desc");
        q.setMaxResults(num);
        return (List<PreNursery>)q.getResultList();
    }

    public static List<PreNursery> getTopDiscussed(int num) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn order by pn.noOfPosts desc");
        q.setMaxResults(num);
        return (List<PreNursery>)q.getResultList();
    }
    
    public static List<PreNursery> getTopBookmarked(int num) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn order by pn.noOfBookmarks desc");
        q.setMaxResults(num);
        return (List<PreNursery>)q.getResultList();
    }

    public static List<PreNursery> getBookmarked(Long userId) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn, SchoolSaved ss where ss.schoolType=?1 and ss.userId=?2 " +
                "and ss.schoolId = pn.id");
        q.setParameter(1, SchoolType.PN);
        q.setParameter(2, userId);
        return (List<PreNursery>)q.getResultList();
    }

    public static List<PreNursery> getFormReceived(Long userId) {
        return getBySavedStatus(userId, SchoolSaved.Status.GotForm);
    }

    public static List<PreNursery> getApplied(Long userId) {
        return getBySavedStatus(userId, SchoolSaved.Status.Applied);
    }

    public static List<PreNursery> getInterviewed(Long userId) {
        return getBySavedStatus(userId, SchoolSaved.Status.Interviewed);
    }

    public static List<PreNursery> getOffered(Long userId) {
        return getBySavedStatus(userId, SchoolSaved.Status.Offered);
    }


    private static List<PreNursery> getBySavedStatus(Long userId, SchoolSaved.Status status) {
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn, SchoolSaved sr where sr.userId=?1 and sr.schoolType=?2 " +
                "and sr.status=?3 and sr.schoolId = pn.id");
        q.setParameter(1, userId);
        q.setParameter(2, SchoolType.PN);
        q.setParameter(3, status);
        return (List<PreNursery>)q.getResultList();
    }

    ///////////////////// Getters /////////////////////
    public String getAnnualFeeHD() {
        return annualFeeHD;
    }

    public String getAnnualFeeWD() {
        return annualFeeWD;
    }

    public String getNumAdmitted() {
        return numAdmitted;
    }
}
