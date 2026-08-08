import asyncio
import sys
import os

# Ensure the app module can be found
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from app.core.config import settings

async def check_mongodb():
    print("Checking MongoDB...")
    from motor.motor_asyncio import AsyncIOMotorClient
    try:
        client = AsyncIOMotorClient(settings.MONGO_URL, serverSelectionTimeoutMS=5000)
        await client.admin.command('ping')
        print("[SUCCESS] MongoDB connection SUCCESS")
    except Exception as e:
        print(f"[FAILED] MongoDB connection FAILED: {e}")

def check_cloudinary():
    print("\nChecking Cloudinary...")
    import cloudinary
    import cloudinary.api
    
    if not settings.CLOUDINARY_CLOUD_NAME or not settings.CLOUDINARY_API_KEY:
        print("[FAILED] Cloudinary credentials not configured in .env")
        return
        
    try:
        cloudinary.config(
            cloud_name=settings.CLOUDINARY_CLOUD_NAME,
            api_key=settings.CLOUDINARY_API_KEY,
            api_secret=settings.CLOUDINARY_API_SECRET,
        )
        # Attempt to call the ping API
        result = cloudinary.api.ping()
        print("[SUCCESS] Cloudinary connection SUCCESS")
    except Exception as e:
        print(f"[FAILED] Cloudinary connection FAILED: {e}")

def check_supabase():
    print("\nChecking Supabase...")
    from app.core.supabase_client import get_supabase_client
    supabase = get_supabase_client()
    
    if not supabase:
        print("[FAILED] Supabase client failed to initialize (check URL and Key in .env)")
        return
        
    try:
        # Check if we can list buckets or check bucket existence
        buckets = supabase.storage.list_buckets()
        bucket_names = [b.name for b in buckets]
        print("[SUCCESS] Supabase connection SUCCESS")
        print(f"   Available buckets: {bucket_names}")
        
        if settings.SUPABASE_BUCKET in bucket_names:
            print(f"[SUCCESS] Bucket '{settings.SUPABASE_BUCKET}' exists.")
        else:
            print(f"[WARNING] Bucket '{settings.SUPABASE_BUCKET}' DOES NOT EXIST. Please create it in your Supabase dashboard.")
    except Exception as e:
        print(f"[FAILED] Supabase connection FAILED: {e}")

async def main():
    await check_mongodb()
    check_cloudinary()
    check_supabase()

if __name__ == "__main__":
    asyncio.run(main())
