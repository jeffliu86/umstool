/**
 * 
 */
package com.telenav.user.tool.service;

import java.util.Collection;
import java.util.LinkedList;


import com.telenav.user.dao.UserProfileDao;
import com.telenav.user.dao.cassandra.CassandraUserAccountDao;
import com.telenav.user.dao.cassandra.CassandraUserDaoFactory;
import com.telenav.user.model.constant.EnumUserCredentialsType;
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
        
        //2) Show user account information (from user_account) including the registration data and migration_data 
        CassandraUserAccountDao accountDao= (CassandraUserAccountDao)daoFactory.getUserAccountDao();
        result.setRoUserMigrationState(accountDao.getMigrationStateForUser(key));       
        result.setRoUserRegistrationData(accountDao.getRegistrationData(key));
        
        //3) Show all credentials associated with this user (from user_credentials) 
        result.setRoUserCredentials(daoFactory.getUserCredentialsDao().getAllCredentialsForUser(key));
        //4) Show user profiles(from user_profile) 
        result.setRoUserProfiles(daoFactory.getUserProfileDao().listAllProfiles(key));
        //5) Show user markers (from user_marker_lookup) 
        result.setRoMarkers(daoFactory.getMarkerDao().ListAllMarkers(key));
        //6) Show user items and their markers like all the Favorites, Recent etc 
        result.setRoUserItems(daoFactory.getUserItemDao().listUserItems(key));
        //7) Show active receipt
        
       
        result.setActiveReceipts(
        		new LinkedList<>( daoFactory.getReceiptDao().getActiveReceipts(key, null).getReceipts()));       
        //8) Show archived receipt 
       
        result.setArchivedReceipts(
        		new LinkedList<>(daoFactory.getReceiptDao().getArchiveReceipts(key, null).getReceipts()));
        
        
        return result;
    }

	@Override
	public String lookUpUserKey(String fieldType, String field) {
		EnumUserCredentialsType cType;
		
		if(fieldType.equalsIgnoreCase(UserSearcher.TYPE_EMAIL)){
			cType=EnumUserCredentialsType.EMAIL_PASSWORD;
		}else if (fieldType.equalsIgnoreCase(UserSearcher.TYPE_FB_ID)) {
			cType=EnumUserCredentialsType.FACEBOOK_ACCESS_TOKEN;
		}else{
			cType=EnumUserCredentialsType.GOOGLEPLUS_ACCESS_TOKEN;
		}
		return daoFactory.getUserCredentialsDao().lookupUserId(
						cType, 
						field);
	}

}
