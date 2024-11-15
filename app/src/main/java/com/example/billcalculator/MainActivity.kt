package com.example.billcalculator
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var id: EditText
    private lateinit var nd: EditText
    private lateinit var un: EditText
    private lateinit var tot: TextView
    private lateinit var btnAdd: Button
    private lateinit var btnView: Button
    private lateinit var btnUpdate: Button

    private var sqliteHelper = SQLiteHelper(this)
    private lateinit var recyclerView: RecyclerView
    private var adaptor: BillAdaptor? = null
    private var elc:ElectricityModel? = null


    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.elc_home)


        val btnGoToMain = findViewById<Button>(R.id.calculate_button)
        btnGoToMain.setOnClickListener {
            setContentView(R.layout.activity_main)
            id = findViewById(R.id.id)
            nd = findViewById(R.id.nd)
            un = findViewById(R.id.un)
            tot = findViewById(R.id.tot)
            btnAdd = findViewById(R.id.btn_add)
            btnView = findViewById(R.id.btn_view)
            btnUpdate = findViewById(R.id.btn_update)

            initRecycleView()

            btnAdd.setOnClickListener{ addBill() }
            btnView.setOnClickListener{ getBill() }
            btnUpdate.setOnClickListener{ updateBill() }

            adaptor?.setOnClickItem {
                Toast.makeText(this,it.id.toString(),Toast.LENGTH_SHORT).show()
                id.setText(it.id.toString())
                nd.setText(it.nd.toString())
                un.setText(it.un.toString())

                elc = it
            }
            adaptor?.setOnClickDeleteItem {
                deleteBill(it.id)
            }


            un.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    val s = ""
                    Log.i(TAG, "afterTextChanged $s")
                    computeElectricity()
                }

            })
        }
        val btnGoToTips = findViewById<Button>(R.id.tips)
        btnGoToTips.setOnClickListener {
            startActivity(Intent(this,Tips_activity::class.java))

        }

    }

    private fun getBill() {
        val elcList = sqliteHelper.getBill()
        Log.e("pppp", "${elcList.size}")

        adaptor?.addItems(elcList)
    }

    private fun addBill(){
        val id = id.text.toString().toInt()
        val nd = nd.text.toString().toInt()
        val un = un.text.toString().toInt()
        val tot = tot.text.toString().toDouble()

        val elc = ElectricityModel(id = id, nd = nd, un = un, tot = tot)

        val status = sqliteHelper.insertBill(elc)
        if(status > -1){
            Toast.makeText(this,"Bill Added...", Toast.LENGTH_SHORT).show()
            clearText()
            getBill()
        }else{
            Toast.makeText(this,"Record Not Saved...", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateBill(){
        val id = id.text.toString().toInt()
        val nd = nd.text.toString().toInt()
        val un = un.text.toString().toInt()
        val tot = tot.text.toString().toDouble()

        if(id == elc?.id && nd == elc?.nd && un == elc?.un && tot == elc?.tot){
            Toast.makeText(this, "Record Not Changed...", Toast.LENGTH_SHORT).show()
            return
        }

        if(elc == null) return

        val elc = ElectricityModel(id = elc!!.id, nd = nd, un = un, tot = tot)
        val status = sqliteHelper.updateBill(elc)
        if(status > -1){
            clearText()
            getBill()
        } else{
            Toast.makeText(this,"Update Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteBill(id:Int){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete the record?...")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes"){dialog,_ ->
            sqliteHelper.deleteBillById(id)
            getBill()
            dialog.dismiss()
        }
        builder.setNegativeButton("No"){dialog,_ ->
            dialog.dismiss()
        }
        var alert = builder.create()
        alert.show()

    }

    private fun clearText() {
        id.setText("")
        nd.setText("")
        un.setText("")
        tot.setText("")
    }
    private fun initRecycleView(){
        recyclerView = findViewById(R.id.recycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adaptor = BillAdaptor()
        recyclerView.adapter = adaptor
    }

    private fun computeElectricity() {
        var units = if (un.text.isNotEmpty()) un.text.toString().toInt() else 0

        var totalCost = 0.0
        val rate1 = 7.85
        val rate2 = 10.00
        val rate3 = 27.75
        val rate4 = 32.00
        val rate5 = 45.00
        val range1 = 60
        val range2 = 90
        val range3 = 120
        val range4 = 180


        if (units <= range1) {
            totalCost = units * rate1
        } else {
            totalCost += range1 * rate1
            units -= range1

            if (units <= (range2 - range1)) {
                totalCost += units * rate2
            } else {
                totalCost += (range2 - range1) * rate2
                units -= (range2 - range1)

                if (units <= (range3 - range2)) {
                    totalCost += units * rate3
                } else {
                    totalCost += (range3 - range2) * rate3
                    units -= (range3 - range2)

                    if (units <= (range4 - range3)) {
                        totalCost += units * rate4
                    } else {
                        totalCost += (range4 - range3) * rate4
                        units -= (range4 - range3)

                        totalCost += units * rate5
                    }
                }
            }
        }

        tot.text = totalCost.toString()
    }


}