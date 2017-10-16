package eu.credential.wallet.externalresources;

import java.io.IOException;

import org.apache.log4j.Level;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import eu.credential.wallet.notificationmanagementservice.api.AppProperties;

/**
 * Sets up a database and cleans it up after a test has been executed. Port and
 * host from the service.properties are used.
 * 
 * @author tfl
 *
 */
public class MongoDatabaseExternalResource extends ExternalResource {

	protected static final Logger logger = LoggerFactory.getLogger(MongoDatabaseExternalResource.class);

	protected static MongodExecutable mongodExecutable = null;
	protected static MongodProcess mongod = null;

	@Override
	protected void before() throws Throwable {
		MongodStarter starter = MongodStarter.getDefaultInstance();
		logger.info("Setup embedded mongodb.");

		// Disable logging on info level
		Level oldLevel = org.apache.log4j.Logger.getRootLogger().getLevel();
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.WARN);

		String bindIp = AppProperties.getInstance().getValue("mongodb.host");
		String port = AppProperties.getInstance().getValue("mongodb.port");

		try {
			IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
					.net(new Net(bindIp, Integer.parseInt(port), Network.localhostIsIPv6())).build();

			mongodExecutable = starter.prepare(mongodConfig);
			mongod = mongodExecutable.start();
		} catch (NumberFormatException | IOException e) {
			logger.error("Could not set up embedded mongodb.", e);
		}
		org.apache.log4j.Logger.getRootLogger().setLevel(oldLevel);
		
		logger.info("Successfully started embedded mongodb.");

	}

	@Override
	protected void after() {
		logger.info("Stop database");
		if (mongodExecutable != null) {
			mongodExecutable.stop();
		}
	}

}
