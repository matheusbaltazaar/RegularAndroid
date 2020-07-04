package com.baltazarstudio.regular.ui.movimentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.MovimentoDAO
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.ui.adapter.MovimentoAdapter
import kotlinx.android.synthetic.main.fragment_movimentos.view.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent


class MovimentosFragment : Fragment() {

    private lateinit var v: View
    private lateinit var movimentoDAO: MovimentoDAO
    private var firstUse: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_movimentos, container, false)

        movimentoDAO = MovimentoDAO(v.context)

        setUpView()
        setUpMovimentos()

        return v
    }

    private fun setUpView() {
        v.fab_add_movimento.setOnClickListener {
            val dialog = CriarMovimentoDialog(v.context, childFragmentManager)
            dialog.setOnDismissListener {
                setUpMovimentos()
            }
            dialog.show()
        }
    }

    private fun setUpMovimentos() {
        val itensMovimentos = movimentoDAO.getTodosMovimentos()

        val excluir = { item: Movimento ->
            AlertDialog.Builder(context!!)
                .setTitle("Excluir")
                .setMessage("Confirmar exclusão")
                .setPositiveButton("Sim") { _, _ ->
                    movimentoDAO.excluir(item)
                    Toast.makeText(context, "Removido!", Toast.LENGTH_SHORT).show()
                    setUpMovimentos()
                }
                .setNegativeButton("Não", null)
                .show()
        }

        v.ll_movimentos.removeAllViews()
        val lp = ViewGroup.LayoutParams(matchParent, wrapContent)

        for (ano in movimentoDAO.getAnosDisponiveis()) {
            for (mes in movimentoDAO.getMesDisponivelPorAno(ano)) {
                val itens = itensMovimentos.filter { it.mes == mes && it.ano == ano }

                val recyclerView = RecyclerView(v.context)
                recyclerView.layoutParams = lp

                val adapter = MovimentoAdapter(v.context, Pair(mes, ano), itens, excluir)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(v.context)

                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        v.context,
                        DividerItemDecoration.VERTICAL
                    )
                )

                v.ll_movimentos.addView(recyclerView)
            }
        }


    }

    override fun onResume() {
        super.onResume()

        if (activity?.intent?.action == "abrir_adicionar_movimento" && firstUse) {
            v.fab_add_movimento.performClick()
        }
        firstUse = false
    }
}