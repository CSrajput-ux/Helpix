import os
import io
import numpy as np

try:
    from PIL import Image
    import cv2
    import tensorflow as tf
    from tensorflow.keras.applications import VGG19
    ML_AVAILABLE = True
except ImportError:
    ML_AVAILABLE = False

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
            # Load VGG19 without top layers for feature extraction
            _vgg_model = VGG19(weights='imagenet', include_top=False, input_shape=(180, 180, 3))
            
        if _disease_model is None:
            # Expecting 6claass.h5 in the root directory
            model_path = os.path.join(os.getcwd(), '6claass.h5')
            if os.path.exists(model_path):
                _disease_model = tf.keras.models.load_model(model_path)
    except Exception as e:
        print(f"Error loading models: {e}")
        return None, None
        
    return _vgg_model, _disease_model

def predict_skin_disease(image_bytes: bytes):
    """
    Analyzes an uploaded image bytes and returns the predicted disease class.
    Uses the logic from skin-detection-acc-98.ipynb.
    """
    if not ML_AVAILABLE:
        return None, "ML libraries (tensorflow, opencv-python) are not installed."
        
    vgg, disease_model = get_models()
    
    if disease_model is None:
        return None, "Model file '6claass.h5' not found in project root."
        
    try:
        # The notebook uses cv2.imread which loads as BGR. 
        # Using PIL to read bytes, then converting to BGR for cv2 matching behavior
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        img_array = np.array(image)
        # Convert RGB to BGR
        img_bgr = cv2.cvtColor(img_array, cv2.COLOR_RGB2BGR)
        
        # Resize and normalize
        img = cv2.resize(img_bgr, (180, 180))
        img = np.array(img) / 255.0
        img = np.expand_dims(img, axis=0)
        
        # 1. Feature extraction using VGG19
        features = vgg.predict(img)
        features = features.reshape(1, -1)
        
        # 2. Disease classification prediction
        start_pred = disease_model.predict(features)
        pred_probs = start_pred[0]
        
        predicted_class_index = np.argmax(pred_probs)
        predicted_class_name = CLASS_NAMES[predicted_class_index]
        confidence = float(pred_probs[predicted_class_index])
        
        return {
            "condition": predicted_class_name,
            "confidence": confidence,
            "severity": "high" if confidence > 0.8 else "moderate"
        }, None
        
    except Exception as e:
        return None, f"Analysis error: {str(e)}"
