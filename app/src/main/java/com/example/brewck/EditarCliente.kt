package com.example.brewck

import FirebaseRepository
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditarCliente : AppCompatActivity() {
    private lateinit var edtClienteNome : EditText
    private lateinit var edtClienteCPF : EditText
    private lateinit var edtClienteBarril : EditText
    private lateinit var edtClienteEndereco : EditText
    private lateinit var btnVoltarCliente : Button
    private lateinit var btnEditarCliente : Button
    private lateinit var btnDeletarCliente : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_cliente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nome = intent.getStringExtra("nome")
        val cpf = intent.getStringExtra("cpf")
        val barril = intent.getStringExtra("barril")
        val endereco = intent.getStringExtra("endereco")

        edtClienteNome = findViewById(R.id.edtClienteNome)
        edtClienteCPF = findViewById(R.id.edtClienteCPF)
        edtClienteBarril = findViewById(R.id.edtClienteBarril)
        edtClienteEndereco = findViewById(R.id.edtClienteEndereço)

        edtClienteNome.setText(nome.toString())
        edtClienteCPF.setText(cpf.toString())
        edtClienteBarril.setText(barril.toString())
        edtClienteEndereco.setText(endereco.toString())

        btnVoltarCliente = findViewById(R.id.btnVoltarEdtCliente)
        btnEditarCliente = findViewById(R.id.btnEditarCliente)
        btnDeletarCliente = findViewById(R.id.btnDeletarCliente)

        btnVoltarCliente.setOnClickListener {
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        btnEditarCliente.setOnClickListener {
            atualizarCliente()
        }

        btnDeletarCliente.setOnClickListener {
            excluirCliente()
        }
    }

    private fun validarCampos(): Boolean {
        val nome = edtClienteNome.text.toString().trim()
        val cpf = edtClienteCPF.text.toString().trim()
        val endereco = edtClienteEndereco.text.toString().trim()
        val barril = edtClienteBarril.text.toString().trim()

        if (nome.isEmpty() || cpf.isEmpty() || endereco.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (nome.length > 100) {
            Toast.makeText(this, "Nome deve ter no máximo 100 caracteres.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (barril.length > 100) {
            Toast.makeText(this, "Barril deve ter no máximo 100 caracteres.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (cpf.length != 11) {
            Toast.makeText(this, "CPF deve ter 11 caracteres.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (endereco.length > 100) {
            Toast.makeText(this, "Capacidade deve ter no máximo 4 caracteres.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun atualizarCliente() {
        if (!validarCampos()) {
            return
        }

        val firebaseRepository = FirebaseRepository()

        val id = intent.getStringExtra("id").toString()
        val newNome = edtClienteNome.text.toString()
        val newCPF = edtClienteCPF.text.toString()
        val newBarril = edtClienteBarril.text.toString()
        val newEndereco = edtClienteEndereco.text.toString()

        firebaseRepository.atualizarCliente(id, newNome, newCPF, newBarril, newEndereco) { sucesso ->
            if (sucesso) {
                Toast.makeText(this, "Cliente atualizado com sucesso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erro ao atualizar cliente", Toast.LENGTH_SHORT).show()
            }
        }

        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun excluirCliente() {
        val firebaseRepository = FirebaseRepository()
        val id = intent.getStringExtra("id").toString()

        firebaseRepository.deletarCliente(id) { sucesso ->
            if (sucesso) {
                Toast.makeText(this, "Cliente excluído com sucesso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erro ao excluir cliente", Toast.LENGTH_SHORT).show()
            }
        }

        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}