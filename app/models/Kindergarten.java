package models;

import common.cache.CommunityMetaCache;
import common.model.SchoolType;
import domain.Commentable;
import domain.Likeable;
import domain.SocialObjectType;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Query;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 22/3/15
 * Time: 10:19 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Kindergarten extends SocialObject implements Likeable, Commentable {
    private static final play.api.Logger logger = play.api.Logger.apply(Kindergarten.class);

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
    public boolean hasPN = false;
    public boolean couponSupport = false;
    public String classTimes;       // comma separated (AM,PM,WD)
    @Column(length = 1024)
    public String curriculum;
    public String curriculumType;

    public String annualFeeAM_N;
    public String annualFeePM_N;
    public String annualFeeWD_N;
    public String annualFeeAM_LKG;
    public String annualFeePM_LKG;
    public String annualFeeWD_LKG;
    public String annualFeeAM_UKG;
    public String annualFeePM_UKG;
    public String annualFeeWD_UKG;

    public String numEnrollAM_N;
    public String numEnrollPM_N;
    public String numEnrollWD_N;
    public String numEnrollAM_LKG;
    public String numEnrollPM_LKG;
    public String numEnrollWD_LKG;
    public String numEnrollAM_UKG;
    public String numEnrollPM_UKG;
    public String numEnrollWD_UKG;

    private String summerUniformFee;
    private String winterUniformFee;
    private String schoolBagFee;
    private String teaAndSnacksFee;
    private String textBooksFee;
    private String workBooksFee;

    // stats
    public int noOfPosts = 0;
    public int noOfLikes = 0;
    public int noOfViews = 0;
    public int noOfBookmarks = 0;

    // date details
    public String applicationDateText = null;
    public String openDateText = null;
    
    // Ctor
    public Kindergarten() {
        this.objectType = SocialObjectType.KINDY;
    }

    ///////////////////// onSocialActions /////////////////////
    @Override
    public void onLikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][kg="+this.id+"] Kindergarten onLikedBy");
        }
        recordLike(user);
        this.noOfLikes++;
        user.likesCount++;
    }

    @Override
    public void onUnlikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][kg="+this.id+"] Kindergarten onUnlikedBy");
        }
        this.noOfLikes--;
        user.likesCount--;
    }

    @Transactional
    public void onBookmarkedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][kg="+this.id+"] Kindergarten onBookmarkedBy");
        }
        // 1) school saved list
        SchoolSaved saved = new SchoolSaved(user.getId(), this.id, SchoolType.KINDY);
        saved.save();
        // 2) join KG community
        if (communityId != null) {
            try {
                Community pnComm = Community.findById(communityId);
                pnComm.onJoinRequest(user);
            } catch (Exception e) {
                logger.underlyingLogger().error("Error joining KG community: "+communityId, e);
            }
        }
        // 3) record bookmark
        recordBookmark(user);
        this.noOfBookmarks++;
    }

    @Transactional
    public void onUnBookmarkedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][kg="+this.id+"] Kindergarten onBookmarkedBy");
        }
        // 1) remove from school saved list
        List<SchoolSaved> savedList = SchoolSaved.findByUserSchoolId(user.getId(), this.id, SchoolType.KINDY);
        if (savedList != null) {
            for (SchoolSaved saved : savedList) {
                saved.delete();
            }
        }
        // 2) leave KG community
        if (communityId != null) {
            boolean leaveComm = true;

            Long pnId = CommunityMetaCache.getPNIdFromCommunity(communityId);
            if (pnId != null) {
                leaveComm = SchoolSaved.findByUserSchoolId(user.getId(), pnId, SchoolType.PN).isEmpty();
            }
            if (leaveComm) {
                try {
                    Community pnComm = Community.findById(communityId);
                    user.leaveCommunity(pnComm);
                } catch (Exception e) {
                    logger.underlyingLogger().error("Error un-joining KG community: "+communityId, e);
                }
            }
        }
        // 3) remove bookmark record
        user.unBookmarkOn(this.id, this.objectType);
        this.noOfBookmarks--;
    }

    ///////////////////// GET SQLs /////////////////////
    public static Kindergarten findById(Long id) {
        Query q = JPA.em().createQuery("SELECT kg FROM Kindergarten kg where kg.id=?1");
        q.setParameter(1, id);
        try {
            return (Kindergarten) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static Kindergarten findByNameDistrictId(String pnName, Long districtId) {
        Query q = JPA.em().createQuery("SELECT kg FROM Kindergarten kg where kg.name=?1 and kg.districtId=?2");
        q.setParameter(1, pnName);
        q.setParameter(2, districtId);
        try {
            return (Kindergarten) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Kindergarten> findAll() {
        Query q = JPA.em().createQuery("SELECT kg FROM Kindergarten kg");
        return (List<Kindergarten>)q.getResultList();
    }

    public static List<Kindergarten> findAll(boolean withPN) {
        Query q = JPA.em().createQuery("SELECT kg FROM Kindergarten kg where kg.hasPN = ?1");
        q.setParameter(1, withPN);
        return (List<Kindergarten>)q.getResultList();
    }

    public static Long searchByNameCount(String nameSubStr) {
        Query q = JPA.em().createQuery("SELECT count(kg) FROM Kindergarten kg where kg.name like ?1 or UPPER(kg.nameEn) like ?2");
        q.setParameter(1, "%"+nameSubStr+"%");
        q.setParameter(2, "%"+nameSubStr.toUpperCase()+"%");
        return (Long)q.getSingleResult();
    }

    public static List<Kindergarten> searchByName(String nameSubStr) {
        Query q = JPA.em().createQuery("SELECT kg FROM Kindergarten kg where kg.name like ?1 or UPPER(kg.nameEn) like ?2");
        q.setParameter(1, "%"+nameSubStr+"%");
        q.setParameter(2, "%"+nameSubStr.toUpperCase()+"%");
        return (List<Kindergarten>)q.getResultList();
    }

    public static List<Kindergarten> getKGsByRegion(Long regionId) {
        Query q = JPA.em().createQuery("SELECT kg FROM Kindergarten kg where kg.regionId = ?1 order by kg.districtId, kg.name");
        q.setParameter(1, regionId);
        return (List<Kindergarten>)q.getResultList();
    }

    public static List<Kindergarten> getKGsByDistrict(Long districtId) {
        Query q = JPA.em().createQuery("SELECT kg FROM Kindergarten kg where kg.districtId = ?1 order by kg.name");
        q.setParameter(1, districtId);
        return (List<Kindergarten>)q.getResultList();
    }

    public static List<String> getOrganizationsByDistrict(Long districtId) {
        Query q = JPA.em().createQuery("SELECT distinct kg.organization FROM Kindergarten kg where kg.districtId=?1 and kg.organization is NOT NULL");
        q.setParameter(1, districtId);
        return (List<String>)q.getResultList();
    }

    public static List<Kindergarten> getTopViews(int num) {
        Query q = JPA.em().createQuery("SELECT kg FROM Kindergarten kg order by kg.noOfViews desc");
        q.setMaxResults(num);
        return (List<Kindergarten>)q.getResultList();
    }

    public static List<Kindergarten> getTopDiscussed(int num) {
        Query q = JPA.em().createQuery("SELECT kg FROM Kindergarten kg order by kg.noOfPosts desc");
        q.setMaxResults(num);
        return (List<Kindergarten>)q.getResultList();
    }

    public static List<Kindergarten> getTopBookmarked(int num) {
        Query q = JPA.em().createQuery("SELECT kg FROM Kindergarten kg order by kg.noOfBookmarks desc");
        q.setMaxResults(num);
        return (List<Kindergarten>)q.getResultList();
    }

    public static List<Kindergarten> getBookmarked(Long userId) {
        Query q = JPA.em().createQuery("SELECT kg FROM Kindergarten kg, SchoolSaved ss where ss.schoolType=?1 and ss.userId=?2 " +
                "and ss.schoolId = kg.id");
        q.setParameter(1, SchoolType.KINDY);
        q.setParameter(2, userId);
        return (List<Kindergarten>)q.getResultList();
    }

    ///////////////////// PN Merge SQLs /////////////////////
    public static int mergeCommunityIdWithPN() {
        int mergeCount = 0;
        List<Kindergarten> kgsWithPN = Kindergarten.findAll(true);
        for (Kindergarten kg : kgsWithPN) {
            PreNursery pn = PreNursery.findBy(kg.name, kg.nameEn, kg.address);
            if (pn != null) {
                kg.communityId = pn.communityId;
                kg.noOfPosts = pn.noOfPosts;
                kg.merge();
                mergeCount++;
            }
            else {
                logger.underlyingLogger().info("No PN found for KG("+kg.name+", "+kg.nameEn+", "+kg.address+"), deleting");
                kg.delete();
            }
        }
        return mergeCount;
    }

    ///////////////////// Getters /////////////////////
    public String getSummerUniformFee() {
        return summerUniformFee;
    }

    public String getWinterUniformFee() {
        return winterUniformFee;
    }

    public String getSchoolBagFee() {
        return schoolBagFee;
    }

    public String getTeaAndSnacksFee() {
        return teaAndSnacksFee;
    }

    public String getTextBooksFee() {
        return textBooksFee;
    }

    public String getWorkBooksFee() {
        return workBooksFee;
    }
}
