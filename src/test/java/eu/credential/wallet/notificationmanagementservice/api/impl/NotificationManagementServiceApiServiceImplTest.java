package eu.credential.wallet.notificationmanagementservice.api.impl;

import java.util.Arrays;
import java.util.Iterator;

import javax.ws.rs.core.Response;

import org.bson.conversions.Bson;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;

import eu.credential.wallet.notificationmanagementservice.model.GetPreferencesRequest;
import eu.credential.wallet.notificationmanagementservice.model.GetPreferencesResponse;
import eu.credential.wallet.notificationmanagementservice.model.Identifier;
import eu.credential.wallet.notificationmanagementservice.model.Preference;
import eu.credential.wallet.notificationmanagementservice.model.PreferenceList;
import mockit.Expectations;
import mockit.Mocked;

/**
 * Testclass for the Notification Management Service Backend implementation
 */
public class NotificationManagementServiceApiServiceImplTest {

	protected static final Logger logger = org.slf4j.LoggerFactory
			.getLogger(NotificationManagementServiceApiServiceImplTest.class);

	@Mocked
	MongoClient mongo;

	protected Preference a1p1;
	protected Preference a1p2;
	protected Preference a1p3;
	protected Preference a2p1;
	protected Preference a3p1;

	@Before
	/**
	 * This init method sets up an embedded mongodb database with the given port
	 * from the test/resources/service.properties
	 *
	 * Fills the database with multiple preferences for different accounts
	 */
	public void initDatabase() {

		a1p1 = NotificationHelper.createPreference("account 1", "a1:preference 1");
		a1p2 = NotificationHelper.createPreference("account 1", "a1:preference 2");
		a1p3 = NotificationHelper.createPreference("account 1", "a1:preference 3");
		a2p1 = NotificationHelper.createPreference("account 2", "a2:preference 1");
		a3p1 = NotificationHelper.createPreference("account 3", "a3:preference 1");

	}

	@After
	/**
	 *
	 */
	public void stopDatabase() {

	}

	@Test
	public void testShouldSuccessfullyGetPreferenceIfAvailable() {

		new Expectations() {
			{
				mongo.getDatabase("notifications").getCollection("preferences", Preference.class).find((Bson) any)
						.iterator();
				result = new MongoCursor<Preference>() {
					
					Iterator<Preference> preferences = Arrays.asList(a1p1, a1p2, a1p3).iterator();

					@Override
					public void close() {
						
					}

					@Override
					public boolean hasNext() {
						return preferences.hasNext();
					}

					@Override
					public Preference next() {
						return preferences.next();
					}

					@Override
					public void remove() {

					}

					@Override
					public Preference tryNext() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ServerCursor getServerCursor() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public ServerAddress getServerAddress() {
						// TODO Auto-generated method stub
						return null;
					}

					
				};
			}
		};

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

}
