import joblib
import numpy as np
from scipy.sparse import hstack
from utils.ToolsPreparation import le_type, le_day, le_importance

# def predict_duration(task_name, task_type, day_of_week, task_importance, user_id):
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
    scaler_duration = joblib.load("duration_scaler.pkl")

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
    #  # Ghép vector đầu vào
    # categorical_features = np.array([[type_encoded, day_encoded, importance_encoded]])
    # X_input = hstack([taskname_vector, categorical_features])

     # Dự đoán giá trị đã được chuẩn hóa
    predicted_duration_scaled = model.predict(new_input)[0]

     # Đảo ngược chuẩn hóa (inverse transform)
    predicted_duration_real = scaler_duration.inverse_transform([[predicted_duration_scaled]])[0][0]
    
    # Dự đoán
    return predicted_duration_real
    # return model.predict(new_input)[0]
