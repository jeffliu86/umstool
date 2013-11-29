package com.telenav.user.dao.cassandra;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.AstyanaxContext.Builder;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.connectionpool.impl.SmaLatencyScoreStrategyImpl;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.telenav.user.commons.Configuration;
import com.telenav.user.commons.ConfigurationFile;
import com.telenav.user.commons.SystemProperty;
import com.telenav.user.commons.UserServiceLogger;

public enum CassandraKeySpace {

	USER_DATA;

	private static final String CQL_VERSION = "3.0.0";
	private static final String CASSANDRA_VERSION = "1.2";

	private static final String CONNECTION_POOL_NAME = "DataConnectionPool";

	private CassandraKeySpace() {
		createKeySpace();
	}

	private String clusterName = null;
	private String seeds = null;
	private Integer port = null;
	private Integer maxConnsPerHost = null;
	private String keyspaceName = null;

	//	private final String username = null;
	//	private final String password = null;

	private Keyspace keySpace = null;
	private AstyanaxContext<Keyspace> context = null;

	private void createKeySpace() {

		try {

			final Configuration config = Configuration.getInstance(ConfigurationFile.USER_SERVICE);

			final String clusterName = config.getString("user.data.cluster.name");
			final String seeds = config.getString("user.data.cluster.seeds");
			final Integer port = config.getInteger("user.data.cluster.port");
			final Integer maxConnsPerHost = config.getInteger("user.data.cluster.maxConnectionPerHost");
			final String keySpaceName = config.getString("user.data.cluster.keyspace");
			//final String username = config.getProperty("user.data.cluster.username");
			//final String password = config.getProperty("user.data.cluster.password");

			if (!clusterName.equals(this.clusterName) || !maxConnsPerHost.equals(this.maxConnsPerHost) || !seeds.equals(this.seeds) || !port.equals(this.port)
			        || !keySpaceName.equals(this.keyspaceName)) {

				if (this.context != null) {
					shutDown();
				}

				final Builder contextBuilder = new AstyanaxContext.Builder();
				contextBuilder.forCluster(clusterName);
				contextBuilder.forKeyspace(keySpaceName);

				{
					final AstyanaxConfigurationImpl targetVersion = new AstyanaxConfigurationImpl();
					targetVersion.setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE);
					targetVersion.setConnectionPoolType(ConnectionPoolType.TOKEN_AWARE);
					targetVersion.setCqlVersion(CQL_VERSION).setTargetCassandraVersion(CASSANDRA_VERSION);
					targetVersion.setDefaultReadConsistencyLevel(ConsistencyLevel.CL_LOCAL_QUORUM);
					targetVersion.setDefaultWriteConsistencyLevel(ConsistencyLevel.CL_ONE);
					contextBuilder.withAstyanaxConfiguration(targetVersion);
				}

				{

					final ConnectionPoolConfigurationImpl connPool = new ConnectionPoolConfigurationImpl(CONNECTION_POOL_NAME);
					connPool.setPort(port);
					connPool.setMaxConnsPerHost(maxConnsPerHost);
					connPool.setSeeds(seeds);
					connPool.setLocalDatacenter(SystemProperty.getDatacenter());
					connPool.setLatencyScoreStrategy(new SmaLatencyScoreStrategyImpl(10000, 10000, 100, 2)); //TODO: tuning
					// 	The constructor takes:
					//  UpdateInterval: 10000 : Will resort hosts per token partition every 10 seconds
					//  ResetInterval: 10000 : Will clear the latency every 10 seconds
					//  WindowSize: 100 : Uses last 100 latency samples
					//  BadnessThreshold: 2 : Will sort hosts if a host is more than 100% 

					//final SimpleAuthenticationCredentials credentials = new SimpleAuthenticationCredentials(username, password);
					//connPool.setAuthenticationCredentials(credentials);

					contextBuilder.withConnectionPoolConfiguration(connPool);
				}

				{
					final CountingConnectionPoolMonitor monitor = new CountingConnectionPoolMonitor();
					contextBuilder.withConnectionPoolMonitor(monitor);
				}

				this.context = contextBuilder.buildKeyspace(ThriftFamilyFactory.getInstance());

				this.context.start();

				final Keyspace keyspace = this.context.getClient();

				this.clusterName = clusterName;
				this.seeds = seeds;
				this.port = port;
				this.maxConnsPerHost = maxConnsPerHost;
				this.keyspaceName = keySpaceName;

				this.keySpace = keyspace;
			}

		}
		catch (final Throwable e) {
			UserServiceLogger.ERROR(getClass(), e, "Keyspace Initialization Error in %s", e);
		}
	}

	Keyspace getKeySpace() {
		createKeySpace();
		return this.keySpace;
	}

	public void shutDown() {
		if (this.context != null) {
			this.context.shutdown();
		}
	}

}
