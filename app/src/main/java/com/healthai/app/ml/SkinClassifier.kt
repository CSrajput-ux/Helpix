package com.healthai.app.ml

import android.content.Context
import android.graphics.Bitmap
import com.healthai.app.BuildConfig
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/** Single-use classifier. Create, use, and close it from a background dispatcher. */
class SkinClassifier(context: Context) : AutoCloseable {

    data class Recognition(
        val label: String,
        val confidence: Float,
        val isUncertain: Boolean,
        val top3: List<Pair<String, Float>>,
        /** All seven post-SOFTMAX TFLite probabilities, in model-output order. */
        val rawProbabilities: List<Float>
    )

    private val appContext = context.applicationContext
    private val labels = loadLabels()
    private val interpreter = Interpreter(loadModelFile())
    private val inputTensor = interpreter.getInputTensor(0)
    private val outputTensor = interpreter.getOutputTensor(0)

    init {
        val inputShape = inputTensor.shape()
        check(inputTensor.dataType() == DataType.FLOAT32) {
            "Unsupported model input type: ${inputTensor.dataType()}"
        }
        check(inputShape.contentEquals(INPUT_SHAPE)) {
            "Unsupported model input shape: ${inputShape.contentToString()}"
        }
        check(outputTensor.dataType() == DataType.FLOAT32) {
            "Unsupported model output type: ${outputTensor.dataType()}"
        }
        check(outputTensor.shape().contentEquals(OUTPUT_SHAPE)) {
            "Unsupported model output shape: ${outputTensor.shape().contentToString()}"
        }
        check(labels.size == CLASS_COUNT) {
            "Model emits $CLASS_COUNT classes but $LABELS_PATH contains ${labels.size} labels"
        }
    }

    fun classifySkin(bitmap: Bitmap): Recognition {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(INPUT_HEIGHT, INPUT_WIDTH, ResizeOp.ResizeMethod.BILINEAR))
            // Verified published pipeline: torchvision.transforms.ToTensor() only.
            .add(NormalizeOp(0f, 255f))
            .build()

        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        tensorImage = imageProcessor.process(tensorImage)

        val probabilityBuffer = TensorBuffer.createFixedSize(outputTensor.shape(), DataType.FLOAT32)
        interpreter.run(tensorImage.buffer, probabilityBuffer.buffer)

        val rawProbabilities = probabilityBuffer.floatArray.toList()
        check(rawProbabilities.size == CLASS_COUNT) { "Model returned ${rawProbabilities.size} probabilities" }
        if (BuildConfig.DEBUG) {
            android.util.Log.d(TAG, "TFLite probabilities: ${rawProbabilities.joinToString(prefix = "[", postfix = "]")}")
        }

        val recognitionList = rawProbabilities
            .mapIndexed { index, confidence -> labels[index] to confidence }
            .sortedByDescending { it.second }

        val topResult = recognitionList.firstOrNull() ?: error("Model returned no classifications")
        return Recognition(
            label = topResult.first,
            confidence = topResult.second,
            isUncertain = topResult.second < CONFIDENCE_THRESHOLD,
            top3 = recognitionList.take(TOP_RESULTS_COUNT),
            rawProbabilities = rawProbabilities
        )
    }

    private fun loadLabels(): List<String> {
        val json = appContext.assets.open(LABELS_PATH).bufferedReader().use { it.readText() }
        val array = org.json.JSONArray(json)
        return List(array.length()) { index -> array.getString(index) }
    }

    private fun loadModelFile(): MappedByteBuffer =
        appContext.assets.openFd(MODEL_PATH).use { descriptor ->
            FileInputStream(descriptor.fileDescriptor).channel.use { channel ->
                channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    descriptor.startOffset,
                    descriptor.declaredLength
                )
            }
        }

    override fun close() = interpreter.close()

    companion object {
        private const val MODEL_PATH = "skin_cancer_model.tflite"
        private const val LABELS_PATH = "skin_labels.json"
        private const val TAG = "SkinClassifier"
        private const val INPUT_HEIGHT = 224
        private const val INPUT_WIDTH = 224
        private const val CLASS_COUNT = 7
        private const val TOP_RESULTS_COUNT = 3
        private const val CONFIDENCE_THRESHOLD = 0.4f
        private val INPUT_SHAPE = intArrayOf(1, INPUT_HEIGHT, INPUT_WIDTH, 3)
        private val OUTPUT_SHAPE = intArrayOf(1, CLASS_COUNT)
    }
}
