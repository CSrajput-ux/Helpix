package com.healthai.app.ui.screens.skin

import android.content.Context
import androidx.lifecycle.ViewModel
import com.healthai.app.ml.SkinClassifier
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Analyzes skin photos entirely on-device using a local TensorFlow Lite model.
 * This provides immediate results without needing an internet connection.
 */
@HiltViewModel
class SkinScanViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    suspend fun analyze(imagePath: String): SkinClassifier.Recognition = withContext(Dispatchers.IO) {
        val image = File(imagePath)
        check(image.isFile && image.length() > 0L) { "Selected image is unavailable." }

        val bitmap = SkinImageDecoder.decode(image)
        val classifier = SkinClassifier(context)

        try {
            classifier.classifySkin(bitmap)
        } finally {
            classifier.close()
            if (!bitmap.isRecycled) bitmap.recycle()
        }
    }
}
