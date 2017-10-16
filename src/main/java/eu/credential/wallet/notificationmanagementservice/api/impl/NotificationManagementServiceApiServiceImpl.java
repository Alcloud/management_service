package eu.credential.wallet.notificationmanagementservice.api.impl;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import eu.credential.config.codesystems.CredentialIdentifiers_1_0;
import eu.credential.wallet.notificationmanagementservice.api.Constants;
import eu.credential.wallet.notificationmanagementservice.api.NotificationManagementServiceApiService;
import eu.credential.wallet.notificationmanagementservice.api.impl.mongo.NotificationMongoClient;
import eu.credential.wallet.notificationmanagementservice.model.AddPreferencesRequest;
import eu.credential.wallet.notificationmanagementservice.model.AddPreferencesResponse;
import eu.credential.wallet.notificationmanagementservice.model.DeletePreferencesRequest;
import eu.credential.wallet.notificationmanagementservice.model.DeletePreferencesResponse;
import eu.credential.wallet.notificationmanagementservice.model.GetPreferencesRequest;
import eu.credential.wallet.notificationmanagementservice.model.GetPreferencesResponse;
import eu.credential.wallet.notificationmanagementservice.model.Identifier;
import eu.credential.wallet.notificationmanagementservice.model.KeyValue;
import eu.credential.wallet.notificationmanagementservice.model.Preference;
import eu.credential.wallet.notificationmanagementservice.model.PreferenceList;
import eu.credential.wallet.notificationmanagementservice.model.ResetPreferencesRequest;
import eu.credential.wallet.notificationmanagementservice.model.ResetPreferencesResponse;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-05-10T15:19:22.672+02:00")
public class NotificationManagementServiceApiServiceImpl extends NotificationManagementServiceApiService {

	private MongoClient mongoClient;
	private MongoDatabase db;
	private MongoCollection<Preference> mc;

	private static final String DB_NAME = "notifications";
	private static final String COLLECTION_NAME = "preferences";

	protected static final Logger logger = LoggerFactory.getLogger(NotificationManagementServiceApiServiceImpl.class);

	@Override
	public Response notificationManagementServiceAddPreferencesPost(AddPreferencesRequest body,
			SecurityContext securityContext) throws NotFoundException {

		logger.info("Preferences that should be added to the DB {}", body.getPreferenceList().toString());
		mongoDBinit();

		try {
			// a list of preferences that should be added
			PreferenceList preferenceList = body.getPreferenceList();

			// a list of preferences that was added
			PreferenceList addedPreferenceList = new PreferenceList();

			if (preferenceList.size() != 0) {
				for (int i = 0; i < preferenceList.size(); i++) {
					// create unique preference identifier
					Identifier preferenceId = new Identifier().type(CredentialIdentifiers_1_0.PREFERENCEID.getCoding())
							.namespace(Constants.CREDENTIAL_NAMESPACE).value(UUID.randomUUID().toString());
					// preferenceList.get(i).setPreferenceId(preferenceId);
					Preference preference = preferenceList.get(i);

					Preference toStorePreference = new Preference();

					toStorePreference.accountId(preference.getAccountId())
							.preferenceCreationTime(preference.getPreferenceCreationTime())
							.preferenceDetails(preference.getPreferenceDetails()).preferenceId(preferenceId)
							.preferenceType(preference.getPreferenceType());

					mc.insertOne(toStorePreference);
					addedPreferenceList.add(toStorePreference);
				}

				logger.info("Preferences were added {}", addedPreferenceList.toString());

				AddPreferencesResponse response = new AddPreferencesResponse().preferenceList(addedPreferenceList);
				return Response.ok().entity(response).build();
			} else {
				// No preference added
				logger.info("No preference added {}", body.getPreferenceList().toString());
				return Response.status(400).build();
			}
		} finally {
			mongoClient.close();
		}
	}

	@Override
	public Response notificationManagementServiceDeletePreferencesPost(DeletePreferencesRequest body,
			SecurityContext securityContext) throws NotFoundException {
		logger.info("A list of preference that should be deleted {}", body.getPreferenceList().toString());

		mongoDBinit();

		try {
			// a list of preferences that should be deleted
			PreferenceList preferenceList = body.getPreferenceList();

			// a list of preferences that was deleted
			PreferenceList deletedPreferenceList = new PreferenceList();

			for (int i = 0; i < preferenceList.size(); i++) {
				Identifier preferenceId = preferenceList.get(i).getPreferenceId();
				Preference newPreference = null;
				newPreference = mc.find(eq("preferenceId.value", preferenceId.getValue())).first();
				if (newPreference != null) {
					deletedPreferenceList.add(newPreference);
					mc.findOneAndDelete(eq("preferenceId.value", preferenceId.getValue()));
				} else {
					// No preference deletet
					logger.info("No preference deletet {}", body.getPreferenceList().toString());
					return Response.status(400).build();
				}
			}

			logger.info("Preferences were deleted {}", deletedPreferenceList.toString());

			DeletePreferencesResponse response = new DeletePreferencesResponse().preferenceList(deletedPreferenceList);
			return Response.ok().entity(response).build();
		} finally {
			mongoClient.close();
		}
	}

	@Override
	public Response notificationManagementServiceGetPreferencesPost(GetPreferencesRequest body,
			SecurityContext securityContext) throws NotFoundException {

		if (body.getAccountId() != null) {
			logger.info("Request a preference with account id {}", body.getAccountId().getValue());
		}

		mongoDBinit();

		try {
			// user accountId of preference, that should be gotten
			Identifier accountId = body.getAccountId();

			// get filter settings
			List<KeyValue> preferenceFilters = body.getPreferenceDetailFilter();

			// get a preference list that belong to the given accountId
			PreferenceList newPreferenceList = findPreferenceById(mc, accountId, "accountId.value", preferenceFilters);

			logger.info("Found a preference list {}", newPreferenceList.toString());
			if (newPreferenceList.size() != 0) {
				GetPreferencesResponse response = new GetPreferencesResponse().preferenceList(newPreferenceList);
				return Response.ok().entity(response).build();
			} else {
				// No preference found
				logger.info("No preference found for request {}", body);
				return Response.status(200).entity(new GetPreferencesResponse()).build();
			}
		} finally {
			mongoClient.close();
		}
	}

	@Override
	public Response notificationManagementServiceResetPreferencesPost(ResetPreferencesRequest body,
			SecurityContext securityContext) throws NotFoundException {

		// Hardcoding init for default preferences
		final String PREFERENCE1 = "a1:preference 1";
		final String PREFERENCE2 = "a1:preference 2";
		Preference preference1 = new Preference();
		Preference preference2 = new Preference();
		Identifier preferenceId1 = new Identifier();
		Identifier preferenceId2 = new Identifier();
		preferenceId1.type(CredentialIdentifiers_1_0.PREFERENCEID.getCoding()).namespace(Constants.CREDENTIAL_NAMESPACE)
				.setValue(PREFERENCE1);
		preferenceId2.type(CredentialIdentifiers_1_0.PREFERENCEID.getCoding()).namespace(Constants.CREDENTIAL_NAMESPACE)
				.setValue(PREFERENCE2);

		logger.info("Preferences should be reset for accountId {}", body.getAccountId().getValue());
		mongoDBinit();

		try {

			// user accountId of preference, that should be reset
			Identifier accountId = body.getAccountId();
			// get a preference list that belong to the given accountId
			PreferenceList preferenceListThatShouldBeReset = findPreferenceById(mc, accountId, "accountId.value");
			logger.info("Preferences that should be reset {}", preferenceListThatShouldBeReset.toString());

			// Hardcoding for default preferences
			PreferenceList defaultPreferences = new PreferenceList();
			preference1.setAccountId(accountId);
			preference2.setAccountId(accountId);
			preference1.setPreferenceId(preferenceId1);
			preference2.setPreferenceId(preferenceId2);
			defaultPreferences.add(preference1);
			defaultPreferences.add(preference2);

			if (preferenceListThatShouldBeReset.size() != 0) {
				mc.deleteMany(eq("accountId.value", accountId.getValue()));
				mc.insertMany(defaultPreferences);

				logger.info("Preferences were reset and default preferences are {}", defaultPreferences.toString());

				ResetPreferencesResponse response = new ResetPreferencesResponse().preferenceList(defaultPreferences);
				return Response.ok().entity(response).build();
			} else {
				// No preference exist
				logger.info("No preference exist for this accountId {}", body.getAccountId().toString());
				return Response.status(400).build();
			}
		} finally {
			mongoClient.close();
		}
	}

	private void mongoDBinit() {
		mongoClient = new NotificationMongoClient();
		db = mongoClient.getDatabase(DB_NAME);
		mc = db.getCollection(COLLECTION_NAME, Preference.class);
	}

	/**
	 * Finds a preference list by any identifier. If no preference can be found
	 * the list is empty
	 * 
	 * @param mc
	 *            the mongo collection
	 * @param identifier
	 *            the expected value
	 * @param fieldName
	 *            the fieldname to filter
	 * @return returns the preference list.
	 */
	private PreferenceList findPreferenceById(MongoCollection<Preference> mc, Identifier identifier, String fieldName) {
		PreferenceList preferences = new PreferenceList();

		FindIterable<Preference> preferencesMongo = mc.find(eq(fieldName, identifier.getValue()));

		MongoCursor<Preference> cursor = preferencesMongo.iterator();
		while (cursor.hasNext()) {
			Preference preference = cursor.next();
			preferences.add(preference);
		}
		return preferences;
	}

	/**
	 * Finds a preference list by any identifier. If no preference can be found
	 * the list is empty
	 * 
	 * @param mc
	 *            the mongo collection
	 * @param identifier
	 *            the expected value
	 * @param fieldName
	 *            the fieldname to filter
	 * @param keyValues
	 *            the key Values of the preference Details to filter
	 * @return returns the preference list.
	 */
	private PreferenceList findPreferenceById(MongoCollection<Preference> mc, Identifier identifier, String fieldName,
			List<KeyValue> keyValues) {
		PreferenceList preferences = new PreferenceList();
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> andDetails = new ArrayList<BasicDBObject>();
		for (KeyValue keyValue : keyValues) {
			BasicDBObject preferenceDetailElemMatch = new BasicDBObject();
			BasicDBObject elemMatch = new BasicDBObject();
			BasicDBObject and = new BasicDBObject();
			List<BasicDBObject> andItems = new ArrayList<BasicDBObject>();
			if(keyValue.getKey().getCode()!=null) {
				andItems.add(new BasicDBObject("key.code", keyValue.getKey().getCode()));
			}
			if(keyValue.getKey().getDisplay()!=null) {
				andItems.add(new BasicDBObject("key.display", keyValue.getKey().getDisplay()));
			}
			if(keyValue.getKey().getVersion()!=null) {
				andItems.add(new BasicDBObject("key.version", keyValue.getKey().getVersion()));
			}
			if(keyValue.getKey().getSystem()!=null) {
				andItems.add(new BasicDBObject("key.system", keyValue.getKey().getSystem()));
			}
			if(keyValue.getValue()!=null){
				andItems.add(new BasicDBObject("value", keyValue.getValue()));
			}
			and.put("$and", andItems);
			elemMatch.put("$elemMatch", and);
			preferenceDetailElemMatch.put("preferenceDetails", elemMatch);
			andDetails.add(preferenceDetailElemMatch);
		}
		if (identifier != null) {
			andDetails.add(new BasicDBObject(fieldName, identifier.getValue()));
		}
		andQuery.put("$and", andDetails);
		
		FindIterable<Preference> preferencesMongo = mc.find(andQuery);

		MongoCursor<Preference> cursor = preferencesMongo.iterator();
		while (cursor.hasNext()) {
			Preference preference = cursor.next();
			preferences.add(preference);
		}
		return preferences;
	}
}
