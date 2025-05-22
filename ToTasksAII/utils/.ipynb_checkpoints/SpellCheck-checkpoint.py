import re
import nltk
from nltk.corpus import stopwords
# from spellchecker import Spellchecker
from spellchecker import SpellChecker
from nltk.stem import WordNetLemmatizer

# nltk.download('stopwords')
# nltk.download('wordnet')

# Tạo đối tượng SpellChecker
spell = SpellChecker()
lemmatizer = WordNetLemmatizer()


# Hàm sửa lỗi chính tả
# def correct_spelling(text):
#     words = text.split()
#     corrected_words = [spell.correction(word) for word in words]
#     return ' '.join(corrected_words)

def correct_spelling(text):
    words = text.split()
    corrected_words = [
        spell.correction(word) if spell.correction(word) is not None else word
        for word in words
    ]
    return ' '.join(corrected_words)

# def correct_spelling(text):
#     words = text.split()
#     corrected_words = []
#     for word in words:
#         corrected = spell.correction(word.lower())
#         if word.istitle():
#             corrected = corrected.capitalize()
#         elif word.isupper():
#             corrected = corrected.upper()
#         corrected_words.append(corrected)
#     return ' '.join(corrected_words)


# Hàm tiền xử lý dữ liệu (bao gồm sửa lỗi chính tả)
def preprocess_text_with_spell_check(text):
    # Tiền xử lý cơ bản: chuyển về chữ thường, loại bỏ ký tự đặc biệt và khoảng trắng thừa
    text = text.lower()
    text = re.sub(r'[^a-zA-Z0-9\s]', '', text)  # Loại bỏ ký tự đặc biệt
    text = re.sub(r'\s+', ' ', text).strip()  # Xóa khoảng trắng thừa

    # Loại bỏ stopwords và lemmatization
    stop = set(stopwords.words('english'))
    words = text.split()
    lemmatized_words = [lemmatizer.lemmatize(word) for word in words if word not in stop]
    text = ' '.join(lemmatized_words)

    # Sửa lỗi chính tả
    text = correct_spelling(text)

    return text