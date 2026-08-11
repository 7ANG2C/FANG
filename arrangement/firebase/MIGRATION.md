# Arrangement Firebase 切換

這個 App 在沒有登入機制下使用 Firebase Firestore。資料只適合私人側載 APK；任何取得 APK 的人都可以讀寫 Firebase 專案的 `sit`、`prod` 資料。

## 先建立 Firebase 服務

在 `fangmono-7ang` Firebase Console：

1. 建立 Cloud Firestore，選擇 Production mode 和離使用者最近的區域。
2. 在 Firestore 的 Rules 分頁貼上 `firestore.rules`。

## 資料切換順序

1. 先 build 並安裝 `sitDebug`，確認它連到 `environments/sit`。
2. 使用 `MigrateSheetToFirestore.java` 將舊 SIT Google Sheet 的七張表匯入各自的 Firestore collection：`attendance`、`loans`、`funds`、`paybacks`、`bosses`、`employees`、`sites`。文件 ID 使用每列第一欄：Attendance 是 `millis`，其餘是 `id`；每一個資料屬性都應是原生 Firestore field。
3. 用 SIT APK 核對員工、工地、出勤與三種款項報表。
4. 以相同方式匯入 PROD Sheet 到 `environments/prod`，再 build `prodRelease` 給正式使用。
5. 確認兩個版本都已切到 Firestore 後，從本機移除 `src/main/assets/service-account.json`，並到 Google Cloud Console 撤銷該 service-account key。

若曾使用舊版匯入器產生 `payload` 字串，使用 `NormalizeFirestoreFields.java --apply` 正規化為原生 fields、arrays 與 nested maps。現有 ViewModel、統計與 PDF 邏輯不需要重寫。
