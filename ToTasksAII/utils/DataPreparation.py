# import pandas as pd
#
# # data = pd.read_csv(r'D:\SSSSSTTTTTT\N3-K2\Học máy\Materials\task_schedule_2.csv')
#
# data = pd.read_csv(r'task_schedule_2.csv')
#
# used_data = data.copy()
# used_data


# # --- Các thư viện ---
# import firebase_admin
# from firebase_admin import credentials, firestore
# import pandas as pd
# import tkinter as tk
# from tkinter import ttk
#
# # --- Hàm hiển thị GUI ---
# def show_dataframe(df):
#     root = tk.Tk()
#     root.title("Dữ liệu từ Firebase")
#
#     frame = ttk.Frame(root)
#     frame.pack(fill="both", expand=True)
#
#     tree = ttk.Treeview(frame)
#     tree["columns"] = list(df.columns)
#     tree["show"] = "headings"
#
#     for col in df.columns:
#         tree.heading(col, text=col)
#         tree.column(col, anchor="center")
#
#     for i, row in df.iterrows():
#         tree.insert("", "end", values=list(row))
#
#     tree.pack(fill="both", expand=True)
#     root.mainloop()
#
# # --- Khởi tạo Firebase ---
# cred = credentials.Certificate(r"D:\PROJECTSSSS\ToTasks\ToTasksAI\serviceAccountKey.json")
# firebase_admin.initialize_app(cred)
#
# db = firestore.client()
# docs = db.collection("dataset").stream()
#
# data_list = []
# for doc in docs:
#     data_list.append(doc.to_dict())
#
# data = pd.DataFrame(data_list)
# used_data = data.copy()
#
# # --- Gọi hiển thị ---
# show_dataframe(used_data)


import firebase_admin
from firebase_admin import credentials, firestore
import pandas as pd

from nltk.corpus import wordnet
from random import choice

def get_synonyms(word):
    synonyms = set()
    for syn in wordnet.synsets(word):
        for lemma in syn.lemmas():
            if lemma.name().lower() != word.lower():
                synonyms.add(lemma.name().replace('_', ' '))
    return list(synonyms)

def augment_with_synonyms(sentence, num_replacements=1):
    words = sentence.split()
    new_words = words.copy()
    replaced = 0

    for i in range(len(words)):
        synonyms = get_synonyms(words[i])
        if synonyms:
            new_words[i] = choice(synonyms)
            replaced += 1
            if replaced >= num_replacements:
                break

    return ' '.join(new_words)


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


