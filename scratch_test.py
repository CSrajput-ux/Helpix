import requests
import os

url = "https://router.huggingface.co/models/kokulan123/skin-lesion-classifier"
# Also try the huggingface.co domain
# url = "https://huggingface.co/api/models/kokulan123/skin-lesion-classifier"

headers = {
    "Authorization": "Bearer YOUR_TOKEN_HERE"
}

try:
    response = requests.get(url, headers=headers)
    print("GET STATUS:", response.status_code)
    print("GET RESPONSE:", response.text[:200])
except Exception as e:
    print("GET ERROR:", e)
