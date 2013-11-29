package com.telenav.user.dao;

public interface UserDaoFactory {

	public abstract ApplicationDao getApplicationDao();

	public abstract LoginSessionDao getLoginSessionDao();

	public abstract MigrationDao getMigrationDao();

	public abstract UserAccountDao getUserAccountDao();

	public abstract UserCredentialsDao getUserCredentialsDao();

	public abstract UserItemDao getUserItemDao();

	public abstract UserProfileDao getUserProfileDao();

	public abstract TokenSetDao getTokenSetDao();

	public abstract MarkerDao getMarkerDao();

	public abstract ReceiptDao getReceiptDao();

	public abstract ClientRequestBufferDao getClientRequestBufferDao();

}
