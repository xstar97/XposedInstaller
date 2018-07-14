package de.robv.android.xposed.installer.core.repo.zips

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

open class ZipDB(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "frameworks", null, 1)
{
   companion object
   {
        private var instance: ZipDB? = null

        @Synchronized
        fun getInstance(ctx: Context): ZipDB {
            if (instance == null) {
                instance = ZipDB(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(Zips.TABLE_NAME_0, true,
                Zips.COLUMN_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Zips.COLUMN_TITLE to TEXT,
                Zips.COLUMN_ICON to INTEGER,
                Zips.COLUMN_TYPE to INTEGER)
        db.createTable(Zips.TABLE_NAME_1, true,
                Zips.COLUMN_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Zips.COLUMN_TITLE to TEXT,
                Zips.COLUMN_ICON to INTEGER,
                Zips.COLUMN_TYPE to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable(Zips.TABLE_NAME_0, true)
        db.dropTable(Zips.TABLE_NAME_1, true)
    }
}
// Access property for Context
val Context.myZipDB: ZipDB
    get() = ZipDB.getInstance(applicationContext)