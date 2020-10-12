package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.database.Database 
import com.baltazarstudio.regular.model.Movimento

class MovimentoDAO(context: Context) : Database<Movimento>(context) {
    
    private var movimentosCache = arrayListOf<Movimento>()
    
    fun getTodosMovimentos(): List<Movimento> {
        if (MovimentoContext.useCache)
            return movimentosCache
        
        val movimentos = ArrayList<Movimento>()
        val query = "SELECT * FROM $TABELA ORDER BY $DATA DESC, $TABLE_ID DESC"
        
        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Movimento()
            bind(cursor, item)
            
            movimentos.add(item)
        }
        cursor.close()
        
        movimentosCache = movimentos
        MovimentoContext.useCache = true
        return movimentos
    }
    
    
    fun getTodosMovimentos(pesquisa: String): List<Movimento> {
        val movimentos = ArrayList<Movimento>()
        val query =
            "SELECT * FROM $TABELA WHERE $DESCRICAO LIKE '%$pesquisa%' ORDER BY $DATA, $TABLE_ID DESC"
        
        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Movimento()
            bind(cursor, item)
            
            movimentos.add(item)
        }
        cursor.close()
        
        return movimentos
    }
    
    fun getMovimentosPorTipo(tipoMovimento: Int): ArrayList<Movimento> {
        val movimentos = ArrayList<Movimento>()
        
        if (MovimentoContext.useCache) {
            movimentos.addAll(movimentosCache.filter { it.tipoMovimento == tipoMovimento })
            return movimentos
        }
        
        val query =
            "SELECT * FROM $TABELA WHERE $TIPO_MOVIMENTO = $tipoMovimento ORDER BY $DATA, $TABLE_ID DESC"
    
        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Movimento()
            bind(cursor, item)
        
            movimentos.add(item)
        }
        cursor.close()
    
        return movimentos
    }
    
    fun inserir(movimento: Movimento) {
        val insert = writableDatabase.compileStatement(
            "INSERT INTO $TABELA (" +
                "$DESCRICAO," +
                "$VALOR," +
                "$DATA," +
                "$TIPO_MOVIMENTO," +
                "$REFERENCIA_DESPESA)" +
                " VALUES (?,?,?,?,?)")
    
        insert.bindString(1, movimento.descricao)
        insert.bindDouble(2, movimento.valor)
        movimento.data?.let { insert.bindLong(3, it) } ?: insert.bindNull(3)
        insert.bindLong(4, movimento.tipoMovimento!!.toLong())
        movimento.referenciaDespesa?.let { insert.bindLong(5, it.toLong()) } ?: insert.bindNull(5)
    
        insert.executeInsert()
        
        MovimentoContext.useCache = false
    }
    
    fun alterar(movimento: Movimento) {
        val update =
            "UPDATE $TABELA" + " SET " +
                    "$DESCRICAO = '${movimento.descricao}'," +
                    "$DATA = ${movimento.data}," +
                    "$VALOR = ${movimento.valor}" +
                    " WHERE $TABLE_ID = ${movimento.id}"
        
        writableDatabase.execSQL(update)
    
        MovimentoContext.useCache = false
    }
    
    fun excluir(movimento: Movimento) {
        val query = "DELETE FROM $TABELA WHERE $TABLE_ID = ${movimento.id}"
        writableDatabase.execSQL(query)
    
        MovimentoContext.useCache = false
    }
    
    override fun bind(cursor: Cursor, elemento: Movimento) {
        elemento.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        elemento.descricao = cursor.getString(cursor.getColumnIndex(DESCRICAO))
        elemento.data = cursor.getLong(cursor.getColumnIndex(DATA))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(VALOR))
        elemento.tipoMovimento = cursor.getInt(cursor.getColumnIndex(TIPO_MOVIMENTO))
        elemento.referenciaDespesa = cursor.getInt(cursor.getColumnIndex(REFERENCIA_DESPESA))
    }
    
    fun restaurarMovimentos(movimentos: List<Movimento>?) {
        val db = writableDatabase
        db.beginTransaction()
        
        //db.execSQL("DELETE FROM $TABELA")
        
        if (!movimentos.isNullOrEmpty()) {
            val sqlInsertStatement = "INSERT INTO $TABELA (" +
                    "$DESCRICAO," +
                    "$VALOR," +
                    "$DATA," +
                    "$TIPO_MOVIMENTO," +
                    "$REFERENCIA_DESPESA)" +
                    " VALUES (?, ?, ?, ?, ?)"
            val stmt = db.compileStatement(sqlInsertStatement)
    
            movimentos.forEach { movimento ->
                stmt.bindString(1, movimento.descricao)
                stmt.bindDouble(2, movimento.valor)
                movimento.data?.let { stmt.bindLong(3, it) } ?: stmt.bindNull(3)
                stmt.bindLong(4, movimento.tipoMovimento!!.toLong())
                movimento.referenciaDespesa?.let { stmt.bindLong(5, it.toLong()) } ?: stmt.bindNull(5)
        
                stmt.executeInsert()
                stmt.clearBindings()
            }
            
            db.setTransactionSuccessful()
            MovimentoContext.useCache = false
        }
        
        db.endTransaction()
    }
    
    companion object {
        internal const val TABELA = "Movimento"
        
        private const val DESCRICAO = "descricao"
        internal const val DATA = "data"
        private const val VALOR = "valor"
        const val TIPO_MOVIMENTO = "tipo_movimento"
        const val REFERENCIA_DESPESA = "referencia_despesa"
        
        fun onCreate(db: SQLiteDatabase) {
            val create =
                "CREATE TABLE $TABELA (" +
                        "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$DESCRICAO TEXT," +
                        "$DATA NUMERIC," +
                        "$VALOR DECIMAL(10, 2)," +
                        "$TIPO_MOVIMENTO INTEGER," +
                        "$REFERENCIA_DESPESA INTEGER" +
                        ")"
            
            db.execSQL(create)
        }
        
    }
    
}
