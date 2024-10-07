package com.example.catnap2;
import Product
import ProductAdapter
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var filteredList: MutableList<Product>
    private lateinit var dbHelper: CatNapDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchEditText = findViewById<EditText>(R.id.EditText)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // Inicializar o DBHelper
        dbHelper = CatNapDBHelper(this)

        // Inserir produtos no banco de dados (apenas na primeira vez)
        insertInitialProducts()

        // Carregar produtos do banco de dados
        productList = loadProductsFromDatabase()
        filteredList = productList.toMutableList()

        // Configurar o Adapter
        adapter = ProductAdapter(filteredList) { product ->
            val intent = Intent(this, PagamentoActivity::class.java)
            intent.putExtra("PRODUCT_NAME", product.name)
            intent.putExtra("PRODUCT_PRICE", product.preco)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Adicionar TextWatcher para a pesquisa
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Inserção inicial de produtos no banco de dados
    private fun insertInitialProducts() {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val productExistsQuery = "SELECT COUNT(*) FROM ${CatNapDBHelper.TABLE_PRODUCTS}"
        val cursor = db.rawQuery(productExistsQuery, null)

        // Verifica se a tabela já tem produtos
        if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
            val products = listOf(
                Product("Café com Leite", R.drawable.produto1, "Café com leite integral com amor felino", "10,00"),
                Product("Cappuccino", R.drawable.produto2, "Cappuccino com espuma cremosa e canela", "12,00"),
                Product("Espresso", R.drawable.produto3, "Espresso curto, forte e saboroso", "8,00"),
                Product("Macchiato", R.drawable.produto1, "Café espresso com uma pitada de leite", "9,00"),
                Product("Mocha", R.drawable.produto2, "Café com chocolate amargo e leite", "15,00"),
                Product("Latte", R.drawable.produto3, "Café espresso com bastante leite vaporizado", "11,00"),
                Product("Café Americano", R.drawable.produto1, "Café espresso diluído em água quente", "7,00"),
                Product("Café Gelado", R.drawable.produto2, "Café gelado com um toque de baunilha", "13,00"),
                Product("Chá Gelado", R.drawable.produto3, "Chá preto gelado com limão e hortelã", "10,00"),
                Product("Suco de Laranja", R.drawable.produto1, "Suco de laranja natural espremido na hora", "9,00"),
                Product("Croissant", R.drawable.produto2, "Croissant amanteigado, fresco e crocante", "6,00"),
                Product("Pão de Queijo", R.drawable.produto3, "Clássico pão de queijo mineiro quentinho", "5,00"),
                Product("Torrada com Manteiga", R.drawable.produto1, "Torrada francesa com manteiga derretida", "8,00"),
                Product("Bolo de Cenoura", R.drawable.produto2, "Bolo de cenoura com cobertura de chocolate", "7,00"),
                Product("Biscoito de Chocolate", R.drawable.produto3, "Biscoito de chocolate crocante", "4,00"),
                Product("Torta de Limão", R.drawable.produto1, "Torta de limão com base de biscoito crocante", "12,00"),
                Product("Muffin de Blueberry", R.drawable.produto2, "Muffin fofo com pedaços de blueberry", "9,00"),
                Product("Brownie", R.drawable.produto3, "Brownie de chocolate com nozes", "10,00"),
                Product("Sanduíche de Frango", R.drawable.produto1, "Sanduíche de frango grelhado com alface", "15,00"),
                Product("Salada de Frutas", R.drawable.produto2, "Salada de frutas frescas da estação", "8,00")
            )

            for (product in products) {
                val values = ContentValues().apply {
                    put(CatNapDBHelper.COLUMN_NAME, product.name)
                    put(CatNapDBHelper.COLUMN_DESC, product.descricao)
                    put(CatNapDBHelper.COLUMN_PRICE, product.preco.toDouble())
                    put(CatNapDBHelper.COLUMN_PHOTO, product.imageResId) // Se quiser inserir fotos, insira aqui o array de bytes
                }
                db.insert(CatNapDBHelper.TABLE_PRODUCTS, null, values)
            }
        }

        cursor.close()
    }

    // Carregar produtos do banco de dados
    private fun loadProductsFromDatabase(): MutableList<Product> {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val productList = mutableListOf<Product>()

        val cursor = db.query(
            CatNapDBHelper.TABLE_PRODUCTS,
            arrayOf(CatNapDBHelper.COLUMN_NAME, CatNapDBHelper.COLUMN_DESC, CatNapDBHelper.COLUMN_PRICE, CatNapDBHelper.COLUMN_PHOTO),
            null, null, null, null, null
        )

        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(CatNapDBHelper.COLUMN_NAME))
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(CatNapDBHelper.COLUMN_DESC))
            val preco = cursor.getDouble(cursor.getColumnIndexOrThrow(CatNapDBHelper.COLUMN_PRICE)).toString()
            // Recuperar a foto, se houver
            productList.add(Product(name, R.drawable.produto1, descricao, preco))
        }

        cursor.close()
        return productList
    }

    // Função para filtrar os produtos
    private fun filterProducts(query: String) {
        val lowerCaseQuery = query.lowercase()
        filteredList.clear()

        for (product in productList) {
            if (product.name.lowercase().contains(lowerCaseQuery) ||
                product.descricao.lowercase().contains(lowerCaseQuery)) {
                filteredList.add(product)
            }
        }

        adapter.notifyDataSetChanged()
    }
}
