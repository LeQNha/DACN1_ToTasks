import joblib
from scipy.sparse import hstack

# def predict_task_type(task_name, user_id):
def predict_task_type(task_name):

    """
    Dự đoán loại nhiệm vụ từ mô hình đã được huấn luyện.
    """
    # Tải mô hình và các đối tượng cần thiết
    type_random_forest_classifier_model = joblib.load("type_prediction_model.pkl")
    # Load tfidf_vectorizer từ file
    tfidf_vectorizer = joblib.load("tfidf_vectorizer.pkl")
    le_type = joblib.load("le_type.pkl")
    # le_user = joblib.load("le_userid.pkl")

    # Vector hóa TaskName
    task_vector = tfidf_vectorizer.transform([task_name])
    # user_id_encoded = le_user.transform([user_id])

    # Kết hợp vector đặc trưng
    # input_vector = hstack([task_vector, user_id_encoded])
    input_vector = hstack([task_vector])
    # Dự đoán loại nhiệm vụ
    prediction = type_random_forest_classifier_model.predict(input_vector)

    # Chuyển kết quả dự đoán từ mã sang tên loại
    predicted_type_name = le_type.inverse_transform([prediction[0]])[0]

    return predicted_type_name
