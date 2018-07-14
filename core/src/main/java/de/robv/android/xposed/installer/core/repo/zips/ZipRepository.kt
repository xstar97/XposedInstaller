package de.robv.android.xposed.installer.core.repo.zips

import android.content.Context
import android.util.Log
import de.robv.android.xposed.installer.core.base.BaseXposedApp
import org.jetbrains.anko.db.*

class ZipRepository(val context: Context)
{
    fun getAllZips(tableName: String) : ArrayList<Zips> = context.myZipDB.use {
        val zips = ArrayList<Zips>()

        select(tableName, "title", "icon", "type")
                .parseList(object: MapRowParser<List<Zips>> {
                    override fun parseRow(columns: Map<String, Any?>): List<Zips> {
                        val title = columns.getValue("title")
                        val icon = columns.getValue("icon")
                        val type = columns.getValue("type")

                        val zip = Zips( title.toString(), icon.toString().toInt(), type.toString().toInt())
                        zips.add(zip)

                        Log.d(BaseXposedApp.TAG, "add:  $zip")
                        return zips
                    }
                })

        zips
    }

    fun addZip(tableName: String, zip: Zips) =  context.myZipDB.use {
        Log.d(BaseXposedApp.TAG, "adding zip..." + "\ntitle: ${zip.title}\nicon: ${zip.icon}\ntype: ${zip.type}")
        insert(tableName,
                "title" to zip.title,
                "icon" to zip.icon,
                "type" to zip.type)
    }

    fun updateZip(tableName: String, zip: Zips) = context.myZipDB.use {
        Log.d(BaseXposedApp.TAG, "updating zips..." + "\ntitle: ${zip.title}\nicon: ${zip.icon}\ntype: ${zip.type}")
        val updateResult = update(tableName, "title" to zip.title, "icon" to zip.icon, "type" to zip.type)
                //.whereArgs("id = {zipId}", "zipID" to Zips.COLUMN_ID)
                .whereArgs("title = {zipTitle}", "zipTitle" to zip.title)
                //.whereArgs("icon = {zipIcon}", "zipIcon" to zip.icon)
                //.whereArgs("type = {zipType}", "zipType" to zip.type)
                .exec()

        Log.d(BaseXposedApp.TAG, "Update result code is $updateResult")
    }

    //TODO add delete function
}