package com.example.billcalculator

import android.view.LayoutInflater
import android.view.ScrollCaptureCallback
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BillAdaptor : RecyclerView.Adapter<BillAdaptor.BillViewHolder>(){
    private var elcList: ArrayList<ElectricityModel> = ArrayList()
    private var onClickItem:((ElectricityModel) -> Unit)? = null
    private var onClickDeleteItem:((ElectricityModel) -> Unit)? = null

    fun addItems(items: ArrayList<ElectricityModel>){
        this.elcList = items
    }

    fun setOnClickItem(callback: (ElectricityModel)-> Unit){
        this.onClickItem = callback
    }

    fun setOnClickDeleteItem(callback: (ElectricityModel) -> Unit){
        this.onClickDeleteItem = callback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BillViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.display_del, parent,false)
    )

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val elc = elcList[position]
        holder.bindView(elc)
        holder.itemView.setOnClickListener{onClickItem?.invoke(elc)}
        holder.btnDelete.setOnClickListener{onClickDeleteItem?.invoke(elc)}
    }

    override fun getItemCount(): Int {
        return elcList.size
    }


    class BillViewHolder(var view: View) : RecyclerView.ViewHolder(view){
        private var id = view.findViewById<TextView>(R.id.id)
        private var nd = view.findViewById<TextView>(R.id.nd)
        private var un = view.findViewById<TextView>(R.id.un)
        private var tot = view.findViewById<TextView>(R.id.tot)
        var btnDelete = view.findViewById<TextView>(R.id.btnDelete)

        fun bindView(elc: ElectricityModel){
            id.text = elc.id.toString()
            nd.text = elc.nd.toString()
            un.text = elc.un.toString()
            tot.text = elc.tot.toString()
        }
    }

}