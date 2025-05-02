import joblib
import numpy as np
from utils.ToolsPreparation import le_type, le_importance, le_day

# def predict_start_time_2(task_name, task_type, importance, day_of_week, user_id):
def predict_start_time_2(task_name, task_type, importance, day_of_week):

    """
    Dự đoán thời gian bắt đầu từ mô hình đã huấn luyện.
    """
    # Tải mô hình và scaler
    model = joblib.load("start_time_prediction_model_2.pkl")
    # scaler = joblib.load("start_time_scaler.pkl")
    tfidf_vectorizer = joblib.load("tfidf_vectorizer.pkl")
    le_type = joblib.load("le_type.pkl")
    le_importance = joblib.load("le_importance.pkl")
    le_day = joblib.load("le_day.pkl")
    # le_user = joblib.load("le_userid.pkl")

    # Vector hóa TaskName
    task_name_vectorized = tfidf_vectorizer.transform([task_name])

    # Mã hóa các cột
    task_type_encoded = le_type.transform([task_type])[0]
    importance_encoded = le_importance.transform([importance])[0]
    day_of_week_encoded = le_day.transform([day_of_week])[0]
    # user_id_encoded = le_user.transform([user_id])

    # Kết hợp dữ liệu đã xử lý
    X_new = np.concatenate([
        task_name_vectorized.toarray().flatten(),
        # [task_type_encoded, importance_encoded, day_of_week_encoded, user_id_encoded]
        [task_type_encoded, importance_encoded, day_of_week_encoded]
    ]).reshape(1, -1)

    # Chuẩn hóa dữ liệu
    # X_new_scaled = scaler.transform(X_new)

    # Dự đoán StartTime
    # return model.predict(X_new_scaled)[0]
    return model.predict(X_new)[0]
