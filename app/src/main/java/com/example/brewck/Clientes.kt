package com.example.brewck

import Cliente
import ClienteAdapter
import FirebaseRepository
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Clientes : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var btnVoltar : Button

    private lateinit var btnCadastrarBarril : ImageView
    private lateinit var btnAtualizar : TextView

    private lateinit var clienteAdapter : ClienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_clientes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnVoltar = findViewById(R.id.btnVoltarCliente)
        btnVoltar.setOnClickListener {
            finish()
        }

        btnAtualizar = findViewById(R.id.btnAtualizarCliente)
        btnAtualizar.setOnClickListener {
            recreate()
        }

        btnCadastrarBarril = findViewById(R.id.btnCadastrarCliente)
        btnCadastrarBarril.setOnClickListener {
            val intent = Intent(this, AdicionarCliente::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        recyclerView = findViewById(R.id.recyclerViewClientes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        buscarClientePorEmail()
    }

    companion object {
        private const val REQUEST_CODE = 1
    }

    private fun buscarClientePorEmail() {
        FirebaseRepository().pegarEmail { email ->
            if (email != null) {
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("clientes")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val listaClientes = mutableListOf<Pair<String, Cliente>>()
                        for (document in querySnapshot.documents) {
                            val id = document.id
                            val nome = document.getString("nome") ?: ""
                            val cpf = document.getString("cpf") ?: ""
                            val barril = document.getString("barril") ?: ""
                            val endereco = document.getString("endereco") ?: ""
                            listaClientes.add(id to Cliente(nome, cpf, barril, endereco))
                        }

                        listaClientes.sortBy { it.second.nome }

                        clienteAdapter = ClienteAdapter(listaClientes.map { it.second }) { perfil ->
                            val intent = Intent(this, EditarCliente::class.java)
                            intent.putExtra("id", listaClientes.find { it.second == perfil }?.first)
                            intent.putExtra("nome", perfil.nome)
                            intent.putExtra("cpf", perfil.cpf)
                            intent.putExtra("barril", perfil.barril)
                            intent.putExtra("endereco", perfil.endereco)
                            startActivityForResult(intent, REQUEST_CODE)
                        }

                        recyclerView.adapter = clienteAdapter
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            this,
                            "Erro ao carregar clientes: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                buscarClientePorEmail()
            }
        }
    }
}