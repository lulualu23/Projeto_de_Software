package com.example.brewck

import Barril
import BarrilAdapter
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

class Barris : AppCompatActivity() {
    private lateinit var btnVoltar : Button
    private lateinit var btnCadastrarBarril : ImageView

    private lateinit var recyclerView : RecyclerView
    private lateinit var barrilAdapter : BarrilAdapter

    private lateinit var btnAtualizar : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_barris)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnAtualizar = findViewById(R.id.btnAtualizar)
        btnAtualizar.setOnClickListener {
            recreate()
        }

        btnCadastrarBarril = findViewById(R.id.btnCadastrarBarril)
        btnCadastrarBarril.setOnClickListener {
            val intent = Intent(this, AdicionarBarril::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        btnVoltar = findViewById(R.id.btnVoltarBarril)
        btnVoltar.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerViewBarris)
        recyclerView.layoutManager = LinearLayoutManager(this)

        buscarBarrilPorEmail()
    }

    companion object {
        private const val REQUEST_CODE = 1
    }

    private fun buscarBarrilPorEmail() {
        FirebaseRepository().pegarEmail { email ->
            if (email != null) {
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("barris")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val barris = mutableListOf<Pair<String, Barril>>()
                        for (document in querySnapshot.documents) {
                            val id = document.id
                            val nome = document.getString("nome") ?: ""
                            val capacidade = document.getLong("capacidade")?.toInt() ?: 0
                            val propriedade = document.getString("propriedade") ?: ""
                            val status = document.getString("status") ?: ""
                            val liquido = document.getString("liquido") ?: ""
                            barris.add(id to Barril(nome, capacidade, propriedade, status, liquido))
                        }

                        barris.sortBy { it.second.nome }

                        barrilAdapter = BarrilAdapter(barris.map { it.second }) { barril ->
                            val intent = Intent(this, EditarBarril::class.java)
                            intent.putExtra("id", barris.find { it.second == barril }?.first)
                            intent.putExtra("nome", barril.nome)
                            intent.putExtra("capacidade", barril.capacidade)
                            intent.putExtra("propriedade", barril.propriedade)
                            intent.putExtra("status", barril.status)
                            intent.putExtra("liquido", barril.liquido)
                            startActivityForResult(intent, REQUEST_CODE)
                        }

                        recyclerView.adapter = barrilAdapter
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            this,
                            "Erro ao carregar barris: ${exception.message}",
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
                buscarBarrilPorEmail()
            }
        }
    }
}
