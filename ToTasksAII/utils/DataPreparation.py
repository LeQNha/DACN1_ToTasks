
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


