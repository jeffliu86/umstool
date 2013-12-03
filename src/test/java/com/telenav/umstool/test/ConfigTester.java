package com.telenav.umstool.test;
import static org.junit.Assert.*;

import org.apache.cassandra.thrift.Cassandra.system_add_column_family_args;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.telenav.user.commons.Configuration;
import com.telenav.user.commons.ConfigurationFile;


public class ConfigTester {
	private Configuration config;
	
	@Before
	public void setup(){
		config=Configuration.getInstance(ConfigurationFile.USER_SERVICE);
	}
	
	
	@Test
	public void testReadConfigFile() {
		final String clusterName = config.getString("user.data.cluster.name");
		final String keySpaceName = config.getString("user.data.cluster.keyspace");
		
		if(StringUtils.isEmpty(clusterName)||StringUtils.isEmpty(keySpaceName)){
			fail();
		}
		
		
		System.out.println("clusterName:"+clusterName);
		System.out.println("keySpaceName:"+keySpaceName);
	}

}
