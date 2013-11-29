package com.telenav.user.dao.cassandra;

import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;
import com.telenav.user.commons.LogContext;
import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserCommonUtils;
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.dao.DataAccessException;
import com.telenav.user.dao.ReceiptDao;
import com.telenav.user.model.UmTelenavReceiptsCollection;
import com.telenav.user.resource.RoDeviceInfo;
import com.telenav.user.resource.RoTelenavReceipt;

public class CassandraReceiptDao extends CassandraDao implements ReceiptDao {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(CassandraReceiptDao.class);

	private static final String KEY_TELENAV_RECEIPT = "telenav_receipt";

	private static final String KEY_ACTIVE_RECEIPT_USER_LOOKUP = "active_receipt_user_lookup";
	private static final String KEY_ACTIVE_RECEIPT_INTANCE_ID_LOOKUP = "active_receipt_instance_id_lookup";
	private static final String KEY_ACTIVE_RECEIPT_DEVICE_UID_LOOKUP = "active_receipt_device_uid_lookup";
	private static final String KEY_ACTIVE_RECEIPT_MAC_ADDRESS_LOOKUP = "active_receipt_mac_address_lookup";

	private static final String KEY_ARCHIVE_RECEIPT_USER_LOOKUP = "archive_receipt_user_lookup";
	private static final String KEY_ARCHIVE_RECEIPT_INTANCE_ID_LOOKUP = "archive_receipt_instance_id_lookup";
	private static final String KEY_ARCHIVE_RECEIPT_DEVICE_UID_LOOKUP = "archive_receipt_device_uid_lookup";
	private static final String KEY_ARCHIVE_RECEIPT_MAC_ADDRESS_LOOKUP = "archive_receipt_mac_address_lookup";

	private static final StringSerializer SERIALIZER = StringSerializer.get();

	private static final ColumnFamily<String, String> CF_TELENAV_RECEIPT = new ColumnFamily<String, String>(KEY_TELENAV_RECEIPT, SERIALIZER, SERIALIZER);

	private static final ColumnFamily<String, String> CF_ACTIVE_RECEIPT_USER_LOOKUP = new ColumnFamily<String, String>(KEY_ACTIVE_RECEIPT_USER_LOOKUP, SERIALIZER, SERIALIZER);
	private static final ColumnFamily<String, String> CF_ACTIVE_RECEIPT_INSTANCE_ID_LOOKUP = new ColumnFamily<String, String>(KEY_ACTIVE_RECEIPT_INTANCE_ID_LOOKUP, SERIALIZER, SERIALIZER);
	private static final ColumnFamily<String, String> CF_ACTIVE_RECEIPT_DEVICE_UID_LOOKUP = new ColumnFamily<String, String>(KEY_ACTIVE_RECEIPT_DEVICE_UID_LOOKUP, SERIALIZER, SERIALIZER);
	private static final ColumnFamily<String, String> CF_ACTIVE_RECEIPT_MAC_ADDRESS_LOOKUP = new ColumnFamily<String, String>(KEY_ACTIVE_RECEIPT_MAC_ADDRESS_LOOKUP, SERIALIZER, SERIALIZER);

	private static final ColumnFamily<String, String> CF_ARCHIVE_RECEIPT_USER_LOOKUP = new ColumnFamily<String, String>(KEY_ARCHIVE_RECEIPT_USER_LOOKUP, SERIALIZER, SERIALIZER);
	private static final ColumnFamily<String, String> CF_ARCHIVE_RECEIPT_INSTANCE_ID_LOOKUP = new ColumnFamily<String, String>(KEY_ARCHIVE_RECEIPT_INTANCE_ID_LOOKUP, SERIALIZER, SERIALIZER);
	private static final ColumnFamily<String, String> CF_ARCHIVE_RECEIPT_DEVICE_UID_LOOKUP = new ColumnFamily<String, String>(KEY_ARCHIVE_RECEIPT_DEVICE_UID_LOOKUP, SERIALIZER, SERIALIZER);
	private static final ColumnFamily<String, String> CF_ARCHIVE_RECEIPT_MAC_ADDRESS_LOOKUP = new ColumnFamily<String, String>(KEY_ARCHIVE_RECEIPT_MAC_ADDRESS_LOOKUP, SERIALIZER, SERIALIZER);

	public CassandraReceiptDao(final CassandraUserDaoFactory factory) {
		super(CassandraKeySpace.USER_DATA, factory);
	}

	@Override
	public RoTelenavReceipt put(final RoTelenavReceipt receipt) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraReceiptDao.put: receipt: %s", receipt);

		RoTelenavReceipt returnValue = null;

		try {

			//If client does not fill in receipt Id, will generate one
			String receiptId = receipt.getReceiptId();
			if (StringUtils.isEmpty(receiptId)) {
				receiptId = UserCommonUtils.generateRandomGUID();
				receipt.setReceiptId(receiptId);
			}

			final boolean isTTL = receipt.isReceiptArchivable().equals(Boolean.TRUE);
			int ttl = 60;

			if (isTTL) {
				//final long purchaseTimestamp = receipt.getPurchaseTimestamp();
				final long offerExpiryTimestamp = receipt.getOfferExpiryTimestamp();
				final long difference = offerExpiryTimestamp - System.currentTimeMillis();

				if (difference > 0) {
					ttl = (int) (difference / 1000);
				}
			}

			final MutationBatch mutaion = this.keyspace.prepareMutationBatch();

			//store telenav receipt data
			ColumnListMutation<String> columnListMutation = mutaion.withRow(CF_TELENAV_RECEIPT, receiptId);
			columnListMutation.putColumn(KEY_TELENAV_RECEIPT, receipt.toJsonString());

			//store user active receipt lookup data
			if (receipt.getUserId() != null) {
				columnListMutation = mutaion.withRow(CF_ACTIVE_RECEIPT_USER_LOOKUP, receipt.getUserId());
				if (isTTL && (ttl > 0)) {
					columnListMutation.putColumn(receiptId, receipt.toJsonString(), ttl);
				}
				else {
					columnListMutation.putColumn(receiptId, receipt.toJsonString());
				}

				//store user receipt archive data
				columnListMutation = mutaion.withRow(CF_ARCHIVE_RECEIPT_USER_LOOKUP, receipt.getUserId());
				columnListMutation.putColumn(receiptId, receipt.toJsonString());
			}

			//store device uid active receipt data
			if (receipt.getDeviceUid() != null) {
				columnListMutation = mutaion.withRow(CF_ACTIVE_RECEIPT_DEVICE_UID_LOOKUP, receipt.getDeviceUid());
				if (isTTL && (ttl > 0)) {
					columnListMutation.putColumn(receipt.getReceiptId(), receipt.toJsonString(), ttl);
				}
				else {
					columnListMutation.putColumn(receipt.getReceiptId(), receipt.toJsonString());
				}

				columnListMutation = mutaion.withRow(CF_ARCHIVE_RECEIPT_DEVICE_UID_LOOKUP, receipt.getDeviceUid());
				columnListMutation.putColumn(receipt.getReceiptId(), receipt.toJsonString());
			}

			//store instance id active receipt data
			if (receipt.getDeviceInstanceId() != null) {
				columnListMutation = mutaion.withRow(CF_ACTIVE_RECEIPT_INSTANCE_ID_LOOKUP, receipt.getDeviceInstanceId());
				if (isTTL && (ttl > 0)) {
					columnListMutation.putColumn(receipt.getReceiptId(), receipt.toJsonString(), ttl);
				}
				else {
					columnListMutation.putColumn(receipt.getReceiptId(), receipt.toJsonString());
				}

				columnListMutation = mutaion.withRow(CF_ARCHIVE_RECEIPT_INSTANCE_ID_LOOKUP, receipt.getDeviceInstanceId());
				columnListMutation.putColumn(receipt.getReceiptId(), receipt.toJsonString());
			}

			mutaion.execute();

			returnValue = receipt;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraReceiptDao.put: %s", returnValue);

		return returnValue;
	}

	@Override
	public UmTelenavReceiptsCollection getArchiveReceipts(final String userId, final RoDeviceInfo deviceInfo) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraReceiptDao.getArchiveReceipts: userId: %s, deviceInfo: %s", userId, deviceInfo);

		final UmTelenavReceiptsCollection returnValue = new UmTelenavReceiptsCollection(ResponseCode.GENERAL_INTERNAL_ERROR, null);

		final Collection<RoTelenavReceipt> receipts = new TreeSet<RoTelenavReceipt>();

		getReceipt(CF_ARCHIVE_RECEIPT_USER_LOOKUP, userId, receipts);

		if (deviceInfo != null) {

			getReceipt(CF_ARCHIVE_RECEIPT_INSTANCE_ID_LOOKUP, deviceInfo.getInstanceId(), receipts);
			getReceipt(CF_ARCHIVE_RECEIPT_DEVICE_UID_LOOKUP, deviceInfo.getDeviceUid(), receipts);
			getReceipt(CF_ARCHIVE_RECEIPT_MAC_ADDRESS_LOOKUP, deviceInfo.getDeviceMacAddress(), receipts);
		}

		returnValue.setReceipts(receipts);
		returnValue.setResponseCode(ResponseCode.ALL_OK, null);

		LOG.exit(logContext, "CassandraReceiptDao.getArchiveReceipts: %s", returnValue);

		return returnValue;
	}

	private void getReceipt(final ColumnFamily<String, String> columnFamily, final String key, final Collection<RoTelenavReceipt> returnValue) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraReceiptDao.getReceipt: columnFamily: %s, key: %s, returnValue: %s", columnFamily, key, returnValue);

		if (key != null) {

			try {
				final ColumnList<String> receiptColumns = this.keyspace.prepareQuery(columnFamily).getKey(key).execute().getResult();
				String receiptJosn;
				RoTelenavReceipt receipt;

				for (final Column<String> column : receiptColumns) {
					receiptJosn = column.getStringValue();
					receipt = RoTelenavReceipt.buildFromJson(receiptJosn);
					returnValue.add(receipt);
				}
			}
			catch (final NotFoundException e) {
				// Silencing the exception
			}
			catch (final ConnectionException e) {
				throw new DataAccessException(e);
			}
		}

		LOG.exit(logContext, "CassandraReceiptDao.getReceipt: %s", returnValue);

	}

	@Override
	public UmTelenavReceiptsCollection getActiveReceipts(final String userId, final RoDeviceInfo deviceInfo) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraReceiptDao.getActiveReceipts: userId: %s, deviceInfo: %s", userId, deviceInfo);

		final UmTelenavReceiptsCollection returnValue = new UmTelenavReceiptsCollection(ResponseCode.GENERAL_INTERNAL_ERROR, null);

		final Collection<RoTelenavReceipt> receipts = new TreeSet<RoTelenavReceipt>();

		getReceipt(CF_ACTIVE_RECEIPT_USER_LOOKUP, userId, receipts);

		if (deviceInfo != null) {

			getReceipt(CF_ACTIVE_RECEIPT_INSTANCE_ID_LOOKUP, deviceInfo.getInstanceId(), receipts);
			getReceipt(CF_ACTIVE_RECEIPT_DEVICE_UID_LOOKUP, deviceInfo.getDeviceUid(), receipts);
			getReceipt(CF_ACTIVE_RECEIPT_MAC_ADDRESS_LOOKUP, deviceInfo.getDeviceMacAddress(), receipts);
		}

		returnValue.setReceipts(receipts);
		returnValue.setResponseCode(ResponseCode.ALL_OK, null);

		LOG.exit(logContext, "CassandraReceiptDao.getActiveReceipts: %s", returnValue);

		return returnValue;
	}
}
