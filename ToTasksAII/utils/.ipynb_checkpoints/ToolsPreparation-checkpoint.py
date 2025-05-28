import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.preprocessing import LabelEncoder, StandardScaler, OneHotEncoder

le_type = LabelEncoder()
le_importance = LabelEncoder()
le_day = LabelEncoder()
le_preferredtime = LabelEncoder()
le_userid = LabelEncoder()

# One-hot encode Type
ohe_type = OneHotEncoder(sparse_output=True, handle_unknown='ignore')

tfidf_vectorizer = TfidfVectorizer()

scaler = StandardScaler()

# def prepare_and_save_tools(task_names, types, importance, days):
#     """
#     Huấn luyện các công cụ và lưu lại.
#     """
#     # Huấn luyện LabelEncoder
#     le_type.fit(types)
#     le_importance.fit(importance)
#     le_day.fit(days)
#
#     # Huấn luyện TfidfVectorizer
#     tfidf_vectorizer.fit(task_names)
#
#     # Lưu các công cụ
#     joblib.dump(le_type, "le_type.pkl")
#     joblib.dump(le_importance, "le_importance.pkl")
#     joblib.dump(le_day, "le_day.pkl")
#     joblib.dump(tfidf_vectorizer, "tfidf_vectorizer.pkl")
#     print("Tools saved successfully.")

