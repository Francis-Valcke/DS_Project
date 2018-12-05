package com.kuleuven.distributedsystems.dispatcher;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ApplicationServerSerializer extends StdSerializer<ApplicationServer> {

    public ApplicationServerSerializer() {
        this(null);
    }

    public ApplicationServerSerializer(Class<ApplicationServer> t) {
        super(t);
    }

    @Override
    public void serialize(ApplicationServer applicationServer, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", applicationServer.getName());
        jsonGenerator.writeStringField("ip", applicationServer.getIp());
        jsonGenerator.writeNumberField("rmiPort", applicationServer.getRmiPort());
        jsonGenerator.writeNumberField("restPort", applicationServer.getRestPort());
        jsonGenerator.writeEndObject();
    }
}
