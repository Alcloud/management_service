package eu.credential.wallet.notificationmanagementservice.api.impl.mongo;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;

import eu.credential.wallet.notificationmanagementservice.model.Coding;

public class CodingCodec implements Codec<Coding> {

	protected Codec<Document> documentCodec;

	public CodingCodec() {
		documentCodec = new DocumentCodec();
	}

	@Override
	public void encode(BsonWriter writer, Coding value, EncoderContext encoderContext) {

		Document doc = new Document();

		writer.writeStartDocument();
		writer.writeName("code");
		if (value.getCode() != null) {
			writer.writeString(value.getCode());
		} else {
			writer.writeNull();
		}
		writer.writeName("display");
		if (value.getDisplay() != null) {
			writer.writeString(value.getDisplay());
		} else {
			writer.writeNull();
		}
		writer.writeName("version");
		if (value.getVersion() != null) {
			writer.writeString(value.getVersion());
		} else {
			writer.writeNull();
		}
		writer.writeName("system");
		if (value.getSystem() != null) {
			writer.writeString(value.getSystem());
		} else {
			writer.writeNull();
		}
		writer.writeEndDocument();
	}

	@Override
	public Class<Coding> getEncoderClass() {
		return Coding.class;
	}

	@Override
	public Coding decode(BsonReader reader, DecoderContext decoderContext) {
		Coding coding = new Coding();

		reader.readStartDocument();

		BsonType type;
		while ((type = reader.readBsonType()) != BsonType.END_OF_DOCUMENT) {
			String fieldName = reader.readName();
			if ("_id".equals(fieldName)) {
				reader.readObjectId();
			}

			else {
				if (type == BsonType.NULL) {
					reader.readNull();
				} else {
					if ("system".equals(fieldName)) {
						coding.setSystem(reader.readString());
					} else if ("version".equals(fieldName)) {
						coding.setVersion(reader.readString());
					} else if ("display".equals(fieldName)) {
						coding.setDisplay(reader.readString());
					} else if ("code".equals(fieldName)) {
						coding.setCode(reader.readString());
					}
				}
			}

		}
		reader.readEndDocument();
		return coding;
	}
}
