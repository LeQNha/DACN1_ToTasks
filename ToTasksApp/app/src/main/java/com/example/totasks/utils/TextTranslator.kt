package com.example.totasks.utils

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.*
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier

class TextTranslator {
    companion object {
        fun translateToEnglish(
            text: String,
            onResult: (String) -> Unit,
            onError: (String) -> Unit
        ) {
            // 1. Tự động nhận diện ngôn ngữ
            val languageIdentifier: LanguageIdentifier = LanguageIdentification.getClient()
            languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener { languageCode ->
                    if (languageCode == "und") {
                        onError("Không xác định được ngôn ngữ.")
                    } else {
                        // 2. Tạo translator từ ngôn ngữ gốc sang tiếng Anh
                        val options = TranslatorOptions.Builder()
                            .setSourceLanguage(languageCode)
                            .setTargetLanguage(TranslateLanguage.ENGLISH)
                            .build()
                        val translator = Translation.getClient(options)

                        // 3. Tải model nếu cần
                        val conditions = DownloadConditions.Builder().build()
                        translator.downloadModelIfNeeded(conditions)
                            .addOnSuccessListener {
                                // 4. Dịch văn bản
                                translator.translate(text)
                                    .addOnSuccessListener { translatedText ->
                                        onResult(translatedText)
                                    }
                                    .addOnFailureListener { e ->
                                        onError("Lỗi khi dịch: ${e.message}")
                                    }
                            }
                            .addOnFailureListener { e ->
                                onError("Lỗi khi tải mô hình: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    onError("Lỗi khi nhận diện ngôn ngữ: ${e.message}")
                }
        }
    }
}