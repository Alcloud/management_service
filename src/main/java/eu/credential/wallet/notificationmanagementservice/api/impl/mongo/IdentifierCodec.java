package eu.credential.wallet.notificationmanagementservice.api.impl.mongo;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import eu.credential.wallet.notificationmanagementservice.model.Coding;
import eu.credential.wallet.notificationmanagementservice.model.Identifier;

public class IdentifierCodec implements Codec<Identifier> {

	protected final CodecRegistry codecRegistry;

	public IdentifierCodec(final CodecRegistry codecRegistry) {
		this.codecRegistry = codecRegistry;
	}

	@Override
	public void encode(BsonWriter writer, Identifier value, EncoderContext encoderContext) {

		String namespace = value.getNamespace();
		Coding coding = value.getType();
		String identifierValue = value.getValue();

		writer.writeStartDocument();
		if(namespace != null){
			writer.writeName("namespace");
			writer.writeString(namespace);			
		}
		if (coding != null) {
			Codec<Coding> codingCodec = codecRegistry.get(Coding.class);
			writer.writeName("type");
			encoderContext.encodeWithChildContext(codingCodec, writer, coding);
		}
		if(identifierValue != null){
			writer.writeName("value");
			writer.writeString(identifierValue);			
		}
		writer.writeEndDocument();
	}

	@Override
	public Class<Identifier> getEncoderClass() {
		return Identifier.class;
	}

	@Override
	public Identifier decode(BsonReader reader, DecoderContext decoderContext) {
		
		Identifier identifier = new Identifier();
		reader.readStartDocument();
		
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
		    String fieldName = reader.readName();
		    if("_id".equals(fieldName)){
		    	reader.readObjectId();
		    }
		    else if("namespace".equals(fieldName)){
		    	identifier.setNamespace(reader.readString());
		    }
		    else if("type".equals(fieldName)){
		    	Codec<Coding> codingCodec = codecRegistry.get(Coding.class);
				Coding coding = codingCodec.decode(reader, decoderContext);
				identifier.setType(coding);
		    }
		    else if("value".equals(fieldName)){
		    	identifier.setValue(reader.readString());
		    } else {
		    	reader.readUndefined();
		    }
		}

		reader.readEndDocument();

		return identifier;
		
	}

}
