package eu.credential.wallet.externalresources;

import org.junit.rules.ExternalResource;

import com.mongodb.MongoClient;

import eu.credential.wallet.notificationmanagementservice.api.impl.mongo.NotificationMongoClient;
import eu.credential.wallet.notificationmanagementservice.model.Preference;

/**
 * This class initializes a database with enough test entries.
 * 
 * It creates a "notifications" collection with three different notification entries.
 * 
 * @author tfl
 *
 */
public class PreferencesResource extends ExternalResource {

	@Override
	public void before() throws Throwable {
		MongoClient mongo = new NotificationMongoClient();
		
		// Create three different notifications
		Preference preference = new Preference();
		
		
	}

	@Override
	public void after() {

	}

}
