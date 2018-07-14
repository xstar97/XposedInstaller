package de.robv.android.xposed.installer.core.repo.zips

data class Zips(val title: String, val icon: Int, val type: Int) {
    companion object {
        val TABLE_NAME_0 = "installer"
        val TABLE_NAME_1 = "uninstaller"
        val COLUMN_ID = "id"
        val COLUMN_TITLE = "title"
        val COLUMN_ICON = "icon"
        val COLUMN_TYPE = "type"
    }
}