import pandas as pd
import joblib
import numpy as np
from sklearn.preprocessing import StandardScaler
from utils.SpellCheck import preprocess_text_with_spell_check
# from utils.ToolsPreparation import tfidf_vectorizer, le_type, le_importance, le_day, le_userid, le_preferredtime
from utils.ToolsPreparation import tfidf_vectorizer, le_type, le_importance, le_day, le_userid, le_preferredtime, ohe_type

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


def clean_time_format(t):
    """Chuẩn hóa định dạng thời gian (HH:mm), trả về None nếu không hợp lệ"""
    if pd.isna(t):
        return None
    t = str(t).strip()
    if t == "":
        return None
    if ":" not in t:
        return None
    try:
        parts = t.split(":")
        if len(parts) != 2:
            return None
        hours, minutes = int(parts[0]), int(parts[1])
        if 0 <= hours < 24 and 0 <= minutes < 60:
            return f"{hours:02}:{minutes:02}"
        return None
    except:
        return None


def time_to_minutes(start_time):
    try:
        hours, minutes = map(int, start_time.strip().split(":"))
        return hours * 60 + minutes
    except Exception:
        return None  # Nếu định dạng lỗi hoặc rỗng


def preprocess_data(used_data):
    
    print("Bắt đầu tiền xử lý dữ liệu...")

    # 1. Hiển thị dữ liệu thiếu
    print("Kiểm tra dữ liệu thiếu ban đầu:")
    print(used_data.isnull().sum())

    # 2. Xử lý giá trị thiếu hoặc không hợp lệ
    important_columns = ['TaskName', 'Type', 'Importance', 'Duration', 'DayOfWeek', 'StartTime', 'EndTime', 'UserID']
    used_data = used_data.dropna(subset=important_columns)

    used_data = used_data.dropna(subset=['TaskName'])
    used_data = used_data[used_data['TaskName'].str.strip() != ""]

    used_data['StartTime'] = used_data['StartTime'].apply(clean_time_format)
    used_data['EndTime'] = used_data['EndTime'].apply(clean_time_format)
    used_data = used_data.dropna(subset=['StartTime', 'EndTime'])

    # duplicates = used_data.duplicated(subset=["TaskName", "Type", "Importance", "StartTime", "UserID", "DayOfWeek"])
    # print(f"Số dòng trùng lặp theo tiêu chí: {duplicates.sum()}")
    # # Loại bỏ dòng trùng lặp dựa trên các thuộc tính đặc trưng
    # used_data = used_data.drop_duplicates(subset=['TaskName', "Type", "Importance", 'StartTime', 'UserID', 'DayOfWeek'])

    # Reset index sau khi lọc
    used_data.reset_index(drop=True, inplace=True)

    # 3. Tiền xử lý văn bản (TaskName)
    used_data["TaskName"] = used_data["TaskName"].apply(preprocess_text_with_spell_check)

    # 4. Vector hóa TaskName
    task_name_vectorized = tfidf_vectorizer.fit_transform(used_data['TaskName'])
    joblib.dump(tfidf_vectorizer, "tfidf_vectorizer.pkl")

    # 5. Label encode các cột phân loại
    # used_data['Type'] = le_type.fit_transform(used_data['Type'])
    # joblib.dump(le_type, "le_type.pkl")
    used_data['Type_LabelEncoded'] = le_type.fit_transform(used_data['Type'])
    joblib.dump(le_type, "le_type.pkl")

    # # One-hot encode cột 'Type'
    # type_dummies = pd.get_dummies(used_data['Type'], prefix='Type')
    # used_data = pd.concat([used_data.drop(columns=['Type']), type_dummies], axis=1)

    type_encoded = ohe_type.fit_transform(used_data[['Type']])
    joblib.dump(ohe_type, "ohe_type.pkl")

    # Thêm vào DataFrame:
    type_encoded_df = pd.DataFrame(type_encoded.toarray(), columns=[f"Type_{cat}" for cat in ohe_type.categories_[0]])
    # Reset chỉ mục để tránh lỗi khi concat
    type_encoded_df.index = used_data.index
    # used_data = pd.concat([used_data.drop(columns=['Type']), type_encoded_df], axis=1)
    used_data = pd.concat([used_data, type_encoded_df], axis=1)
    

    used_data['Importance'] = le_importance.fit_transform(used_data['Importance'])
    joblib.dump(le_importance, "le_importance.pkl")

    used_data['DayOfWeek'] = le_day.fit_transform(used_data['DayOfWeek'])
    joblib.dump(le_day, "le_day.pkl")

    # used_data['UserID'] = le_userid.fit_transform(used_data['UserID'])
    # joblib.dump(le_userid, "le_userid.pkl")

    # 6. Chuyển đổi StartTime thành số phút từ đầu ngày
    used_data['StartTimeMinutes'] = used_data['StartTime'].apply(time_to_minutes)

    # # 7. Feature Engineering: thời gian tuần hoàn
    # used_data['StartTime_sin'] = np.sin(2 * np.pi * used_data['StartTimeMinutes'] / 1440)
    # used_data['StartTime_cos'] = np.cos(2 * np.pi * used_data['StartTimeMinutes'] / 1440)

    # 8. DayOfWeek tuần hoàn (nếu cần)
    used_data['DayOfWeek_sin'] = np.sin(2 * np.pi * used_data['DayOfWeek'] / 7)
    used_data['DayOfWeek_cos'] = np.cos(2 * np.pi * used_data['DayOfWeek'] / 7)

    # 10. Chuẩn hóa các cột số
    scaler_duration = StandardScaler()
    scaler_starttime = StandardScaler()
    used_data['Duration'] = scaler_duration.fit_transform(used_data[['Duration']])
    used_data['StartTimeMinutes'] = scaler_starttime.fit_transform(used_data[['StartTimeMinutes']])

    joblib.dump(scaler_duration, "duration_scaler.pkl")
    joblib.dump(scaler_starttime, "starttime_scaler.pkl")

    
    print("✅ Tiền xử lý hoàn tất với các đặc trưng nâng cao.")

    print("✅ Tiền xử lý hoàn tất.")
    return used_data, task_name_vectorized