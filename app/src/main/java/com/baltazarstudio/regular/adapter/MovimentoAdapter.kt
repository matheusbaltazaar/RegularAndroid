package com.baltazarstudio.regular.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_item_movimento.view.*
import java.text.SimpleDateFormat
import java.util.*

class MovimentoAdapter(
    private var context: Context,
    private var itens: List<Movimento>,
    private var excluirMovimento: (Movimento) -> AlertDialog
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovimentoViewHolder(
            layoutInflater.inflate(
                R.layout.layout_item_movimento,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return itens.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovimentoViewHolder).bindView(position)
    }

    private inner class MovimentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val calendar = Calendar.getInstance()

        fun bindView(position: Int) {
            val movimento = itens[position]
            itemView.tv_movimento_descricao.text = movimento.descricao
            itemView.tv_movimento_valor.text = Utils.formatCurrency(movimento.valor)

            calendar.set(Calendar.DAY_OF_MONTH, movimento.dia)
            calendar.set(Calendar.MONTH, movimento.mes)
            calendar.set(Calendar.YEAR, movimento.ano)

            itemView.tv_movimento_data.text = calendar.formattedDate()

            itemView.setOnLongClickListener {
                excluirMovimento(movimento)
                true
            }
        }
    }
}