/**
 * 
 */
package com.telenav.user.search;

import java.util.Collection;


import com.telenav.user.dao.UserProfileDao;
import com.telenav.user.dao.cassandra.CassandraUserDaoFactory;
import com.telenav.user.resource.RoItemMark;
import com.telenav.user.resource.RoUserProfile;
import com.telenav.user.resource.RoUserRegistrationData;

/**
 * @author [Liu Jie]
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
public class UserSearchServiceImpl implements UserSearchService {
    
       private static final CassandraUserDaoFactory daoFactory=new CassandraUserDaoFactory();

    
    public Collection<RoUserProfile>  queryUserProfiles(String key) {
        UserProfileDao profileDao=daoFactory.getUserProfileDao();
        Collection<RoUserProfile> profiles=profileDao.listAllProfiles(key);
        for (RoUserProfile eachProfile : profiles) {
            System.out.println(eachProfile.toJsonString());
        }
        return profiles;
    }
    
    public RoUserRegistrationData getUserRegistrationData(String key) {
        RoUserRegistrationData data = new RoUserRegistrationData(null,null);
        data.setApplicationId("A1111111111");
        data.setFirstName("new");
        data.setLastName("jersey");
        data.setDeviceInfo("SamSung");
        return data;
    }

    public RoItemMark getRoItemMark(String key) {
        RoItemMark roItemMark  = new RoItemMark(null,null);
        roItemMark.setMarkerId("1001");
        return roItemMark;
    }

    /*
     * (non-Javadoc)
     * @see com.telenav.user.search.UserSearchService#searchUser()
     */
    @Override
    public UserSearchResult searchUser(String key) {
        UserSearchResult result = new UserSearchResult();
        RoUserRegistrationData roUserRegistrationData = getUserRegistrationData(key);
        RoItemMark roItemMark = getRoItemMark(key);
        result.setRoItemMark(roItemMark);
        result.setRoUserRegistrationData(roUserRegistrationData);
        result.setRoUserProfiles(queryUserProfiles(key));
        return result;
    }

}
