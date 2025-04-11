import joblib
import numpy as np
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from utils.ModelEvaluation import mean_r2_calculate

def train_duration_prediction_model(task_name_vectorized, used_data_type, used_data_day_of_week, used_data_importance, used_data_userid, used_data_duration):
    """
    Huấn luyện mô hình dự đoán thời lượng và lưu vào file.
    """
    duration_random_forest_regressor_model = RandomForestRegressor(random_state=42)

    # Tạo tập dữ liệu đầu vào (X) và đầu ra (y)
    X = np.hstack([
        task_name_vectorized.toarray(),
        np.column_stack((used_data_type, used_data_day_of_week, used_data_importance, used_data_userid))
    ])
    y = used_data_duration

    # Chia dữ liệu thành tập huấn luyện và tập kiểm tra
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Huấn luyện mô hình
    duration_random_forest_regressor_model.fit(X_train, y_train)

    # Lưu mô hình
    joblib.dump(duration_random_forest_regressor_model, "duration_prediction_model.pkl")

    # Đánh giá mô hình
    y_pred = duration_random_forest_regressor_model.predict(X_test)
    mean_r2_calculate(y_test, y_pred)

    print("duration_prediction_model.pkl have been saved successfully.")
