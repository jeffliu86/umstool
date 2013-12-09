package com.telenav.umstool.test;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.telenav.user.dao.UserProfileDao;
import com.telenav.user.dao.cassandra.CassandraUserAccountDao;
import com.telenav.user.dao.cassandra.CassandraUserDaoFactory;
import com.telenav.user.resource.RoMarker;
import com.telenav.user.resource.RoTelenavReceipt;
import com.telenav.user.resource.RoUserItem;
import com.telenav.user.resource.RoUserProfile;
import com.telenav.user.tool.service.Car;
import com.telenav.user.tool.service.TableBean;

public class userItemDaoTester {
    CassandraUserDaoFactory daoFactory=new CassandraUserDaoFactory();
    
    @Before
    public void setup(){

    }
	
    @Test
	public void testQueryUserProfiles() {
    	String key="8QGE4S4RRJQ13MPJ3U7P335LV";
    	UserProfileDao profileDao=daoFactory.getUserProfileDao();
    	Collection<RoUserProfile> profiles=profileDao.listAllProfiles(key);
    	printInfo(profiles);
	}

	private void printInfo(Collection collection) {
		for (Object object : collection) {
			System.out.println(object);
		}
	}
    
	@Test
    public void testQueryUserMarkers(){
    	String key="L50H3UJDH8QJYBJAECGQF2PN";
    	Collection<RoMarker> markers=daoFactory.getMarkerDao().ListAllMarkers(key);
    	
    	printInfo(markers);
    	
    	
    }
	
	@Test
	public void testQueryUserItems(){
		String key="L50H3UJDH8QJYBJAECGQF2PN";
    	Collection<RoUserItem> items=daoFactory.getUserItemDao().listUserItems(key);
    	
    	printInfo(items);
    	
	}
	
	@Test
	public void testQueryUserRegistration(){
		String key="8QGE4S4RRJQ13MPJ3U7P335LV";
		 
		CassandraUserAccountDao accountDao= (CassandraUserAccountDao)daoFactory.getUserAccountDao();
		
		System.out.println(accountDao.getRegistrationData(key));
	}
	
	@Test
	public void testQueryActiveReceipt(){
		String key="2NNUU41PDPTTYOXBLE9A6Y32P";
		Collection<RoTelenavReceipt> result= new LinkedList<>( daoFactory.getReceiptDao().getActiveReceipts(key, null).getReceipts());
		for (RoTelenavReceipt each : result) {
			System.out.println(each.getReceiptId());
			System.out.println(each.getOfferCode());
		}
	}
	
	@Test
	public void testQueryItem(){
		String key="9ZHE984FCHBYRISDOAAW7POVD";
		Collection<RoUserItem> results= daoFactory.getUserItemDao().listUserItems(key);
		for (RoUserItem roUserItem : results) {
			System.out.println(roUserItem.getAllMarksName());			
		}
	}
	
	@Test
	public void somethingTester(){
		TableBean tb=new TableBean();
		List<Car> results= tb.getCarsSmall();
	}

}
