import joblib
import pandas as pd
import numpy as np
from sklearn.ensemble import GradientBoostingRegressor
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.preprocessing import StandardScaler
from utils.ToolsPreparation import scaler, tfidf_vectorizer
from utils.ModelEvaluation import mean_r2_calculate

def train_start_time_prediction_model(task_name_vectorized, required_columns, start_time_minutes):
    """
    Huấn luyện mô hình dự đoán thời gian bắt đầu và lưu vào file.
    """
    # Tạo mô hình
    grid_search = GridSearchCV(
        estimator=GradientBoostingRegressor(random_state=42),
        param_grid={
            'n_estimators': [50, 100, 200],
            'learning_rate': [0.01, 0.1, 0.2],
            'max_depth': [3, 5, 7],
            'min_samples_split': [2, 5, 10],
            'min_samples_leaf': [1, 2, 4]
        },
        cv=5, n_jobs=-1, verbose=2
    )

    # Chuyển đổi X_taskname thành DataFrame
    X_taskname_df = pd.DataFrame(task_name_vectorized.toarray(), columns=tfidf_vectorizer.get_feature_names_out())

    # Kết hợp X_taskname_df với các cột khác trong used_data
    X_combined = pd.concat([X_taskname_df, required_columns], axis=1)

    # Đầu ra y
    y = start_time_minutes

    # Chia tập dữ liệu thành training và testing
    X_train, X_test, y_train, y_test = train_test_split(X_combined, y, test_size=0.2, random_state=42)

    # Chuẩn hóa dữ liệu
    scaler = StandardScaler()
    X_train = scaler.fit_transform(X_train)
    X_test = scaler.transform(X_test)

    # Huấn luyện mô hình
    grid_search.fit(X_train, y_train)
    best_model = grid_search.best_estimator_

    # Lưu mô hình và scaler
    joblib.dump(best_model, "start_time_prediction_model.pkl")
    joblib.dump(scaler, "start_time_scaler.pkl")

    # Đánh giá mô hình
    y_pred = best_model.predict(X_test)
    mean_r2_calculate(y_test, y_pred)

    print("Model and scaler saved successfully.")
