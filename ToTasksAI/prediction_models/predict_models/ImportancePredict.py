import numpy as np
from scipy.sparse import hstack
import joblib

from utils.ToolsPreparation import le_importance, le_type

def predict_importance(task_name, task_type):
    # Tải mô hình đã huấn luyện
    importance_random_forest_classifier_model = joblib.load("importance_prediction_model.pkl")
    tfidf_vectorizer = joblib.load("tfidf_vectorizer.pkl")
    le_type = joblib.load("le_type.pkl")
    le_importance = joblib.load("le_importance.pkl")

    # Vector hóa TaskName
    task_name_vector = tfidf_vectorizer.transform([task_name])

    # Mã hóa Type
    task_type_encoded = le_type.transform([task_type])

    # Kết hợp
    task_vector = hstack([task_name_vector, np.array(task_type_encoded).reshape(-1, 1)])

    # Dự đoán và giải mã kết quả
    prediction_encoded = importance_random_forest_classifier_model.predict(task_vector)[0]
    prediction = le_importance.inverse_transform([prediction_encoded])[0]
    return prediction
