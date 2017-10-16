package eu.credential.wallet.notificationmanagementservice.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ws.rs.core.Response;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import eu.credential.wallet.notificationmanagementservice.api.impl.mongo.NotificationMongoClient;
import eu.credential.wallet.notificationmanagementservice.model.AddPreferencesRequest;
import eu.credential.wallet.notificationmanagementservice.model.AddPreferencesResponse;
import eu.credential.wallet.notificationmanagementservice.model.Coding;
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

public class ITNotificationManagementServiceApiServiceImpl {
	protected static final Logger logger = org.slf4j.LoggerFactory
			.getLogger(ITNotificationManagementServiceApiServiceImpl.class);

	protected static MongoClient mongo;

	protected static Preference a1p1;
	protected static Preference a1p2;
	protected static Preference a1p3;
	protected static Preference a2p1;
	protected static Preference a3p1;

	@BeforeClass
	/**
	 * This init method sets up an embedded mongodb database with the given port
	 * from the test/resources/service.properties
	 */
	public static void initDatabase() {

		logger.info("Fill dummy Data");

		mongo = new NotificationMongoClient();
		mongo.dropDatabase("notifications");
		MongoDatabase db = mongo.getDatabase("notifications");

		MongoCursor<String> collectionNames = db.listCollectionNames().iterator();
		boolean exists = false;
		while (collectionNames.hasNext()) {
			String collectionName = collectionNames.next();
			if ("preferences".equals(collectionName)) {
				exists = true;
			}
		}
		if (exists) {
			mongo.dropDatabase("notifications");
		}
		db.createCollection("preferences");
		MongoCollection<Preference> mc = db.getCollection("preferences", Preference.class);

		GregorianCalendar calendara1n1 = new GregorianCalendar();
		calendara1n1.set(2010, 10, 10, 10, 10);
		GregorianCalendar calendara1n2 = new GregorianCalendar();
		calendara1n2.set(2010, 10, 11, 10, 10);
		GregorianCalendar calendara1n3 = new GregorianCalendar();
		calendara1n3.set(2010, 10, 12, 10, 10);
		GregorianCalendar calendara2n1 = new GregorianCalendar();
		calendara2n1.set(2010, 11, 10, 10, 10);
		GregorianCalendar calendara3n1 = new GregorianCalendar();
		calendara3n1.set(2010, 12, 10, 10, 10);
		
		KeyValue documentid = new KeyValue();
		documentid.setKey(new Coding().system("https://credential.eu/config/codesystems/preferenceitemkey").code("documentid"));
		documentid.setValue("abcdef");

		a1p1 = NotificationHelper.createPreference("account 1", "a1:preference 1", calendara1n1.getTime());
		a1p2 = NotificationHelper.createPreference("account 1", "a1:preference 2", calendara1n2.getTime());
		a1p3 = NotificationHelper.createPreference("account 1", "a1:preference 3", calendara1n3.getTime(), documentid);
		a2p1 = NotificationHelper.createPreference("account 2", "a2:preference 1", calendara2n1.getTime());
		a3p1 = NotificationHelper.createPreference("account 3", "a3:preference 1", calendara3n1.getTime(), documentid);

		List<Preference> preferences = Arrays.asList(a1p1, a1p2, a1p3, a2p1, a3p1);
		mc.insertMany(preferences);

		logger.info("Successful filled dummy Data");
	}

	@AfterClass
	/**
	 *
	 */
	public static void stopDatabase() {
		mongo.dropDatabase("notifications");

		if (mongo != null) {
			mongo.close();
		}
	}

	@Test
	public void shouldSuccessfullyAddAPreference() {

		Identifier account = new Identifier();
		account.setValue("Hans August");
		account.setNamespace("a");

		logger.info("Testing: add a preference for User {}", account.getValue());

		NotificationManagementServiceApiServiceImpl managementService = new NotificationManagementServiceApiServiceImpl();

		AddPreferencesRequest body = new AddPreferencesRequest();

		Preference preference = new Preference();

		preference.setAccountId(account);
		Coding coding = new Coding();
		coding.setSystem("a");
		coding.setCode("a");
		preference.setPreferenceType(coding);

		body.setPreferenceList(new PreferenceList());
		body.getPreferenceList().add(preference);

		Response resp = managementService.notificationManagementServiceAddPreferencesPost(body, null);

		PreferenceList preferenceList = ((AddPreferencesResponse) resp.getEntity()).getPreferenceList();

		Assert.assertThat(preferenceList.size(), Matchers.is(1));
	}

	@Test
	public void testShouldSuccessfullyGetAPreferenceIfAvailablea1n1() {

		logger.info("Testing: get a specific preference.");

		NotificationManagementServiceApiServiceImpl managementService = new NotificationManagementServiceApiServiceImpl();

		GetPreferencesRequest body = new GetPreferencesRequest();

		Identifier accountId = new Identifier();

		accountId.setNamespace("https://credential.eu/config/codesystems/identifiers");
		accountId.setValue("account 1");

		body.setAccountId(accountId);

		Response resp = managementService.notificationManagementServiceGetPreferencesPost(body, null);

		PreferenceList preference = ((GetPreferencesResponse) resp.getEntity()).getPreferenceList();
		Assert.assertThat(preference.size(), Matchers.is(3));
		Assert.assertThat(preference.get(0).getAccountId().getValue(), Matchers.is("account 1"));
		Assert.assertThat(preference.get(0).getPreferenceId().getValue(), Matchers.is("a1:preference 1"));
		Assert.assertThat(resp.getStatus(), Matchers.is(200));
	}

	@Test
	public void testShouldSuccessfullyGetAPreferenceIfAvailablea2n1() {

		logger.info("Testing: get a specific preference.");

		NotificationManagementServiceApiServiceImpl managementService = new NotificationManagementServiceApiServiceImpl();

		GetPreferencesRequest body = new GetPreferencesRequest();

		Identifier accountId = new Identifier();

		accountId.setNamespace("https://credential.eu/config/codesystems/identifiers");
		accountId.setValue("account 2");

		body.setAccountId(accountId);

		Response resp = managementService.notificationManagementServiceGetPreferencesPost(body, null);

		PreferenceList preference = ((GetPreferencesResponse) resp.getEntity()).getPreferenceList();
		Assert.assertThat(preference.size(), Matchers.is(1));
		Assert.assertThat(preference.get(0).getAccountId().getValue(), Matchers.is("account 2"));
		Assert.assertThat(preference.get(0).getPreferenceId().getValue(), Matchers.is("a2:preference 1"));
		Assert.assertThat(resp.getStatus(), Matchers.is(200));
	}

	@Test
	public void testShouldSuccessfullyGetAPreferenceIfAvailablea3n1() {

		logger.info("Testing: get a specific preference.");

		NotificationManagementServiceApiServiceImpl managementService = new NotificationManagementServiceApiServiceImpl();

		GetPreferencesRequest body = new GetPreferencesRequest();

		Identifier accountId = new Identifier();

		accountId.setNamespace("https://credential.eu/config/codesystems/identifiers");
		accountId.setValue("account 3");

		body.setAccountId(accountId);

		Response resp = managementService.notificationManagementServiceGetPreferencesPost(body, null);

		PreferenceList preference = ((GetPreferencesResponse) resp.getEntity()).getPreferenceList();
		Assert.assertThat(preference.size(), Matchers.is(1));
		Assert.assertThat(preference.get(0).getAccountId().getValue(), Matchers.is("account 3"));
		Assert.assertThat(preference.get(0).getPreferenceId().getValue(), Matchers.is("a3:preference 1"));
		Assert.assertThat(resp.getStatus(), Matchers.is(200));
	}

	@Test
	public void testShouldGetAPreferenceBasedOnFilterNotificationDetails() {
		logger.info("Get All preferences with a specific notification details key value pair.");

		NotificationManagementServiceApiServiceImpl managementService = new NotificationManagementServiceApiServiceImpl();

		GetPreferencesRequest body = new GetPreferencesRequest();

		List<KeyValue> preferenceFilter = new ArrayList<KeyValue>();

		KeyValue documentid = new KeyValue();

		Coding documentidKey = new Coding();

		documentidKey.setSystem("https://credential.eu/config/codesystems/preferenceitemkey");
		documentidKey.setCode("documentid");

		documentid.setKey(documentidKey);
		documentid.setValue("abcdef");

		preferenceFilter.add(documentid);

		body.setPreferenceDetailFilter(preferenceFilter);

		Response resp = managementService.notificationManagementServiceGetPreferencesPost(body, null);

		PreferenceList preference = ((GetPreferencesResponse) resp.getEntity()).getPreferenceList();

		Assert.assertThat(preference.size(), Matchers.is(2));
		Assert.assertThat(preference, Matchers.containsInAnyOrder(new TypeSafeMatcher<Preference>() {

			@Override
			public void describeTo(Description arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			protected boolean matchesSafely(Preference arg0) {
				return arg0.getAccountId().getValue().equals("account 1") && arg0.getPreferenceId().getValue().equals("a1:preference 3");
			}
		}, new TypeSafeMatcher<Preference>() {

			@Override
			public void describeTo(Description arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			protected boolean matchesSafely(Preference arg0) {
				return arg0.getAccountId().getValue().equals("account 3") && arg0.getPreferenceId().getValue().equals("a3:preference 1");
			}

		}));
		Assert.assertThat(resp.getStatus(), Matchers.is(200));
	}

	@Test
	public void shouldReturnEmptyResponseWhenPreferenceIsNotAvailable() {
		logger.info("Testing: get a specific preference.");

		NotificationManagementServiceApiServiceImpl managementService = new NotificationManagementServiceApiServiceImpl();

		GetPreferencesRequest body = new GetPreferencesRequest();

		Identifier preferenceId = new Identifier();

		preferenceId.setNamespace("https://credential.eu/config/codesystems/identifiers");
		preferenceId.setValue("a4:preference 2");
		body.setAccountId(preferenceId);

		Response resp = managementService.notificationManagementServiceGetPreferencesPost(body, null);

		PreferenceList preference = ((GetPreferencesResponse) resp.getEntity()).getPreferenceList();
		
		Assert.assertNull(preference);

	}

	@Test
	public void testShouldSuccessfullyDeleteAPreferenceIfAvailablea1n1() {

		logger.info("Testing: delete a specific preference.");

		NotificationManagementServiceApiServiceImpl managementService = new NotificationManagementServiceApiServiceImpl();

		DeletePreferencesRequest body = new DeletePreferencesRequest();

		Identifier preferenceId = new Identifier();

		preferenceId.setNamespace("https://credential.eu/config/codesystems/identifiers");
		preferenceId.setValue("a2:preference 1");

		Preference preference = new Preference().preferenceId(preferenceId);

		body.setPreferenceList(new PreferenceList());
		body.getPreferenceList().add(preference);

		Response resp = managementService.notificationManagementServiceDeletePreferencesPost(body, null);

		PreferenceList preferences = ((DeletePreferencesResponse) resp.getEntity()).getPreferenceList();
		Assert.assertThat(preferences.size(), Matchers.is(1));
		Assert.assertThat(preferences.get(0).getPreferenceId().getValue(), Matchers.is("a2:preference 1"));
		Assert.assertThat(resp.getStatus(), Matchers.is(200));
	}

	@Test
	public void testShouldSuccessfullyResetAPreferenceIfAvailablea1n1() {

		logger.info("Testing: reset preferences.");

		NotificationManagementServiceApiServiceImpl managementService = new NotificationManagementServiceApiServiceImpl();

		ResetPreferencesRequest body = new ResetPreferencesRequest();

		Identifier accountId = new Identifier();

		accountId.setNamespace("https://credential.eu/config/codesystems/identifiers");
		accountId.setValue("account 1");

		body.setAccountId(accountId);

		Response resp = managementService.notificationManagementServiceResetPreferencesPost(body, null);

		PreferenceList preferences = ((ResetPreferencesResponse) resp.getEntity()).getPreferenceList();
		Assert.assertThat(preferences.size(), Matchers.is(2));
		Assert.assertThat(preferences.get(0).getAccountId().getValue(), Matchers.is("account 1"));
		Assert.assertThat(preferences.get(0).getPreferenceId().getValue(), Matchers.is("a1:preference 1"));
		Assert.assertThat(resp.getStatus(), Matchers.is(200));
	}

	protected static class PreferenceMatcher extends BaseMatcher<Preference> {

		protected String accountId;
		protected String preferenceId;

		public PreferenceMatcher(String accountId, String preferenceId) {
			super();
			this.accountId = accountId;
			this.preferenceId = preferenceId;
		}

		@Override
		public boolean matches(Object arg0) {
			if (arg0 instanceof Preference) {
				Preference preference = (Preference) arg0;
				return preference.getAccountId().getValue().equals(accountId)
						&& preference.getPreferenceId().getValue().equals(preferenceId);
			} else {
				return false;
			}
		}

		@Override
		public void describeTo(Description arg0) {
			// TODO Auto-generated method stub

		}
	}
}
