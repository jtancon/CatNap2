package com.example.catnap2

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

fun insertProduct(dbHelper: CatNapDBHelper, name: String, descricao: String, preco: Double, foto: ByteArray?) {
    val db: SQLiteDatabase = dbHelper.writableDatabase

    val values = ContentValues().apply {
        put(CatNapDBHelper.COLUMN_NAME, name)
        put(CatNapDBHelper.COLUMN_DESC, descricao)
        put(CatNapDBHelper.COLUMN_PRICE, preco)
        put(CatNapDBHelper.COLUMN_PHOTO, foto)
    }

    db.insert(CatNapDBHelper.TABLE_PRODUCTS, null, values)
}
