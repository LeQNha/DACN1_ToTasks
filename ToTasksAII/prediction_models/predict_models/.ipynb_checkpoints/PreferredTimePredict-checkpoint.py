import numpy as np
from scipy.sparse import hstack
import joblib

from utils.ToolsPreparation import le_importance, le_type

# def predict_importance(task_name, task_type, user_id):
def predict_preferred_time(task_name, task_type, importance, day_of_week):

    # Tải mô hình đã huấn luyện
    preferredtime_random_forest_classifier_model = joblib.load("preferredtime_random_forest_classifier_model.pkl")
    tfidf_vectorizer = joblib.load("tfidf_vectorizer.pkl")
    le_type = joblib.load("le_type.pkl")
    le_importance = joblib.load("le_importance.pkl")
    le_day = joblib.load("le_day.pkl")
    # le_user = joblib.load("le_userid.pkl")

    le_preferredtime = joblib.load("le_preferredtime.pkl")

    # Vector hóa TaskName
    task_name_vector = tfidf_vectorizer.transform([task_name])

    # Mã hóa Type
    task_type_encoded = le_type.transform([task_type])
    importance_encoded = le_importance.transform([importance])[0]
    day_of_week_encoded = le_day.transform([day_of_week])[0]
    # user_id_encoded = le_user.transform([user_id])

    # Kết hợp
    # task_vector = hstack([task_name_vector, np.array([task_type_encoded[0], user_id_encoded[0]]).reshape(-1, 1)])
    task_vector = hstack([task_name_vector, np.array([task_type_encoded[0], importance_encoded[0], day_of_week_encoded[0]]).reshape(-1, 1)])

    # Dự đoán và giải mã kết quả
    prediction_encoded = preferredtime_random_forest_classifier_model.predict(task_vector)[0]
    prediction = le_preferredtime.inverse_transform([prediction_encoded])[0]
    return prediction
