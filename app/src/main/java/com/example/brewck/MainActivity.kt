package com.example.brewck

import FirebaseRepository
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var edtUsuario : EditText
    private lateinit var edtSenha : EditText
    private lateinit var btnEntrar : Button
    private lateinit var btnCadastro : TextView
    private lateinit var btnRecuperar : TextView

    private lateinit var auth: FirebaseAuth
    private val repository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, MenuPrincipal::class.java))
            finish()
        }

        edtUsuario = findViewById(R.id.edtUsuario)
        edtSenha = findViewById(R.id.edtSenha)
        btnEntrar = findViewById(R.id.btnEntrar)
        btnEntrar.setOnClickListener { login() }
        btnCadastro = findViewById(R.id.btnCadastro)
        btnCadastro.setOnClickListener {
            val intent2 = Intent(this, Cadastro::class.java)
            startActivity(intent2)
        }
        btnRecuperar = findViewById(R.id.btnRecuperar)
        btnRecuperar.setOnClickListener {
            val intent2 = Intent(this, RecuperarConta::class.java)
            startActivity(intent2)
        }
    }

    private fun login() {
        val email = edtUsuario.text.toString()
        val senha = edtSenha.text.toString()

        repository.fazerLogin(email, senha) { sucesso, mensagem ->
            if (sucesso) {
                println("Login bem-sucedido!")
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                val intent2 = Intent(this, MenuPrincipal::class.java)
                startActivity(intent2)
            } else {
                println("Falha no login: $mensagem")
                Toast.makeText(this, "$mensagem", Toast.LENGTH_LONG).show()
            }
        }
    }
}