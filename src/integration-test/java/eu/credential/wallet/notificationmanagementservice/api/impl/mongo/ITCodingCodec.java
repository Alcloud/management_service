package eu.credential.wallet.notificationmanagementservice.api.impl.mongo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import org.hamcrest.Matchers;
import org.junit.After;
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

public class ITCodingCodec {

	protected static final Logger logger = LoggerFactory.getLogger(ITCodingCodec.class);

	protected static MongoClient mongo;

	@BeforeClass
	public static void initDatabase() {

		logger.info("Fill dummy Data");

		mongo = new NotificationMongoClient();
		mongo.dropDatabase("coding");

		Coding code = new Coding();

		code.setCode("a code");
		code.setDisplay("a display");
		code.setSystem("a system");
		code.setVersion("a version");

		Coding codeWithOnlyRequired = new Coding();
		codeWithOnlyRequired.setSystem("theOnlySystem");
		codeWithOnlyRequired.setCode("onlyCode");

		MongoDatabase db = mongo.getDatabase("coding");

		db.createCollection("testcollection");
		MongoCollection<Coding> mc = db.getCollection("testcollection", Coding.class);

		mc.insertOne(codeWithOnlyRequired);
		mc.insertOne(code);

	}

	@AfterClass
	public static void deinitMongo() {
		mongo.dropDatabase("coding");

		if (mongo != null) {
			mongo.close();
		}
	}

	@Test
	public void shouldHaveCoding() {

		MongoDatabase db = mongo.getDatabase("coding");

		MongoCollection<Coding> mc = db.getCollection("testcollection", Coding.class);

		Coding coding = mc.find(and(eq("code", "a code"), eq("system", "a system"))).first();

		org.junit.Assert.assertThat(coding.getCode(), Matchers.is("a code"));
		org.junit.Assert.assertThat(coding.getDisplay(), Matchers.is("a display"));
		org.junit.Assert.assertThat(coding.getSystem(), Matchers.is("a system"));
		org.junit.Assert.assertThat(coding.getVersion(), Matchers.is("a version"));

	}

	@Test
	/**
	 * Based on spec only system and code should have values.
	 */
	public void shouldHaveCodingWithOnlyRequiredValues() {
		MongoDatabase db = mongo.getDatabase("coding");

		MongoCollection<Coding> mc = db.getCollection("testcollection", Coding.class);

		Coding coding = mc.find(and(eq("code", "onlyCode"), eq("system", "theOnlySystem"))).first();

		org.junit.Assert.assertThat(coding.getCode(), Matchers.is("onlyCode"));
		org.junit.Assert.assertThat(coding.getSystem(), Matchers.is("theOnlySystem"));
	}

}
