package org.wildfly.swarm.ts.javaee.jaxrs.jsonb;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class ResultAdapter implements JsonbAdapter<Result, JsonObject> {
    @Override
    public JsonObject adaptToJson(Result result) {
        return Json.createObjectBuilder()
                .add("hello", result.hello)
                .build();
    }

    @Override
    public Result adaptFromJson(JsonObject obj) {
        Result result = new Result();
        result.hello = obj.getString("hello");
        return result;
    }
}
