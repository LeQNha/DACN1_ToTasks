import joblib
import numpy as np
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from utils.ModelEvaluation import mean_r2_calculate
from scipy.sparse import hstack

# def train_duration_prediction_model(task_name_vectorized, used_data_type, used_data_day_of_week, used_data_importance, used_data_userid, used_data_duration):
# def train_duration_prediction_model(task_name_vectorized, used_data_type, used_data_day_of_week, used_data_importance, used_data_duration):
def train_duration_prediction_model(task_name_vectorized, used_data_type, used_data_DayOfWeek_sin, used_data_DayOfWeek_cos, used_data_importance, used_data_duration):

    print("-----duration_prediction_model START TRAINNING.-----")

    """
    Huấn luyện mô hình dự đoán thời lượng và lưu vào file.
    """
    duration_random_forest_regressor_model = RandomForestRegressor(random_state=42)

    # Tạo tập dữ liệu đầu vào (X) và đầu ra (y)
    # X = np.hstack([
    #     task_name_vectorized.toarray(),
    #     np.column_stack((used_data_type, used_data_day_of_week, used_data_importance, used_data_userid))
    # ])
    X = np.hstack([
        task_name_vectorized.toarray(),
        np.column_stack((used_data_type, used_data_DayOfWeek_sin, used_data_DayOfWeek_cos , used_data_importance))
    ])
    y = used_data_duration

    # # Tạo tập dữ liệu đầu vào (X) và đầu ra (y)
    # basic_features = np.column_stack((used_data_type, used_data_day_of_week, used_data_importance))

    # if used_data_features is not None:
    #     # Nếu có đặc trưng mở rộng, ghép chúng với đặc trưng cơ bản
    #     extra_features = used_data_features.values  # convert DataFrame to numpy
    #     combined_features = np.hstack([basic_features, extra_features])
    # else:
    #     combined_features = basic_features

    # # Ghép với TF-IDF vector
    # X = hstack([task_name_vectorized, combined_features])
    # y = used_data_duration

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
