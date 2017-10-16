package eu.credential.wallet.notificationmanagementservice.api.impl.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.credential.wallet.notificationmanagementservice.model.KeyValue;
import eu.credential.wallet.notificationmanagementservice.model.Preference;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import eu.credential.wallet.notificationmanagementservice.model.Coding;
import eu.credential.wallet.notificationmanagementservice.model.Identifier;

public class PreferenceCodec implements Codec<Preference> {

	protected final CodecRegistry codecRegistry;

	public PreferenceCodec(final CodecRegistry codecRegistry) {
		this.codecRegistry = codecRegistry;
	}

	@Override
	public void encode(BsonWriter writer, Preference value, EncoderContext encoderContext) {

		writer.writeStartDocument();

		// Write account id if available
		if (value.getAccountId() != null) {
			Identifier identifier = value.getAccountId();
			Codec<Identifier> identifierCodec = codecRegistry.get(Identifier.class);
			writer.writeName("accountId");
			encoderContext.encodeWithChildContext(identifierCodec, writer, identifier);
		}

		if (value.getPreferenceId() != null) {
			Identifier identifier = value.getPreferenceId();
			Codec<Identifier> identifierCodec = codecRegistry.get(Identifier.class);
			writer.writeName("preferenceId");
			encoderContext.encodeWithChildContext(identifierCodec, writer, identifier);
		}

		if (value.getPreferenceType() != null) {
			Coding coding = value.getPreferenceType();
			Codec<Coding> codingCodec = codecRegistry.get(Coding.class);
			writer.writeName("preferenceType");
			encoderContext.encodeWithChildContext(codingCodec, writer, coding);
		}

		if (value.getPreferenceCreationTime() != null) {
			writer.writeDateTime("preferenceCreationTime", value.getPreferenceCreationTime().getTime());
		}

		if (value.getPreferenceDetails() != null) {

			List<KeyValue> preferences = value.getPreferenceDetails();
			Codec<List> codingList = codecRegistry.get(List.class);
			writer.writeName("preferenceDetails");
			encoderContext.encodeWithChildContext(codingList, writer, preferences);
		}
		writer.writeEndDocument();
	}

	@Override
	public Class<Preference> getEncoderClass() {
		return Preference.class;
	}

	@Override
	public Preference decode(BsonReader reader, DecoderContext decoderContext) {
		Preference preference = new Preference();
		reader.readStartDocument();

		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
			String fieldName = reader.readName();

			if ("_id".equals(fieldName)) {
				reader.readObjectId();
			} else if ("accountId".equals(fieldName)) {
				Codec<Identifier> identifierCodec = codecRegistry.get(Identifier.class);
				Identifier accountId = identifierCodec.decode(reader, decoderContext);
				preference.setAccountId(accountId);
			} else if ("preferenceType".equals(fieldName)) {
				Codec<Coding> codingCodec = codecRegistry.get(Coding.class);
				Coding preferenceType = codingCodec.decode(reader, decoderContext);
				preference.setPreferenceType(preferenceType);
			} else if ("preferenceId".equals(fieldName)) {
				Codec<Identifier> identifierCodec = codecRegistry.get(Identifier.class);
				Identifier preferenceId = identifierCodec.decode(reader, decoderContext);
				preference.setPreferenceId(preferenceId);
			} else if ("preferenceCreationTime".equals(fieldName)) {
				Long time = reader.readDateTime();
				preference.setPreferenceCreationTime(new Date(time));
			} else if ("preferenceDetails".equals(fieldName)) {
				Codec<List> codingList = codecRegistry.get(List.class);
				List keyValueList = codingList.decode(reader, decoderContext);
				preference.setPreferenceDetails(keyValueList);
			}
		}

		reader.readEndDocument();
	
		return preference;
	
	}

}
