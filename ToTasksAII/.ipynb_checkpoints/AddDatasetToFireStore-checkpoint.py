import firebase_admin
from firebase_admin import credentials, firestore
import pandas as pd

# Khởi tạo Firebase app
cred = credentials.Certificate("serviceAccountKey.json")
firebase_admin.initialize_app(cred)

# Kết nối tới Firestore
db = firestore.client()

# Đọc file CSV (đường dẫn file bạn vừa upload)
# csv_file_path = "task_dataset_en.csv"
csv_file_path = "downloaded_dataset_og.csv"
df = pd.read_csv(csv_file_path)

# Thêm cột UserID = "user1"
# df["UserID"] = "user1"

# Lưu từng dòng vào Firestore
for index, row in df.iterrows():
    task_data = {
        "UserID": row["UserID"],
        "TaskID": row.get("TaskID", f"task_{index}"),
        "TaskName": row["TaskName"],
        "Type": row["Type"],
        "Duration": row["Duration"],
        "Importance": row["Importance"],
        "DayOfWeek": row["DayOfWeek"],
        "StartTime": row["StartTime"],
        "EndTime": row["EndTime"],
        "UserID": row ["UserID"]
    }

    # Tên collection: "tasks", mỗi document ID là tự sinh
    db.collection("dataset").add(task_data)

print("✅ Dữ liệu đã được lưu vào Firebase Firestore thành công!")
