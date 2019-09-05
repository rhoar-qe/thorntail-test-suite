package org.wildfly.swarm.ts.javaee8.jsonp11;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonPatch;
import javax.json.JsonPointer;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonCollectors;
import java.io.StringReader;
import java.util.stream.IntStream;

public class JsonController {
    private static final String JSON_PERSON = "{\"name\":\"Joe Black\",\"age\":35}";

    public String pointerContainsValue(String value) {
        try (StringReader reader = new StringReader(JSON_PERSON)) {
            JsonStructure jStructure = Json.createReader(reader).read();
            JsonPointer jPointer = Json.createPointer("/" + value);
            return String.valueOf(jPointer.containsValue(jStructure));
        }
    }

    public String pointerGetValue(String value) {
        try (StringReader reader = new StringReader(JSON_PERSON)) {
            JsonStructure jStructure = Json.createReader(reader).read();
            JsonPointer jPointer = Json.createPointer("/" + value);
            try {
                return String.valueOf(jPointer.getValue(jStructure));
            } catch (JsonException e) {
                return e.getMessage();
            }
        }
    }

    public String pointerAdd(String pointer, String value) {
        try (StringReader reader = new StringReader(JSON_PERSON)) {
            JsonStructure jStructure = Json.createReader(reader).read();
            JsonPointer jPointer = Json.createPointer("/" + pointer);
            JsonValue jValue = Json.createValue(value);
            jStructure = jPointer.add(jStructure, jValue);
            return String.valueOf(jStructure);
        }
    }

    public String pointerReplace(String pointer, String value) {
        try (StringReader reader = new StringReader(JSON_PERSON)) {
            JsonStructure jStructure = Json.createReader(reader).read();
            JsonPointer jPointer = Json.createPointer("/" + pointer);
            JsonValue jValue = Json.createValue(value);
            try {
                jStructure = jPointer.replace(jStructure, jValue);
                return String.valueOf(jStructure);
            } catch (JsonException e) {
                return e.getMessage();
            }
        }
    }

    public String pointerRemove(String pointer) {
        JsonPointer jPointer = Json.createPointer("/" + pointer);
        try (StringReader reader = new StringReader(JSON_PERSON)) {
            JsonStructure jStructure = Json.createReader(reader).read();
            try {
                jStructure = jPointer.remove(jStructure);
                return String.valueOf(jStructure);
            } catch (JsonException e) {
                return e.getMessage();
            }
        }
    }

    public String patchAdd(String path, String value) {
        JsonPatch jPatch = Json.createPatchBuilder().add("/" + path, value).build();
        try (StringReader reader = new StringReader(JSON_PERSON)) {
            JsonStructure jStructure = Json.createReader(reader).read();
            try {
                return String.valueOf(jPatch.apply(jStructure));
            } catch (JsonException e) {
                return e.getMessage();
            }
        }
    }

    public String patchRemove(String path) {
        JsonPatch jPatch = Json.createPatchBuilder().remove("/" + path).build();
        try (StringReader reader = new StringReader(JSON_PERSON)) {
            JsonStructure jStructure = Json.createReader(reader).read();
            try {
                return String.valueOf(jPatch.apply(jStructure));
            } catch (JsonException e) {
                return e.getMessage();
            }
        }
    }

    public String patchMove(String path, String from) {
        JsonPatch jPatch = Json.createPatchBuilder().move("/" + path, "/" + from).build();
        try (StringReader reader = new StringReader(JSON_PERSON)) {
            JsonStructure jStructure = Json.createReader(reader).read();
            try {
                return String.valueOf(jPatch.apply(jStructure));
            } catch (JsonException e) {
                return e.getMessage();
            }
        }
    }

    public String patchCopy(String path, String from) {
        JsonPatch jPatch = Json.createPatchBuilder().copy("/" + path, "/" + from).build();
        try (StringReader reader = new StringReader(JSON_PERSON)) {
            JsonStructure jStructure = Json.createReader(reader).read();
            try {
                return String.valueOf(jPatch.apply(jStructure));
            } catch (JsonException e) {
                return e.getMessage();
            }
        }
    }

    public String patchReplace(String path, String value) {
        JsonPatch jPatch = Json.createPatchBuilder().replace("/" + path, value).build();
        try (StringReader reader = new StringReader(JSON_PERSON)) {
            JsonStructure jStructure = Json.createReader(reader).read();
            try {
                return String.valueOf(jPatch.apply(jStructure));
            } catch (JsonException e) {
                return e.getMessage();
            }
        }
    }

    public String mergePatch(boolean minimal) {
        final String mergeJsonString = "{\"name\":\"Alice\",\"age\":35,\"enabled\":true}";
        final String mergeMinimalJsonString = "{\"name\":\"Alice\",\"enabled\":true}";
        String mergeString = minimal ? mergeMinimalJsonString : mergeJsonString;
        try (StringReader readerDefault = new StringReader(JSON_PERSON);
             StringReader readerMerge = new StringReader(mergeString);
        ) {
            JsonStructure structureDefault = Json.createReader(readerDefault).read();
            JsonStructure structureMerge = Json.createReader(readerMerge).read();
            try {
                return String.valueOf(Json.createMergePatch(structureMerge).apply(structureDefault));
            } catch (JsonException e) {
                return e.getMessage();
            }
        }
    }

    public String mergeDiff() {
        final String expectedMergeResult = "{\"name\":\"Alice\",\"age\":35,\"enabled\":true}";
        try (StringReader readerDefault = new StringReader(JSON_PERSON);
             StringReader readerResult = new StringReader(expectedMergeResult);
        ) {
            JsonStructure structureDefault = Json.createReader(readerDefault).read();
            JsonStructure structureResult = Json.createReader(readerResult).read();
            try {
                return String.valueOf(Json.createMergeDiff(structureDefault, structureResult).toJsonValue());
            } catch (JsonException e) {
                return e.getMessage();
            }
        }
    }

    public String jsonCollector() {
        JsonArray array = IntStream.rangeClosed(1, 5)
                .mapToObj(i -> Json.createObjectBuilder()
                        .add("key" + i, "value" + i)
                        .build())
                .collect(JsonCollectors.toJsonArray());
        return String.valueOf(array);
    }
}
