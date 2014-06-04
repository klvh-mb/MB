package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/*
 * No UI Crud operation for this model. Static Lookup for country.
 */
@Entity
public class Location  {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    @ManyToOne
    public Location parent;
    
    /* e.g. US, China */
    public String country;
    
    /* e.g. California, Hong Kong - state or province or the like */
    public String state;
    
    /* e.g. Palo Alto, Hong Kong */
    public String city;
    
    /* e.g. Hong Kong, Kowloon, New Territories */
    public String region;
    
    /* e.g. Central and Western */
    public String district;

    /* e.g. Admiralty */
    public String area;
    
    /* e.g. Pacific Place */
    public String location;
    
    @Enumerated(EnumType.STRING)
    public LocationType locationType;
    
    public static enum LocationType {
        COUNTRY,
        STATE,
        CITY,
        REGION,
        DISTRICT,
        AREA,
        LOCATION
    }
    
    public Location() {}

    public Location(String country) {
        this(null, country);
    }
    
    public Location(Location parent, String value) {
        this.parent = parent;
        if (parent == null) {
            this.locationType = LocationType.COUNTRY;
            this.country = value;
        } else if (LocationType.COUNTRY.equals(parent.locationType)) {
            this.locationType = LocationType.STATE;
            this.country = parent.country;
            this.state = value;
        } else if (LocationType.STATE.equals(parent.locationType)) {
            this.locationType = LocationType.CITY;
            this.country = parent.country;
            this.state = parent.state;
            this.city = value;
        } else if (LocationType.CITY.equals(parent.locationType)) {
            this.locationType = LocationType.REGION;
            this.country = parent.country;
            this.state = parent.state;
            this.city = parent.city;
            this.region = value;
        } else if (LocationType.REGION.equals(parent.locationType)) {
            this.locationType = LocationType.DISTRICT;
            this.country = parent.country;
            this.state = parent.state;
            this.city = parent.city;
            this.region = parent.region;
            this.district = value;
        } else if (LocationType.DISTRICT.equals(parent.locationType)) {
            this.locationType = LocationType.AREA;
            this.country = parent.country;
            this.state = parent.state;
            this.city = parent.city;
            this.region = parent.region;
            this.district = parent.district;
            this.area = value;
        } else if (LocationType.AREA.equals(parent.locationType)) {
            this.locationType = LocationType.LOCATION;
            this.country = parent.country;
            this.state = parent.state;
            this.city = parent.city;
            this.region = parent.region;
            this.district = parent.district;
            this.area = parent.area;
            this.location = value;
        }
    }
    
    @Transactional
    public void save() {
        JPA.em().persist(this);
        JPA.em().flush();
    }
      
    @Transactional
    public void delete() {
        JPA.em().remove(this);
    }
      
    @Transactional
    public void merge() {
        JPA.em().merge(this);
    }
      
    @Transactional
    public void refresh() {
        JPA.em().refresh(this);
    }
	
    public static List<Location> getAllCountries() {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1");
        q.setParameter(1, LocationType.COUNTRY);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getStatesByCountry(long countryId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.STATE);
        q.setParameter(2, countryId);
        return (List<Location>)q.getSingleResult();
    }

    public static List<Location> getCitiesByState(long stateId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.CITY);
        q.setParameter(2, stateId);
        return (List<Location>)q.getSingleResult();
    }
    
    public static List<Location> getRegionsByCity(long cityId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.REGION);
        q.setParameter(2, cityId);
        return (List<Location>)q.getSingleResult();
    }
    
    public static List<Location> getDistrictsByRegion(long regionId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.DISTRICT);
        q.setParameter(2, regionId);
        return (List<Location>)q.getSingleResult();
    }
    
    public static List<Location> getAreasByDistrict(long districtId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.AREA);
        q.setParameter(2, districtId);
        return (List<Location>)q.getSingleResult();
    }
    
    public static List<Location> getLocationsByArea(long areaId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.LOCATION);
        q.setParameter(2, areaId);
        return (List<Location>)q.getSingleResult();
    }
    
    public static void init() {
        Query q = JPA.em().createQuery("Select count(l) from Location l");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        Location countryHK = new Location("香港");
        JPA.em().persist(countryHK);
        Location stateHK = new Location(countryHK, "香港");
        JPA.em().persist(stateHK);
        Location cityHK = new Location(stateHK, "香港");
        JPA.em().persist(cityHK);
        
        Location hkIsland = new Location(cityHK, "香港島");
        JPA.em().persist(hkIsland);
        Location d1 = new Location(hkIsland, "中西區");
        JPA.em().persist(d1);
        Location d2 = new Location(hkIsland, "港島東區");
        JPA.em().persist(d2);
        Location d3 = new Location(hkIsland, "南區");
        JPA.em().persist(d3);
        Location d4 = new Location(hkIsland, "灣仔區");
        JPA.em().persist(d4);
        
        Location kowloon = new Location(cityHK, "九龍");
        JPA.em().persist(kowloon);
        Location d5 = new Location(kowloon, "九龍城區");
        JPA.em().persist(d5);
        Location d6 = new Location(kowloon, "觀塘區");
        JPA.em().persist(d6);
        Location d8 = new Location(kowloon, "深水埗區");
        JPA.em().persist(d8);
        Location d9 = new Location(kowloon, "黃大仙區");
        JPA.em().persist(d9);
        Location d10 = new Location(kowloon, "油尖旺區");
        JPA.em().persist(d10);
        
        Location newTerritories = new Location(cityHK, "新界");
        JPA.em().persist(newTerritories);
        Location d7 = new Location(newTerritories, "西貢區");
        JPA.em().persist(d7);
        Location d11 = new Location(newTerritories, "北區");
        JPA.em().persist(d11);
        Location d12 = new Location(newTerritories, "沙田區");
        JPA.em().persist(d12);
        Location d13 = new Location(newTerritories, "大埔區");
        JPA.em().persist(d13);
        Location d14 = new Location(newTerritories, "葵青區");
        JPA.em().persist(d14);
        Location d15 = new Location(newTerritories, "荃灣區");
        JPA.em().persist(d15);
        Location d16 = new Location(newTerritories, "屯門區");
        JPA.em().persist(d16);
        Location d17 = new Location(newTerritories, "元朗區");
        JPA.em().persist(d17);
        
        Location islands = new Location(cityHK, "離島");
        JPA.em().persist(islands);
        Location d18 = new Location(islands, "離島區");
        JPA.em().persist(d18);
    }
}
