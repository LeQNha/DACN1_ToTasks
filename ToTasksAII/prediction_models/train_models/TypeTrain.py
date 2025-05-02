import numpy as np
from scipy.sparse import hstack
import joblib
from utils.ModelEvaluation import accuracy_score_calculate
from utils.ToolsPreparation import tfidf_vectorizer, le_type
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split

# Tải các công cụ
# le_type = joblib.load("le_type.pkl")
# le_importance = joblib.load("le_importance.pkl")
# le_day = joblib.load("le_day.pkl")
# tfidf_vectorizer = joblib.load("tfidf_vectorizer.pkl")

# def train_type_prediction_model(task_name_vectorized, used_data_type, used_data_userid):
def train_type_prediction_model(task_name_vectorized, used_data_type):

    print("-----type_prediction_model START TRAINNING.-----")

    """
    Huấn luyện mô hình dự đoán loại nhiệm vụ và lưu vào file.
    """
    # Khởi tạo mô hình
    type_random_forest_classifier_model = RandomForestClassifier(random_state=42)

    # Chia dữ liệu
    X = task_name_vectorized
    # X = hstack([task_name_vectorized, np.array(used_data_userid).reshape(-1, 1)])

    y = used_data_type
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Huấn luyện mô hình
    type_random_forest_classifier_model.fit(X_train, y_train)

    # Lưu mô hình và các đối tượng cần thiết
    joblib.dump(type_random_forest_classifier_model, "type_prediction_model.pkl")
    # joblib.dump(le_type, "le_type.pkl")

    # Đánh giá mô hình
    y_pred = type_random_forest_classifier_model.predict(X_test)
    accuracy_score_calculate(y_test, y_pred)

    print("type_prediction_model.pkl have been saved successfully.")
