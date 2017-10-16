package eu.credential.wallet.notificationmanagementservice.api.impl.mongo;

import eu.credential.wallet.notificationmanagementservice.model.Coding;
import eu.credential.wallet.notificationmanagementservice.model.KeyValue;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Created by Administrator on 12.05.2017.
 */
public class KeyValueCodecProvider implements CodecProvider {

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == KeyValue.class) {
            return (Codec<T>) new KeyValueCodec(registry);
        }
        if (clazz == Coding.class) {
            return (Codec<T>) new CodingCodec();
        }
        return null;
    }
}
