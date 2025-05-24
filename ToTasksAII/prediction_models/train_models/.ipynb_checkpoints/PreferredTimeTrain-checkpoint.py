import joblib
import numpy as np
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from utils.ModelEvaluation import mean_r2_calculate
from scipy.sparse import hstack

def train_preferred_time_prediction_model(task_name_vectorized, used_data_type, used_data_importance, used_data_day_of_week, used_data_preferredtime):

     print("-----importance_prediction_model START TRAINNING.-----")
    
    # Tạo mô hình
    preferredtime_random_forest_classifier_model = RandomForestClassifier(random_state=42)

    # Kết hợp TaskName vectorized và Type_Encoded
    # X = hstack([task_name_vectorized, np.array(used_data_type).reshape(-1, 1), np.array(used_data_userid).reshape(-1, 1)])
    X = hstack([task_name_vectorized, np.array(used_data_type).reshape(-1, 1), np.array(used_data_importance).reshape(-1, 1), np.array(used_data_day_of_week).reshape(-1, 1),])

    y = used_data_preferredtime

    # Chia tập dữ liệu
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Huấn luyện mô hình
    preferredtime_random_forest_classifier_model.fit(X_train, y_train)

    # Dự đoán
    y_pred = preferredtime_random_forest_classifier_model.predict(X_test)

    # Đánh giá
    accuracy_score_calculate(y_test, y_pred)

    # Lưu mô hình đã huấn luyện
    joblib.dump(preferredtime_random_forest_classifier_model, "preferredtime_random_forest_classifier_model.pkl")
    print("preferred_time_prediction_model.pkl have been saved successfully.")

    return
