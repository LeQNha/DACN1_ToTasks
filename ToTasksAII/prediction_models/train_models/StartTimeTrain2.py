import joblib
import pandas as pd
import numpy as np
from sklearn.ensemble import GradientBoostingRegressor, RandomForestRegressor
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.preprocessing import StandardScaler
from utils.ToolsPreparation import scaler, tfidf_vectorizer
from utils.ModelEvaluation import mean_r2_calculate

def train_start_time_prediction_model_2(task_name_vectorized, required_columns, start_time_minutes):

    print("-----start_time_prediction_model START TRAINNING.-----")
    """
    Huấn luyện mô hình dự đoán thời gian bắt đầu và lưu vào file.
    """
    start_time_random_forest_regressor_model_2 = RandomForestRegressor(random_state=42)

    # Chuyển đổi X_taskname thành DataFrame
    X_taskname_df = pd.DataFrame(task_name_vectorized.toarray(), columns=tfidf_vectorizer.get_feature_names_out())

    # Kết hợp X_taskname_df với các cột khác trong used_data
    X_combined = pd.concat([X_taskname_df, required_columns], axis=1)

    # Đầu ra y
    y = start_time_minutes

    # Chia tập dữ liệu thành training và testing
    X_train, X_test, y_train, y_test = train_test_split(X_combined, y, test_size=0.2, random_state=42)

    start_time_random_forest_regressor_model_2.fit(X_train, y_train)

    # Lưu mô hình và scaler
    joblib.dump(start_time_random_forest_regressor_model_2, "start_time_prediction_model_2.pkl")
    # joblib.dump(scaler, "start_time_scaler.pkl")

    # Đánh giá mô hình
    y_pred = start_time_random_forest_regressor_model_2.predict(X_test)
    mean_r2_calculate(y_test, y_pred)

    print("start_time_prediction_model.pkl have been saved successfully.")
