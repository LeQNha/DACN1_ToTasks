import numpy as np
from scipy.sparse import hstack
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
import joblib

from utils.ModelEvaluation import accuracy_score_calculate
# from utils.ToolsPreparation import tfidf_vectorizer, le_importance, le_type
from utils.ToolsPreparation import tfidf_vectorizer, le_importance, ohe_type

# def train_importance_prediction_model(task_name_vectorized, used_data_type, used_data_userid, used_data_importance):
def train_importance_prediction_model(task_name_vectorized, used_data_type, used_data_importance):

    print("-----importance_prediction_model START TRAINNING.-----")
    
    # Tạo mô hình
    importance_random_forest_classifier_model = RandomForestClassifier(random_state=42)

    # Kết hợp TaskName vectorized và Type_Encoded
    # X = hstack([task_name_vectorized, np.array(used_data_type).reshape(-1, 1), np.array(used_data_userid).reshape(-1, 1)])
    # X = hstack([task_name_vectorized, np.array(used_data_type).reshape(-1, 1)])
    X = hstack([task_name_vectorized, used_data_type])

    y = used_data_importance

    # Chia tập dữ liệu
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Huấn luyện mô hình
    importance_random_forest_classifier_model.fit(X_train, y_train)

    # Dự đoán
    y_pred = importance_random_forest_classifier_model.predict(X_test)

    # Đánh giá
    accuracy_score_calculate(y_test, y_pred)

    # Lưu mô hình đã huấn luyện
    joblib.dump(importance_random_forest_classifier_model, "importance_prediction_model.pkl")
    print("importance_prediction_model.pkl have been saved successfully.")

    return
