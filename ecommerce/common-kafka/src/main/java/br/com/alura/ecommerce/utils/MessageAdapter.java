package br.com.alura.ecommerce.utils;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

    @Override
    public JsonElement serialize(Message message, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", message.getPayload().getClass().getName());
        obj.add("correlationId", jsonSerializationContext.serialize(message.getId()));
        obj.add("payload", jsonSerializationContext.serialize(message.getPayload()));
        return obj;
    }

    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var obj = jsonElement.getAsJsonObject();
        var payloadType = obj.get("type").getAsString();
        var correlationId = jsonDeserializationContext.deserialize(obj.get("correlationId"), CorrelationID.class);
        try {
            var payload = jsonDeserializationContext.deserialize(obj.get("payload"), Class.forName(payloadType));
            return new Message<>((CorrelationID) correlationId, payload);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }
}
