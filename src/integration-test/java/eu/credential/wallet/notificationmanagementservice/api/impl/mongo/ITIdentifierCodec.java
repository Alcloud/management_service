package eu.credential.wallet.notificationmanagementservice.api.impl.mongo;

import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import eu.credential.wallet.notificationmanagementservice.model.Coding;
import eu.credential.wallet.notificationmanagementservice.model.Identifier;

public class ITIdentifierCodec {

	protected static final Logger logger = LoggerFactory.getLogger(ITIdentifierCodec.class);

	protected static MongoClient mongo;

	@BeforeClass
	public static void initDatabase() {

		logger.info("Fill dummy Data");

		mongo = new NotificationMongoClient();
		mongo.dropDatabase("identifier");

		Identifier identifier = new Identifier();

		identifier.setNamespace("namespace");
		identifier.setValue("the value");

		Coding code = new Coding();

		code.setCode("a code");
		code.setDisplay("a display");
		code.setSystem("a system");
		code.setVersion("a version");

		identifier.setType(code);

		MongoDatabase db = mongo.getDatabase("identifier");

		db.createCollection("testcollection");
		MongoCollection<Identifier> mc = db.getCollection("testcollection", Identifier.class);

		mc.insertOne(identifier);

	}

	@AfterClass
	public static void deinitMongo() {

		mongo.dropDatabase("identifier");

		if (mongo != null) {
			mongo.close();
		}
	}

	@Test
	public void shouldHaveIdentifier() {
		logger.info("Request identifier");

		MongoDatabase db = mongo.getDatabase("identifier");

		MongoCollection<Identifier> mc = db.getCollection("testcollection", Identifier.class);

		FindIterable<Identifier> identifiers = mc.find();

		Identifier identifier = identifiers.first();

		org.junit.Assert.assertThat(identifier.getType().getCode(), Matchers.is("a code"));
		org.junit.Assert.assertThat(identifier.getType().getDisplay(), Matchers.is("a display"));
		org.junit.Assert.assertThat(identifier.getType().getSystem(), Matchers.is("a system"));
		org.junit.Assert.assertThat(identifier.getType().getVersion(), Matchers.is("a version"));
		org.junit.Assert.assertThat(identifier.getNamespace(), Matchers.is("namespace"));
		org.junit.Assert.assertThat(identifier.getValue(), Matchers.is("the value"));
	}

}
