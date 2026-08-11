import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** One-time converter from the legacy payload string to native Firestore fields. */
public final class NormalizeFirestoreFields {
  private static final String ROOT =
      "https://firestore.googleapis.com/v1/projects/fangmono-7ang/databases/(default)/documents/environments/sit";
  private static final List<String> COLLECTIONS =
      List.of("attendance", "loans", "funds", "paybacks", "bosses", "employees", "sites");

  public static void main(String[] args) throws Exception {
    if (args.length > 1 || (args.length == 1 && !"--apply".equals(args[0]))) {
      throw new IllegalArgumentException("Usage: [--apply]");
    }
    boolean apply = args.length == 1;
    HttpClient http = HttpClient.newHttpClient();

    for (String collection : COLLECTIONS) {
      JsonArray documents = documents(http, collection);
      int legacyCount = 0;
      for (JsonElement element : documents) {
        JsonObject fields = element.getAsJsonObject().getAsJsonObject("fields");
        if (fields.has("payload") && fields.getAsJsonObject("payload").has("stringValue")) legacyCount++;
      }
      System.out.printf("%-10s Documents=%d LegacyPayload=%d%n", collection, documents.size(), legacyCount);
      if (apply && legacyCount != documents.size()) {
        throw new IllegalStateException(collection + " has unexpected fields; refusing conversion.");
      }
      if (apply) {
        for (JsonElement element : documents) {
          JsonObject document = element.getAsJsonObject();
          String payload =
              document.getAsJsonObject("fields").getAsJsonObject("payload").get("stringValue").getAsString();
          overwrite(http, document.get("name").getAsString(), JsonParser.parseString(payload).getAsJsonObject());
        }
        System.out.println("Normalized " + collection);
      }
    }
  }

  private static JsonArray documents(HttpClient http, String collection) throws Exception {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create(ROOT + "/" + collection + "?pageSize=1000")).GET().build();
    HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() != 200) throw new IllegalStateException(response.body());
    JsonObject result = JsonParser.parseString(response.body()).getAsJsonObject();
    return result.has("documents") ? result.getAsJsonArray("documents") : new JsonArray();
  }

  private static void overwrite(HttpClient http, String documentName, JsonObject document) throws Exception {
    JsonObject body = new JsonObject();
    body.add("fields", firestoreFields(document));
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("https://firestore.googleapis.com/v1/" + documentName))
            .header("Content-Type", "application/json")
            .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
            .build();
    HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() != 200) throw new IllegalStateException(response.body());
  }

  private static JsonObject firestoreFields(JsonObject document) {
    JsonObject fields = new JsonObject();
    document.entrySet().forEach(entry -> fields.add(entry.getKey(), firestoreValue(entry.getValue())));
    return fields;
  }

  private static JsonObject firestoreValue(JsonElement element) {
    JsonObject value = new JsonObject();
    if (element.isJsonNull()) {
      value.add("nullValue", null);
    } else if (element.isJsonArray()) {
      JsonArray values = new JsonArray();
      element.getAsJsonArray().forEach(item -> values.add(firestoreValue(item)));
      JsonObject array = new JsonObject();
      array.add("values", values);
      value.add("arrayValue", array);
    } else if (element.isJsonObject()) {
      JsonObject map = new JsonObject();
      map.add("fields", firestoreFields(element.getAsJsonObject()));
      value.add("mapValue", map);
    } else {
      JsonPrimitive primitive = element.getAsJsonPrimitive();
      if (primitive.isBoolean()) value.addProperty("booleanValue", primitive.getAsBoolean());
      else if (primitive.isNumber()) {
        String number = primitive.getAsString();
        if (number.matches("-?\\d+")) value.addProperty("integerValue", number);
        else value.addProperty("doubleValue", number);
      } else value.addProperty("stringValue", primitive.getAsString());
    }
    return value;
  }
}
