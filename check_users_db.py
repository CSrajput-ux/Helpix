import asyncio
import os
from motor.motor_asyncio import AsyncIOMotorClient
from dotenv import load_dotenv

# Load .env
load_dotenv()

async def check_users():
    mongo_url = os.getenv("MONGO_URL")
    db_name = os.getenv("MONGO_DB_NAME", "helpix_ai")
    
    try:
        client = AsyncIOMotorClient(mongo_url)
        db = client[db_name]
        users_col = db["users"]
        
        count = await users_col.count_documents({})
        print(f"Total Users in DB: {count}")
        
        if count > 0:
            cursor = users_col.find({}, {"password": 0, "hashed_password": 0})
            async for user in cursor:
                print(f"User: {user.get('full_name')} ({user.get('email')}) - Role: {user.get('role')}")
        else:
            print("No users found in database.")
            
    except Exception as e:
        print(f"Error: {e}")
    finally:
        client.close()

if __name__ == "__main__":
    asyncio.run(check_users())
