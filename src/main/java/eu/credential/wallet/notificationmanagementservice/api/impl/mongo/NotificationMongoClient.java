package eu.credential.wallet.notificationmanagementservice.api.impl.mongo;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

import eu.credential.wallet.notificationmanagementservice.api.AppProperties;

/**
 * This class extends the MongoClient and initializes it with the proper
 * configuration values. In particular the host and port from the
 * service.proeprties will be used as well as all self developed Codecs.
 * 
 * @author tfl
 *
 */
public class NotificationMongoClient extends MongoClient {

	protected static final Logger logger = LoggerFactory.getLogger(NotificationMongoClient.class);

	protected static String bindIp;
	protected static String port;

	protected static CodecRegistry codecRegistry;

	static {
		bindIp = AppProperties.getInstance().getValue("mongodb.host");
		port = AppProperties.getInstance().getValue("mongodb.port");
		codecRegistry = CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(new CodingCodec()),
				CodecRegistries.fromProviders(new IdentifierCodecProvider(), new KeyValueCodecProvider(), new PreferenceCodecProvider()),
				MongoClient.getDefaultCodecRegistry());
		
	}

	public NotificationMongoClient() {
		super(new ServerAddress(bindIp, Integer.parseInt(port)),
				MongoClientOptions.builder().codecRegistry(codecRegistry).build());
	}

}
