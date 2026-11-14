import asyncio

from app.core.db import close_db, connect_db
import app.core.db as db_module


async def check_users():
    try:
        await connect_db()
        users_col = db_module.db["users"]
        count = await users_col.count_documents({})
        print(f"Total Users in DB: {count}")

        if count > 0:
            for user in await users_col.find().to_list(count):
                print(
                    f"User: {user.get('full_name')} ({user.get('email')}) "
                    f"- Role: {user.get('role')}"
                )
        else:
            print("No users found in database.")

    except Exception as e:
        print(f"Error: {e}")
    finally:
        await close_db()


if __name__ == "__main__":
    asyncio.run(check_users())
