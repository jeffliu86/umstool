package com.telenav.umstool.test;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.telenav.user.dao.UserProfileDao;
import com.telenav.user.dao.cassandra.CassandraUserDaoFactory;
import com.telenav.user.resource.RoUserProfile;

public class userItemDaoTester {
    private UserProfileDao profileDao;
    
    @Before
    public void setup(){
    	CassandraUserDaoFactory daoFactory=new CassandraUserDaoFactory();
    	profileDao=daoFactory.getUserProfileDao();
    }
	
    @Test
	public void testQueryUserProfiles() {
    	String key="8QGE4S4RRJQ13MPJ3U7P335LV";
    	Collection<RoUserProfile> profiles=profileDao.listAllProfiles(key);
		for (RoUserProfile eachProfile : profiles) {
			System.out.println(eachProfile.toJsonString());
		}
	}

}
