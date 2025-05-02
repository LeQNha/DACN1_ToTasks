import firebase_admin
from firebase_admin import credentials, firestore
import pandas as pd
import tkinter as tk
from tkinter import filedialog
import os

# Tạo cửa sổ ẩn của tkinter
root = tk.Tk()
root.withdraw()  # Ẩn cửa sổ chính

# Hộp thoại chọn folder
folder_path = filedialog.askdirectory(title="Chọn thư mục để lưu file CSV")

if folder_path:
    # Khởi tạo Firebase nếu chưa có
    if not firebase_admin._apps:
        cred = credentials.Certificate("serviceAccountKey.json")
        firebase_admin.initialize_app(cred)

    # Kết nối tới Firestore
    db = firestore.client()

    # Lấy tất cả documents trong collection "dataset"
    docs = db.collection("dataset").stream()

    # Tạo danh sách dict từ dữ liệu
    data_list = [doc.to_dict() for doc in docs]
    df = pd.DataFrame(data_list)

    # Tạo đường dẫn file đầy đủ
    output_file = os.path.join(folder_path, "downloaded_dataset.csv")

    desired_order = [
        "TaskID", "TaskName", "Type", "Importance", "Duration",
        "DayOfWeek", "StartTime", "EndTime", "UserID"
    ]
    df = df[desired_order]  # Đặt lại thứ tự cột
    df.to_csv(output_file, index=False)

    print(f"✅ File đã được lưu tại: {output_file}")
else:
    print("❌ Bạn chưa chọn thư mục. File sẽ không được lưu.")
