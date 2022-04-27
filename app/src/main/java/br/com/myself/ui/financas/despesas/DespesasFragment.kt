package br.com.myself.ui.financas.despesas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.databinding.FragmentDespesasBinding
import br.com.myself.domain.entity.Despesa
import br.com.myself.ui.adapter.DespesaAdapter
import br.com.myself.util.Utils
import br.com.myself.viewmodel.DespesasFragmentViewModel
import org.jetbrains.anko.support.v4.toast

class DespesasFragment : Fragment(R.layout.fragment_despesas) {
    
    private val viewModel: DespesasFragmentViewModel by viewModels()
    private var _binding: FragmentDespesasBinding? = null
    private val binding get() = _binding!!
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.buttonDespesasInfo.setOnClickListener {
            binding.cardDespesasInfo.visibility = View.VISIBLE
            binding.cardDespesasInfo.animation = null
        }
        
        binding.buttonCardDespesasInfoClose.setOnClickListener {
            binding.cardDespesasInfo.animation =
                AnimationUtils.loadAnimation(it.context, android.R.anim.fade_out)
            binding.cardDespesasInfo.visibility = View.GONE
        }
        
        binding.buttonAdicionar.setOnClickListener {
            CriarDespesaDialog(it.context) { dialog, despesa ->
                viewModel.salvar(despesa) {
                    toast("Salvo!")
                    dialog.dismiss()
                }
            }.show()
        }
    
        setUpAdapter()
    
        viewModel.despesas.observe(viewLifecycleOwner) { despesas ->
            (binding.recyclerviewDespesas.adapter as DespesaAdapter).submitList(despesas)
            binding.textviewSemDepesas.visibility =
                if (despesas.isEmpty()) View.VISIBLE else View.GONE
        }
        
    }
    
    private fun setUpAdapter() {
        val adapter = DespesaAdapter()
        binding.recyclerviewDespesas.adapter = adapter
        binding.recyclerviewDespesas.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnItemActionListener { action, despesa ->
            when (action) {
                DespesaAdapter.ACTION_EXCLUIR -> confirmarExcluirDespesa(despesa)
                DespesaAdapter.ACTION_DETALHES -> {
                    startActivity(Intent(context,
                        DetalhesDespesaActivity::class.java).apply {
                            putExtra(DetalhesDespesaActivity.DESPESA_ID,
                                despesa.id)
                        })
                }
                DespesaAdapter.ACTION_REGISTRAR -> {
                    viewModel.getSugestoes(despesa) { sugestoes ->
                        val dialog =
                            RegistrarDespesaDialog(despesa, sugestoes) { dialog, valor, data ->
                                viewModel.registrarDespesa(despesa, valor, data) {
                                    toast("Registrado!")
                                    dialog.dismiss()
                                }
                            }
                        dialog.show(childFragmentManager, null)
                    }
                }
            }
        }
    }
    
    private fun confirmarExcluirDespesa(despesa: Despesa) {
        var mensagem = "Nome: ${despesa.nome}"
        mensagem += "\nValor: ${Utils.formatCurrency(despesa.valor)}"
        if (despesa.diaVencimento != 0) mensagem += "\nVencimento: ${despesa.diaVencimento}"
        
        AlertDialog.Builder(requireContext()).setTitle("Excluir despesa?")
            .setMessage(mensagem)
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.excluir(despesa) {
                    toast("Removido!")
                }
            }.setNegativeButton("Cancelar", null)
            .show()
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}