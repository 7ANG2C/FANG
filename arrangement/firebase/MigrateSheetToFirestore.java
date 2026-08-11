import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParser;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * One-time Google Sheets to Firestore importer.
 *
 * Usage:
 *   java -cp <dependencies> MigrateSheetToFirestore <service-account.json> <environment> <sheet-id> [--apply]
 *
 * Default mode is read-only. --apply refuses to write into a non-empty target
 * collection. --replace-fields rewrites matching documents without the legacy
 * payload wrapper.
 */
public final class MigrateSheetToFirestore {
  private static final String PROJECT_ID = "fangmono-7ang";
  private static final Map<String, String> COLLECTIONS = new LinkedHashMap<>();

  static {
    COLLECTIONS.put("Attendance", "attendance");
    COLLECTIONS.put("Loan", "loans");
    COLLECTIONS.put("Fund", "funds");
    COLLECTIONS.put("Payback", "paybacks");
    COLLECTIONS.put("Boss", "bosses");
    COLLECTIONS.put("Employee", "employees");
    COLLECTIONS.put("Site", "sites");
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 3 || args.length > 4 || (args.length == 4 && !Set.of("--apply", "--replace-fields").contains(args[3]))) {
      throw new IllegalArgumentException("Usage: <service-account.json> <environment> <sheet-id> [--apply|--replace-fields]");
    }

    String environment = args[1];
    boolean apply = args.length == 4;
    boolean replaceFields = apply && "--replace-fields".equals(args[3]);
    Sheets sheets = sheets(args[0]);
    HttpClient http = HttpClient.newHttpClient();
    Map<String, List<List<Object>>> source = new LinkedHashMap<>();

    for (String sheetName : COLLECTIONS.keySet()) {
      List<List<Object>> rows = sheets.spreadsheets().values().get(args[2], sheetName).execute().getValues();
      source.put(sheetName, rows == null ? List.of() : rows);
    }

    for (Map.Entry<String, String> mapping : COLLECTIONS.entrySet()) {
      List<List<Object>> rows = source.get(mapping.getKey());
      int sourceCount = Math.max(rows.size() - 1, 0);
      int targetCount = targetCount(http, environment, mapping.getValue());
      System.out.printf("%-10s Sheet=%d Firestore=%d%n", mapping.getKey(), sourceCount, targetCount);
      if (apply && !replaceFields && targetCount != 0) {
        throw new IllegalStateException(mapping.getValue() + " is not empty; refusing to overwrite it.");
      }
      if (replaceFields && sourceCount != targetCount) {
        throw new IllegalStateException(mapping.getValue() + " count differs; refusing to replace it.");
      }
    }

    if (!apply) {
      System.out.println("Preview only. Re-run with --apply after checking these counts.");
      return;
    }

    for (Map.Entry<String, String> mapping : COLLECTIONS.entrySet()) {
      List<List<Object>> rows = source.get(mapping.getKey());
      if (rows.isEmpty()) continue;
      List<Object> headers = rows.get(0);
      for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
        JsonObject parsed = payload(headers, rows.get(rowIndex));
        String id = parsed.get(headers.get(0).toString()).getAsString();
        put(http, environment, mapping.getValue(), id, parsed);
      }
      System.out.println("Imported " + mapping.getKey());
    }
  }

  private static Sheets sheets(String serviceAccountPath) throws Exception {
    GoogleCredentials credentials =
        ServiceAccountCredentials.fromStream(new FileInputStream(serviceAccountPath))
            .createScoped(SheetsScopes.SPREADSHEETS_READONLY);
    return new Sheets.Builder(
            new NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            new HttpCredentialsAdapter(credentials))
        .setApplicationName("Arrangement Firestore migration")
        .build();
  }

  private static JsonObject payload(List<Object> headers, List<Object> row) {
    JsonObject payload = new JsonObject();
    for (int i = 0; i < headers.size(); i++) {
      if (i >= row.size()) continue;
      String value = row.get(i).toString();
      if (value.isBlank()) continue;
      if (value.startsWith("[")) {
        payload.add(headers.get(i).toString(), JsonParser.parseString(value));
      } else {
        payload.addProperty(headers.get(i).toString(), value);
      }
    }
    return payload;
  }

  private static int targetCount(HttpClient http, String environment, String collection) throws Exception {
    int count = 0;
    String pageToken = null;
    do {
      String query = "?pageSize=1000";
      if (pageToken != null) query += "&pageToken=" + URLEncoder.encode(pageToken, StandardCharsets.UTF_8);
      HttpRequest request =
          HttpRequest.newBuilder(URI.create(collectionUri(environment, collection) + query)).GET().build();
      HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) throw new IllegalStateException(response.body());
      JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
      if (body.has("documents")) count += body.getAsJsonArray("documents").size();
      pageToken = body.has("nextPageToken") ? body.get("nextPageToken").getAsString() : null;
    } while (pageToken != null);
    return count;
  }

  private static void put(HttpClient http, String environment, String collection, String id, JsonObject document) throws Exception {
    JsonObject body = new JsonObject();
    body.add("fields", firestoreFields(document));
    HttpRequest request =
        HttpRequest.newBuilder(collectionUri(environment, collection + "/" + id))
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
      if (primitive.isBoolean()) {
        value.addProperty("booleanValue", primitive.getAsBoolean());
      } else if (primitive.isNumber()) {
        String number = primitive.getAsString();
        if (number.matches("-?\\d+")) value.addProperty("integerValue", number);
        else value.addProperty("doubleValue", number);
      } else {
        value.addProperty("stringValue", primitive.getAsString());
      }
    }
    return value;
  }

  private static URI collectionUri(String environment, String path) {
    return URI.create(
        "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID
            + "/databases/(default)/documents/environments/" + environment + "/" + path);
  }
}
