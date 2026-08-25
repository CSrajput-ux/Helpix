"""
app/services/skin_scanner.py
-----------------------------
Skin disease prediction using VGG19 feature extraction and a custom classifier.
Falls back gracefully if ML libraries are not installed or the model file is missing.
"""

import logging
import os
import io

try:
    import numpy as np
    from PIL import Image
    import cv2
    import tensorflow as tf
    from tensorflow.keras.applications import VGG19
    ML_AVAILABLE = True
except ImportError:
    ML_AVAILABLE = False

logger = logging.getLogger(__name__)

CLASS_NAMES = ["Acne", "Eczema", "Atopic dermatitis", "Psoriasis", "Tinea", "Vitiligo"]

# Cache the models globally to avoid reloading on every request
_vgg_model = None
_disease_model = None


def get_models():
    global _vgg_model, _disease_model
    if not ML_AVAILABLE:
        return None, None

    try:
        if _vgg_model is None:
            _vgg_model = VGG19(weights="imagenet", include_top=False, input_shape=(180, 180, 3))

        if _disease_model is None:
            model_path = os.path.join(os.getcwd(), "6class.h5")
            if os.path.exists(model_path):
                _disease_model = tf.keras.models.load_model(model_path)
    except Exception as e:
        logger.error("Error loading skin scanner models: %s", e)
        return None, None

    return _vgg_model, _disease_model


def predict_skin_disease(image_bytes: bytes):
    """
    Analyzes uploaded image bytes and returns the predicted disease class.
    Uses VGG19 for feature extraction followed by a custom classifier.

    Returns:
        (dict, None) on success with keys: condition, confidence, severity
        (None, str) on failure with an error message
    """
    if not ML_AVAILABLE:
        return None, "ML libraries (tensorflow, opencv-python) are not installed."

    vgg, disease_model = get_models()

    if disease_model is None:
        return None, "Model file '6class.h5' not found in project root."

    try:
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        img_array = np.array(image)
        img_bgr = cv2.cvtColor(img_array, cv2.COLOR_RGB2BGR)

        img = cv2.resize(img_bgr, (180, 180))
        img = np.array(img) / 255.0
        img = np.expand_dims(img, axis=0)

        features = vgg.predict(img)
        features = features.reshape(1, -1)

        start_pred = disease_model.predict(features)
        pred_probs = start_pred[0]

        predicted_class_index = np.argmax(pred_probs)
        predicted_class_name = CLASS_NAMES[predicted_class_index]
        confidence = float(pred_probs[predicted_class_index])

        # Get Top 3
        top_indices = np.argsort(pred_probs)[-3:][::-1]
        top_3 = [
            {"label": CLASS_NAMES[i], "confidence": float(pred_probs[i])}
            for i in top_indices if i < len(CLASS_NAMES)
        ]

        return {
            "condition": predicted_class_name,
            "confidence": confidence,
            "severity": "high" if confidence > 0.8 else "moderate",
            "top_3": top_3
        }, None

    except Exception as e:
        return None, f"Analysis error: {str(e)}"
