package eu.credential.wallet.notificationmanagementservice.api.impl;

import eu.credential.wallet.notificationmanagementservice.model.Coding;
import eu.credential.wallet.notificationmanagementservice.model.Identifier;
import eu.credential.wallet.notificationmanagementservice.model.KeyValue;
import eu.credential.wallet.notificationmanagementservice.model.Preference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods for the Notification class.
 * 
 * @author tfl
 *
 */
public class NotificationHelper {
	/**
	 * Creates a notification with the given notification id, account id.
	 */
	public static Preference createPreference(String accountId, String preferenceId) {
		Preference preference = new Preference();
		Identifier accountIdIdentifier = new Identifier();

		accountIdIdentifier.setNamespace("https://credential.eu/config/codesystems/identifiers");
		accountIdIdentifier.setValue(accountId);
		Coding accountType = new Coding();
		accountType.setCode("accountId");
		accountType.setDisplay("Account ID of a CREDENTIAL user.");
		accountType.setSystem("https://credential.eu/config/codesystems/identifiers");
		accountType.setVersion("1.0");
		accountIdIdentifier.setType(accountType);
		preference.setAccountId(accountIdIdentifier);

		Identifier preferenceIdIdentifier = new Identifier();

		preferenceIdIdentifier.setNamespace("https://credential.eu/config/codesystems/identifiers");
		preferenceIdIdentifier.setValue(preferenceId);

		Coding preferenceIdType = new Coding();
		preferenceIdType.setCode("notificationId");
		preferenceIdType.setDisplay("ID of a notification.");
		preferenceIdType.setSystem("https://credential.eu/config/codesystems/identifiers");
		preferenceIdType.setVersion("1.0");
		preferenceIdIdentifier.setType(preferenceIdType);
		preference.setPreferenceId(preferenceIdIdentifier);
		preference.setPreferenceCreationTime(new Date());

		Coding preferenceType = new Coding();
		preferenceType.setCode("message");
		preferenceType.setDisplay("The message of the notification");
		preferenceType.setSystem("https://credential.eu/config/codesystems/notificationdetail");
		preferenceType.setVersion("1.0");
		preference.setPreferenceType(preferenceType);

		List<KeyValue> preferenceDetails = new ArrayList<KeyValue>();
		KeyValue keyValue = new KeyValue();

		keyValue.setKey(preferenceType);
		keyValue.setValue("blub");
		preferenceDetails.add(keyValue);

		preference.setPreferenceDetails(preferenceDetails);

		return preference;
	}

	/**
	 * Creates a notification with the given notification id, account id and
	 * creation time.
	 */
	public static Preference createPreference (String accountId, String preferenceId, Date creationTime) {
		Preference preference = createPreference(accountId, preferenceId);
		preference.setPreferenceCreationTime(creationTime);

		return preference;
	}
	
	/**
	 * Creates a notification with the given notification id, account id and
	 * creation time and a specified keyValue pair.
	 */
	public static Preference createPreference (String accountId, String preferenceId, Date creationTime, KeyValue keyValue) {
		Preference preference = createPreference(accountId, preferenceId);
		preference.setPreferenceCreationTime(creationTime);
		preference.getPreferenceDetails().add(keyValue);
		
		return preference;
	}

}
