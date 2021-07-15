package com.baltazarstudio.regular.context

import android.content.Context
import com.baltazarstudio.regular.database.dao.RegistroDAO
import com.baltazarstudio.regular.model.Registro

abstract class RegistroContext {
    companion object {
        
        private var mDAO: RegistroDAO? = null
        
        var textoPesquisa: String? = null
        var registrosParaExcluir: ArrayList<Registro> = arrayListOf()
    
        fun getDAO(context: Context): RegistroDAO {
            if (mDAO == null) mDAO = RegistroDAO(context)
            return mDAO!!
        }
    
        fun excluirMovimentosSelecionados(context: Context): Int {
            for (movimento in registrosParaExcluir) {
                getDAO(context).excluir(movimento)
            }
            
            return registrosParaExcluir.size
        }
    }
    
}