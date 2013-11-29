package com.telenav.user.dao.cassandra;

import com.telenav.user.commons.UserObject;
import com.telenav.user.dao.ApplicationDao;
import com.telenav.user.dao.ClientRequestBufferDao;
import com.telenav.user.dao.LoginSessionDao;
import com.telenav.user.dao.MarkerDao;
import com.telenav.user.dao.MigrationDao;
import com.telenav.user.dao.ReceiptDao;
import com.telenav.user.dao.TokenSetDao;
import com.telenav.user.dao.UserAccountDao;
import com.telenav.user.dao.UserCredentialsDao;
import com.telenav.user.dao.UserDaoFactory;
import com.telenav.user.dao.UserItemDao;
import com.telenav.user.dao.UserProfileDao;

public class CassandraUserDaoFactory extends UserObject implements UserDaoFactory {

	private CassandraApplicationDao cassandraApplicationDao = null;
	private CassandraLoginSessionDao cassandraLoginSessionDao = null;
	private CassandraMigrationDao cassandraMigrationDao = null;
	private CassandraUserAccountDao cassandraUserAccountDao = null;
	private CassandraUserCredentialsDao cassandraUserCredentialsDao = null;
	private CassandraUserItemDao cassandraUserItemDao = null;
	private CassandraUserProfileDao cassandraUserProfileDao = null;
	private CassandraTokenSetDao cassandraTokenSetDao = null;
	private CassandraMarkerDao cassandraMarkerDao = null;
	private CassandraReceiptDao cassandraReceiptDao = null;
	private CassandraClientRequestBufferDao cassandraClientRequestBufferDao = null;

	public CassandraUserDaoFactory() {
	}

	@Override
	public ApplicationDao getApplicationDao() {
		if (this.cassandraApplicationDao == null) {
			this.cassandraApplicationDao = new CassandraApplicationDao(this);
		}
		return this.cassandraApplicationDao;
	}

	@Override
	public LoginSessionDao getLoginSessionDao() {
		if (this.cassandraLoginSessionDao == null) {
			this.cassandraLoginSessionDao = new CassandraLoginSessionDao(this);
		}
		return this.cassandraLoginSessionDao;
	}

	@Override
	public MigrationDao getMigrationDao() {
		if (this.cassandraMigrationDao == null) {
			this.cassandraMigrationDao = new CassandraMigrationDao(this);
		}
		return this.cassandraMigrationDao;
	}

	@Override
	public UserAccountDao getUserAccountDao() {
		if (this.cassandraUserAccountDao == null) {
			this.cassandraUserAccountDao = new CassandraUserAccountDao(this);
		}
		return this.cassandraUserAccountDao;
	}

	@Override
	public UserItemDao getUserItemDao() {
		if (this.cassandraUserItemDao == null) {
			this.cassandraUserItemDao = new CassandraUserItemDao(this);
		}
		return this.cassandraUserItemDao;
	}

	@Override
	public UserProfileDao getUserProfileDao() {
		if (this.cassandraUserProfileDao == null) {
			this.cassandraUserProfileDao = new CassandraUserProfileDao(this);
		}
		return this.cassandraUserProfileDao;
	}

	@Override
	public UserCredentialsDao getUserCredentialsDao() {
		if (this.cassandraUserCredentialsDao == null) {
			this.cassandraUserCredentialsDao = new CassandraUserCredentialsDao(this);
		}
		return this.cassandraUserCredentialsDao;
	}

	@Override
	public TokenSetDao getTokenSetDao() {
		if (this.cassandraTokenSetDao == null) {
			this.cassandraTokenSetDao = new CassandraTokenSetDao(this);
		}
		return this.cassandraTokenSetDao;
	}

	@Override
	public MarkerDao getMarkerDao() {
		if (this.cassandraMarkerDao == null) {
			this.cassandraMarkerDao = new CassandraMarkerDao(this);
		}
		return this.cassandraMarkerDao;
	}

	@Override
	public ReceiptDao getReceiptDao() {
		if (this.cassandraReceiptDao == null) {
			this.cassandraReceiptDao = new CassandraReceiptDao(this);
		}
		return this.cassandraReceiptDao;
	}

	@Override
	public ClientRequestBufferDao getClientRequestBufferDao() {
		if (this.cassandraClientRequestBufferDao == null) {
			this.cassandraClientRequestBufferDao = new CassandraClientRequestBufferDao(this);
		}
		return this.cassandraClientRequestBufferDao;
	}

}
