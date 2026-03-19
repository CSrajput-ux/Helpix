package com.healthai.app.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.MappedByteBuffer

class SkinClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null

    data class Recognition(
        val label: String,
        val confidence: Float
    )

    companion object {

        private const val TAG = "SkinClassifier"
        private const val MODEL_PATH = "skin_model.tflite"
        private const val INPUT_SIZE = 224

        private val LABELS = listOf(
            "Acne",
            "Eczema",
            "Psoriasis",
            "Fungal Infection",
            "Normal Skin"
        )
    }

    init {
        loadModel()
    }

    private fun loadModel() {

        try {

            val model: MappedByteBuffer =
                FileUtil.loadMappedFile(context, MODEL_PATH)

            val options = Interpreter.Options()

            options.setNumThreads(4)

            interpreter = Interpreter(model, options)

            Log.d(TAG, "Model loaded successfully")

        } catch (e: Exception) {

            Log.e(TAG, "Model loading error: ${e.message}")

        }
    }

    fun classifySkin(bitmap: Bitmap): Recognition {

        val currentInterpreter =
            interpreter ?: return Recognition("Initialization Error", 0f)

        try {

            val tensorImage = preprocessImage(bitmap)

            val outputBuffer =
                TensorBuffer.createFixedSize(
                    intArrayOf(1, LABELS.size),
                    DataType.FLOAT32
                )

            currentInterpreter.run(
                tensorImage.buffer,
                outputBuffer.buffer
            )

            return getTopResult(outputBuffer.floatArray)

        } catch (e: Exception) {

            Log.e(TAG, "Inference error: ${e.message}")

            return Recognition("Inference Error", 0f)
        }
    }

    private fun preprocessImage(bitmap: Bitmap): TensorImage {

        val imageProcessor =
            ImageProcessor.Builder()
                .add(
                    ResizeOp(
                        INPUT_SIZE,
                        INPUT_SIZE,
                        ResizeMethod.BILINEAR
                    )
                )
                .add(
                    NormalizeOp(0f, 255f)
                )
                .build()

        val tensorImage =
            TensorImage(DataType.FLOAT32)

        tensorImage.load(bitmap)

        return imageProcessor.process(tensorImage)
    }

    private fun getTopResult(confidences: FloatArray): Recognition {

        var maxIdx = 0
        var maxConf = -1f

        for (i in confidences.indices) {

            if (confidences[i] > maxConf) {

                maxConf = confidences[i]

                maxIdx = i
            }
        }

        return Recognition(
            label =
                if (maxIdx < LABELS.size)
                    LABELS[maxIdx]
                else
                    "Unknown",

            confidence = maxConf
        )
    }

    fun close() {

        interpreter?.close()

        interpreter = null

        Log.d(TAG, "Classifier closed")
    }
}