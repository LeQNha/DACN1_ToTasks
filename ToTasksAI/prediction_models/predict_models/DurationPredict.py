import joblib
import numpy as np
from utils.ToolsPreparation import le_type, le_day, le_importance

def predict_duration(task_name, task_type, day_of_week, task_importance):
    """
    Dự đoán thời lượng từ mô hình đã huấn luyện.
    """
    # Tải mô hình
    model = joblib.load("duration_prediction_model.pkl")
    tfidf_vectorizer = joblib.load("tfidf_vectorizer.pkl")
    le_type = joblib.load("le_type.pkl")
    le_importance = joblib.load("le_importance.pkl")
    le_day = joblib.load("le_day.pkl")
    # le_user = joblib.load("le_userid.pkl")

    # Vector hóa TaskName
    taskname_vector = tfidf_vectorizer.transform([task_name])

    # Mã hóa các cột
    type_encoded = le_type.transform([task_type])
    day_encoded = le_day.transform([day_of_week])
    # user_id_encoded = le_user.transform([user_id])
    importance_encoded = le_importance.transform([task_importance])

    # Tạo mảng đầu vào
    new_input = np.hstack([
        taskname_vector.toarray(),
        # np.array([type_encoded[0], day_encoded[0], importance_encoded[0], user_id_encoded[0]]).reshape(1, -1)
        np.array([type_encoded[0], day_encoded[0], importance_encoded[0]]).reshape(1, -1)
    ])

    # Dự đoán
    return model.predict(new_input)[0]
