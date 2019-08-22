package com.baltazarstudio.regular.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.EconomiaDAO
import com.baltazarstudio.regular.model.Economia
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.activity_detalhes_economia.*
import kotlinx.android.synthetic.main.dialog_ajuste_poupanca.view.*
import java.math.BigDecimal

class DetalhesEconomiaActivity : AppCompatActivity() {

    private lateinit var item: Economia
    private var economiaDAO = EconomiaDAO(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_economia)
        supportActionBar?.title = getString(R.string.activity_title_detalhes_economia)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        button_ajustar_poupanca.setOnClickListener {
            createDialogAjustarPoupanca()
        }

        refreshDados()

    }

    @SuppressLint("InflateParams")
    private fun createDialogAjustarPoupanca() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_ajuste_poupanca, null)
        val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

        dialogView.button_dialog_ajuste_poupanca_confirmar.setOnClickListener {
            val valor = BigDecimal(Utils.unformatBRCurrency(dialogView.textinput_ajuste_valor_poupanca.text.toString()))

            if (valor > BigDecimal.ZERO) {
                if (dialogView.tab_ajuste_poupanca_movimento.selectedTabPosition == 0) {
                    economiaDAO.adicionarValorPoupanca(item, valor)
                    Toast.makeText(this, R.string.detalhes_economia_valor_poupanca_registrado, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    refreshDados()
                } else {
                    if (valor <= item.valorPoupanca) {
                        economiaDAO.retirarValorPoupanca(item, valor)
                        Toast.makeText(this, R.string.detalhes_economia_valor_poupanca_registrado, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        refreshDados()
                    } else {
                        Toast.makeText(this, R.string.dialog_ajuste_poupanca_valor_invalido, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, R.string.dialog_ajuste_poupanca_maior_que_zero, Toast.LENGTH_LONG).show()
            }
        }

        dialog.show()
    }

    private fun refreshDados() {
        item = economiaDAO.get(intent.getIntExtra("id", 0))

        tv_detalhes_economia_descricao.text = item.descricao
        tv_detalhes_economia_data.text = item.data
        tv_detalhes_economia_valor.text = Utils.formatCurrency(item.valor).replace("R$", "").trim()
        tv_detalhes_economia_valor_poupanca.text = Utils.formatCurrency(item.valorPoupanca).replace("R$", "").trim()

        if (item.valorPoupanca < item.valor) {
            tv_detalhes_economia_aviso_valor_atingido.visibility = View.GONE
        } else {
            tv_detalhes_economia_aviso_valor_atingido.visibility = View.VISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
