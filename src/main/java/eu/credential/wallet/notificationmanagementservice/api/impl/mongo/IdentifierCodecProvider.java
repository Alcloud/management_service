package eu.credential.wallet.notificationmanagementservice.api.impl.mongo;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import eu.credential.wallet.notificationmanagementservice.model.Coding;
import eu.credential.wallet.notificationmanagementservice.model.Identifier;

public class IdentifierCodecProvider implements CodecProvider {

	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if (clazz == Identifier.class) {
			return (Codec<T>) new IdentifierCodec(registry);
		}
		if (clazz == Coding.class) {
			return (Codec<T>) new CodingCodec();
		}
		return null;
	}

}
