package com.example.billcalculator

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context:Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "electricity.db"
        private const val TBL_ELECTRICITY = "tbl_electricity"
        private const val ID  = "id"
        private const val NO_DAYS = "nd"
        private const val UNIT = "un"
        private const val TOTAL = "tot"


    }
    override fun onCreate(p0: SQLiteDatabase?) {
        val createTblElectricity = ("CREATE TABLE " + TBL_ELECTRICITY + "("
                + ID + " INTEGER PRIMARY KEY," + NO_DAYS + " INTEGER," + UNIT + " INTEGER," + TOTAL + " DOUBLE" + ")")

        p0?.execSQL(createTblElectricity)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TBL_ELECTRICITY")
        onCreate(p0)
    }

    fun insertBill(elc: ElectricityModel): Long{
        val p0 = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, elc.id)
        contentValues.put(NO_DAYS, elc.nd)
        contentValues.put(UNIT, elc.un)
        contentValues.put(TOTAL, elc.tot)

        val success = p0.insert(TBL_ELECTRICITY, null, contentValues)
        p0.close()
        return success
    }


    @SuppressLint("Range")
    fun getBill(): ArrayList<ElectricityModel>{
        val elcList: ArrayList<ElectricityModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_ELECTRICITY"
        val p0 = this.readableDatabase

        val cursor: Cursor?

        try{
            cursor = p0.rawQuery(selectQuery, null)
        } catch(e: Exception){
            e.printStackTrace()
            p0.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var nd: Int
        var un: Int
        var tot: Double

        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex("id"))
                nd = cursor.getInt(cursor.getColumnIndex("nd"))
                un = cursor.getInt(cursor.getColumnIndex("un"))
                tot = cursor.getDouble(cursor.getColumnIndex("tot"))

                val elc = ElectricityModel(id = id, nd = nd, un = un, tot = tot)
                elcList.add(elc)
            } while (cursor.moveToNext())
        }

        return elcList
    }

    fun updateBill(elc: ElectricityModel) : Int{
        val p0 = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, elc.id)
        contentValues.put(NO_DAYS, elc.nd)
        contentValues.put(UNIT, elc.un)
        contentValues.put(TOTAL, elc.tot)

        val success = p0.update(TBL_ELECTRICITY, contentValues,"id=" + elc.id, null)
        p0.close()
        return success
    }

    fun deleteBillById(id:Int): Int{
        val p0 = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, id)

        val success = p0.delete(TBL_ELECTRICITY, "id=$id", null)
        p0.close()
        return success
    }
}














