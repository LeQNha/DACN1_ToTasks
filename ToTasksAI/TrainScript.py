import joblib

from prediction_models.train_models.DurationTrain import train_duration_prediction_model
from prediction_models.train_models.ImportanceTrain import train_importance_prediction_model
from prediction_models.train_models.StartTimeTrain import train_start_time_prediction_model
from prediction_models.train_models.StartTimeTrain2 import train_start_time_prediction_model_2
from utils.DataPreparation import used_data
from utils.SpellCheck import preprocess_text_with_spell_check
from utils.ToolsPreparation import tfidf_vectorizer, le_type, le_importance, le_day, le_userid

from prediction_models.train_models.TypeTrain import train_type_prediction_model

# from utils.DataPreparation import prepare_data  # Giả sử bạn có file chuẩn bị dữ liệu

# Chuẩn bị dữ liệu
# task_name_vectorized, used_data_type = prepare_data()
print(used_data.head())

# Áp dụng quá trình tiền xử lý cho dữ liệu
used_data["TaskName"] = used_data["TaskName"].apply(preprocess_text_with_spell_check)
# Vectorize TaskName
task_name_vectorized = tfidf_vectorizer.fit_transform(used_data['TaskName'])
joblib.dump(tfidf_vectorizer, "tfidf_vectorizer.pkl")
# Encode Type, Importance, DayOfWeek
used_data['Type'] = le_type.fit_transform(used_data['Type'])
joblib.dump(le_type, "le_type.pkl")
used_data['Importance'] = le_importance.fit_transform(used_data['Importance'])
joblib.dump(le_importance, "le_importance.pkl")
used_data['DayOfWeek'] = le_day.fit_transform(used_data['DayOfWeek'])
joblib.dump(le_day, "le_day.pkl")
used_data['UserID'] = le_userid.fit_transform(used_data['UserID'])
joblib.dump(le_userid, "le_userid.pkl")

# Huấn luyện và lưu mô hình
# TRAIN TYPE PREDICTION MODEL
# train_type_prediction_model(task_name_vectorized, used_data_type=used_data['Type'], used_data_userid=used_data['UserID'])
train_type_prediction_model(task_name_vectorized, used_data_type=used_data['Type'])

# TRAIN IMPORTANCE PREDICTION MODEL
# train_importance_prediction_model(task_name_vectorized, used_data_type=used_data['Type'],
#                                   used_data_importance=used_data['Importance'], used_data_userid=used_data['UserID'])
train_importance_prediction_model(task_name_vectorized, used_data_type=used_data['Type'],
                                  used_data_importance=used_data['Importance'])

# TRAIN DURATION PREDICTION MODEL
train_duration_prediction_model(task_name_vectorized, used_data_type=used_data['Type'],
                                used_data_day_of_week=used_data['DayOfWeek'],
                                used_data_importance=used_data['Importance'], used_data_duration=used_data['Duration'])


# # TRAIN START TIME PREDICTION MODEL
# # Chuyển StartTime thành số phút từ đầu ngày
# def time_to_minutes(start_time):
#     hours, minutes = map(int, start_time.split(":"))
#     return hours * 60 + minutes
#
# used_data['StartTimeMinutes'] = used_data['StartTime'].apply(time_to_minutes)
#
# required_columns = used_data[['Type', 'Importance', 'DayOfWeek']]
# train_start_time_prediction_model(task_name_vectorized, required_columns=required_columns,
#                                   start_time_minutes=used_data['StartTimeMinutes'])


#-------------------------
def time_to_minutes(start_time):
    hours, minutes = map(int, start_time.split(":"))
    return hours * 60 + minutes

used_data['StartTimeMinutes'] = used_data['StartTime'].apply(time_to_minutes)

required_columns = used_data[['Type', 'Importance', 'DayOfWeek']]
train_start_time_prediction_model_2(task_name_vectorized, required_columns=required_columns,
                                  start_time_minutes=used_data['StartTimeMinutes'])
