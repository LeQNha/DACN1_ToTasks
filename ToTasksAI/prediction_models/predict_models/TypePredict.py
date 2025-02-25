import joblib
from utils.ToolsPreparation import le_type

def predict_task_type(task_name):
    """
    Dự đoán loại nhiệm vụ từ mô hình đã được huấn luyện.
    """
    # Tải mô hình và các đối tượng cần thiết
    type_random_forest_classifier_model = joblib.load("type_prediction_model.pkl")
    # Load tfidf_vectorizer từ file
    tfidf_vectorizer = joblib.load("tfidf_vectorizer.pkl")
    le_type = joblib.load("le_type.pkl")

    # Vector hóa TaskName
    task_vector = tfidf_vectorizer.transform([task_name])

    # Dự đoán loại nhiệm vụ
    prediction = type_random_forest_classifier_model.predict(task_vector)

    # Chuyển kết quả dự đoán từ mã sang tên loại
    predicted_type_name = le_type.inverse_transform([prediction[0]])[0]

    return predicted_type_name
