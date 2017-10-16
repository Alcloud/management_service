package eu.credential.wallet.notificationmanagementservice.api.impl.mongo;

import eu.credential.wallet.notificationmanagementservice.model.KeyValue;
import eu.credential.wallet.notificationmanagementservice.model.Preference;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import eu.credential.wallet.notificationmanagementservice.model.Coding;
import eu.credential.wallet.notificationmanagementservice.model.Identifier;

public class PreferenceCodecProvider implements CodecProvider {

	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if(clazz == Preference.class) {
			return (Codec<T>) new PreferenceCodec(registry);
		}
		if(clazz == Identifier.class) {
			return (Codec<T>) new IdentifierCodec(registry);
		}
		if(clazz == Coding.class) {
			return (Codec<T>) new CodingCodec();
		}
		if(clazz == KeyValue.class) {
			return (Codec<T>) new KeyValueCodec(registry);
		}
		return null;
	}

}
