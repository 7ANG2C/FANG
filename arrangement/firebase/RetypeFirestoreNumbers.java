import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/** Converts legacy numeric strings into Firestore integer fields for DTO mapping. */
public final class RetypeFirestoreNumbers {
  private static final List<String> COLLECTIONS =
      List.of("attendance", "loans", "funds", "paybacks", "bosses", "employees", "sites");
  private static final Set<String> INTEGER_FIELDS =
      Set.of("id", "millis", "employee", "loan", "site", "boss", "fund", "salary", "income", "start", "end", "expired", "delete", "order", "archive");

  public static void main(String[] args) throws Exception {
    if (args.length < 1 || args.length > 2 || (args.length == 2 && !"--apply".equals(args[1]))) {
      throw new IllegalArgumentException("Usage: <environment> [--apply]");
    }
    String environment = args[0];
    boolean apply = args.length == 2;
    HttpClient http = HttpClient.newHttpClient();
    for (String collection : COLLECTIONS) {
      JsonArray documents = documents(http, environment, collection);
      int candidates = 0;
      for (JsonElement element : documents) {
        JsonObject document = element.getAsJsonObject();
        JsonObject fields = document.getAsJsonObject("fields");
        int converted = retype(fields);
        candidates += converted;
        if (apply && converted > 0) overwrite(http, document.get("name").getAsString(), fields);
      }
      System.out.printf("%-10s Documents=%d NumericStrings=%d%n", collection, documents.size(), candidates);
    }
  }

  private static JsonArray documents(HttpClient http, String environment, String collection) throws Exception {
    JsonArray documents = new JsonArray();
    String pageToken = null;
    do {
      String query = "?pageSize=1000";
      if (pageToken != null) query += "&pageToken=" + URLEncoder.encode(pageToken, StandardCharsets.UTF_8);
      HttpRequest request = HttpRequest.newBuilder(URI.create(root(environment) + "/" + collection + query)).GET().build();
      HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) throw new IllegalStateException(response.body());
      JsonObject result = JsonParser.parseString(response.body()).getAsJsonObject();
      if (result.has("documents")) result.getAsJsonArray("documents").forEach(documents::add);
      pageToken = result.has("nextPageToken") ? result.get("nextPageToken").getAsString() : null;
    } while (pageToken != null);
    return documents;
  }

  private static String root(String environment) {
    return "https://firestore.googleapis.com/v1/projects/fangmono-7ang/databases/(default)/documents/environments/" + environment;
  }

  private static int retype(JsonObject fields) {
    int count = 0;
    for (var entry : fields.entrySet()) {
      JsonObject value = entry.getValue().getAsJsonObject();
      if (INTEGER_FIELDS.contains(entry.getKey()) && value.has("stringValue")) {
        String number = value.get("stringValue").getAsString();
        if (number.matches("-?\\d+")) {
          value.remove("stringValue");
          value.addProperty("integerValue", number);
          count++;
        }
      }
      if (value.has("mapValue")) count += retype(value.getAsJsonObject("mapValue").getAsJsonObject("fields"));
      if (value.has("arrayValue")) {
        JsonObject array = value.getAsJsonObject("arrayValue");
        if (array.has("values")) {
          for (JsonElement item : array.getAsJsonArray("values")) {
            JsonObject itemValue = item.getAsJsonObject();
            if (itemValue.has("mapValue")) count += retype(itemValue.getAsJsonObject("mapValue").getAsJsonObject("fields"));
          }
        }
      }
    }
    return count;
  }

  private static void overwrite(HttpClient http, String documentName, JsonObject fields) throws Exception {
    JsonObject body = new JsonObject();
    body.add("fields", fields);
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("https://firestore.googleapis.com/v1/" + documentName))
            .header("Content-Type", "application/json")
            .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
            .build();
    HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() != 200) throw new IllegalStateException(response.body());
  }
}
