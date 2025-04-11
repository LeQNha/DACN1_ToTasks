# import pandas as pd
#
# # data = pd.read_csv(r'D:\SSSSSTTTTTT\N3-K2\Học máy\Materials\task_schedule_2.csv')
#
# data = pd.read_csv(r'task_schedule_2.csv')
#
# used_data = data.copy()
# used_data


import firebase_admin
from firebase_admin import credentials, firestore
import pandas as pd

# Khởi tạo Firebase app
cred = credentials.Certificate("serviceAccountKey.json")
firebase_admin.initialize_app(cred)

# Truy cập Firestore
db = firestore.client()

# Lấy tất cả các document trong collection "tasks"
docs = db.collection("dataset").stream()

# Chuyển document về danh sách dict
data_list = []
for doc in docs:
    task_data = doc.to_dict()
    data_list.append(task_data)

# Tạo DataFrame từ danh sách
data = pd.DataFrame(data_list)

# Copy dữ liệu để sử dụng
used_data = data.copy()


