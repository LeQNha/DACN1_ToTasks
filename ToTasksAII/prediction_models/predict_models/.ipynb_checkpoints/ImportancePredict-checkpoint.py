import numpy as np
from scipy.sparse import hstack
import joblib
import pandas as pd

# from utils.ToolsPreparation import le_importance, le_type
from utils.ToolsPreparation import le_importance, ohe_type

# def predict_importance(task_name, task_type, user_id):
def predict_importance(task_name, task_type):

    # Tải mô hình đã huấn luyện
    importance_random_forest_classifier_model = joblib.load("importance_prediction_model.pkl")
    tfidf_vectorizer = joblib.load("tfidf_vectorizer.pkl")
    # le_type = joblib.load("le_type.pkl")
    ohe_type = joblib.load("ohe_type.pkl")
    le_importance = joblib.load("le_importance.pkl")
    # le_user = joblib.load("le_userid.pkl")

    # Vector hóa TaskName
    task_name_vector = tfidf_vectorizer.transform([task_name])

    # # Mã hóa Type
    # task_type_encoded = le_type.transform([task_type])
    # One-hot encode kiểu task_type
    # type_encoded = ohe_type.transform([[task_type]])  # [[task_type]] because it expects 2D input
    type_encoded = ohe_type.transform(pd.DataFrame([[task_type]], columns=['Type']))
    # user_id_encoded = le_user.transform([user_id])

    # Kết hợp
    # task_vector = hstack([task_name_vector, np.array([task_type_encoded[0], user_id_encoded[0]]).reshape(-1, 1)])
    # task_vector = hstack([task_name_vector, np.array([task_type_encoded[0]]).reshape(-1, 1)])
    input_vector = hstack([task_name_vector, type_encoded])

    # Dự đoán và giải mã kết quả
    prediction_encoded = importance_random_forest_classifier_model.predict(input_vector)[0]
    prediction = le_importance.inverse_transform([prediction_encoded])[0]
    return prediction
