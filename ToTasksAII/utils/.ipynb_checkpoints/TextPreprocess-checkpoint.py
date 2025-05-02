import re

def preprocess_text(text):
    # Tiền xử lý cơ bản: chuyển về chữ thường, loại bỏ ký tự đặc biệt và khoảng trắng thừa
    text = text.lower()
    text = re.sub(r'[^a-zA-Z0-9\s]', '', text)  # Loại bỏ ký tự đặc biệt
    text = re.sub(r'\s+', ' ', text).strip()  # Xóa khoảng trắng thừa

    return text