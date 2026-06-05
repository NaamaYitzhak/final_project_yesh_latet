import pymysql
from config import DB_CONFIG

def get_db():
    return pymysql.connect(
        **DB_CONFIG,
        autocommit=False,
        charset="utf8mb4",
        cursorclass=pymysql.cursors.DictCursor
    )