package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;

import play.db.jpa.JPA;

/*
 * No UI Crud operation for this model. Static Lookup for country.
 * 
 * TODO - keith
 * Put all locations into local cache.
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
    
    public String displayName;
    
    @Enumerated(EnumType.STRING)
    public LocationType locationType;
    
    @Enumerated(EnumType.STRING)
    public LocationCode locationCode;
    
    public static enum LocationType {
        COUNTRY,
        STATE,
        CITY,
        REGION,
        DISTRICT,
        AREA,
        LOCATION
    }
    
    public static enum LocationCode {
        HK,
        US
    }
    
    public Location() {}

    public Location(LocationCode locationCode, String country) {
        this(locationCode, country, country);
    }
    
    public Location(LocationCode locationCode, String country, String displayName) {
        this.locationType = LocationType.COUNTRY;
        this.locationCode = locationCode;
        this.country = country;
        this.displayName = displayName;
    }
    
    public Location(Location parent, String value) {
        this(parent, value, value);
    }
    
    public Location(Location parent, String value, String displayName) {
        this.parent = parent;
        this.locationCode = parent.locationCode;
        this.displayName = displayName;
        if (LocationType.COUNTRY.equals(parent.locationType)) {
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

    public String getName() {
        if (locationType == LocationType.COUNTRY) {
            return country;
        } else if (locationType == LocationType.STATE) {
            return country;
        } else if (locationType == LocationType.CITY) {
            return country;
        } else if (locationType == LocationType.REGION) {
            return country;
        } else if (locationType == LocationType.DISTRICT) {
            return country;
        } else if (locationType == LocationType.AREA) {
            return country;
        } else if (locationType == LocationType.LOCATION) {
            return country;
        }
        throw new RuntimeException("Unknown locationType: " + locationType);
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static Location getHongKongCity() {
        return getLocation(LocationCode.HK, LocationType.CITY);
    }
    
    public static Location getLocation(LocationCode code, LocationType type) {
        Query q = JPA.em().createQuery("select l from Location l where locationCode = ?1 and locationType = ?2");
        q.setParameter(1, code);
        q.setParameter(2, type);
        return (Location)q.getSingleResult();
    }
    
    public static List<Location> getHongKongCityRegionsDistricts() {
        return getLocationsByCountry(getLocation(LocationCode.HK, LocationType.COUNTRY), 
                new LocationType[] { LocationType.CITY, LocationType.REGION, LocationType.DISTRICT });
    }
    
    /**
     * Traverse all children and add to the list if flagged.
     * 
     * @param country
     * @param locationTypes
     * @return
     */
    public static List<Location> getLocationsByCountry(Location country, LocationType[] locationTypes) {
        if (!LocationType.COUNTRY.equals(country.locationType))
            throw new RuntimeException(country + " is not a country");
        
        List<Location> locations = new ArrayList<Location>();
        List<LocationType> types = Arrays.asList(locationTypes);
        
        if (types.contains(LocationType.COUNTRY)) {
            locations.add(country);
        }

        List<Location> states = getStatesByCountry(country.id);
        for (Location state : states) {
            if (types.contains(LocationType.STATE)) 
                locations.add(state);
            
            List<Location> cities = getCitiesByState(state.id);
            for (Location city : cities) {
                if (types.contains(LocationType.CITY)) 
                    locations.add(city);
                
                List<Location> regions = getRegionsByCity(city.id);
                for (Location region : regions) {
                    if (types.contains(LocationType.REGION)) 
                        locations.add(region);
                    
                    List<Location> districts = getDistrictsByRegion(region.id);
                    for (Location district : districts) {
                        if (types.contains(LocationType.DISTRICT)) 
                            locations.add(district);
                        
                        List<Location> areas = getAreasByDistrict(district.id);
                        for (Location area : areas) {
                            if (types.contains(LocationType.AREA)) 
                                locations.add(area);
                            
                            if (types.contains(LocationType.LOCATION))
                                locations.addAll(getLocationsByArea(area.id));
                        }
                    }
                }
            }
        }
        return locations;
    }
    
    public static List<Location> getHongKongDistricts() {
        Query q = JPA.em().createQuery("select l from Location l where locationType = ?1 and locationCode = ?2");
        q.setParameter(1, LocationType.DISTRICT);
        q.setParameter(2, LocationCode.HK);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getHongKongAreas() {
        Query q = JPA.em().createQuery("select l from Location l where locationType = ?1 and locationCode = ?2");
        q.setParameter(1, LocationType.AREA);
        q.setParameter(2, LocationCode.HK);
        return (List<Location>)q.getResultList();
    }

    public static Location getLocationById(long id) {
        Query q = JPA.em().createQuery("Select l from Location l where id = ?1");
        q.setParameter(1, id);
        return (Location)q.getSingleResult();
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
        return (List<Location>)q.getResultList();
    }

    public static List<Location> getCitiesByState(long stateId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.CITY);
        q.setParameter(2, stateId);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getRegionsByCity(long cityId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.REGION);
        q.setParameter(2, cityId);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getDistrictsByRegion(long regionId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.DISTRICT);
        q.setParameter(2, regionId);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getAreasByDistrict(long districtId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.AREA);
        q.setParameter(2, districtId);
        return (List<Location>)q.getResultList();
    }
    
    public static List<Location> getLocationsByArea(long areaId) {
        Query q = JPA.em().createQuery("Select l from Location l where locationType = ?1 and parent_id = ?2");
        q.setParameter(1, LocationType.LOCATION);
        q.setParameter(2, areaId);
        return (List<Location>)q.getResultList();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Location) {
            final Location other = (Location) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
    
    @Override
    public  String toString() {
        return "[" + locationCode + "|" + locationType + "|" + country + "|" + 
                state + "|" + city + "|" + region + "|" + district + "|" + 
                area + "|" + location + "|" + displayName + "]";
    }
}
