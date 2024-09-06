package com.example.brewck

import FirebaseRepository
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MenuPrincipal : AppCompatActivity() {
    private lateinit var btnBarris : ConstraintLayout
    private lateinit var btnClientes : ConstraintLayout
    private lateinit var btnQR : ConstraintLayout
    private lateinit var btnSair : ConstraintLayout

    private lateinit var txtBarrisLimpos : TextView
    private lateinit var txtBarrisSujos : TextView
    private lateinit var txtBarrisCheios : TextView
    private lateinit var txtBarrisCliente : TextView

    private lateinit var txtUsuario : TextView

    private lateinit var firestore : FirebaseFirestore
    private val repository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()

        txtUsuario = findViewById(R.id.txtUsuario)
        repository.recuperarNomeDoUsuario { nome ->
            nome?.let {
                txtUsuario.text = ("Olá, " + it + ".")
            } ?: run {
                Toast.makeText(this, "Nome não encontrado.", Toast.LENGTH_SHORT).show()
            }
        }

        txtBarrisCheios = findViewById(R.id.txtBarrisCheios)
        txtBarrisSujos = findViewById(R.id.txtBarrisSujos)
        txtBarrisCliente = findViewById(R.id.txtBarrisCliente)
        txtBarrisLimpos = findViewById(R.id.txtBarrisLimpos)

        btnBarris = findViewById(R.id.btnBarris)
        btnClientes = findViewById(R.id.btnClientes)
        btnQR = findViewById(R.id.btnQR)
        btnSair = findViewById(R.id.btnSair)

        btnBarris.setOnClickListener {
            val intent1 = Intent(this, Barris::class.java)
            startActivity(intent1)
        }
        btnClientes.setOnClickListener {
            val intent1 = Intent(this, Clientes::class.java)
            startActivity(intent1)
        }
        btnQR.setOnClickListener {
            val intent1 = Intent(this, QRCode::class.java)
            startActivity(intent1)
        }
        btnSair.setOnClickListener {
            deslogarUsuario()
        }
        txtUsuario.setOnClickListener {
            val intent = Intent(this, Configuracoes::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarContagens()
    }

    private fun atualizarContagens() {
        contarBarrisPorStatus("Cheio") { count ->
            txtBarrisCheios.text = "Barris Cheios: $count"
        }

        contarBarrisPorStatus("Sujo") { count ->
            txtBarrisSujos.text = "Barris Sujos: $count"
        }

        contarBarrisPorStatus("Limpo") { count ->
            txtBarrisLimpos.text = "Barris Limpos: $count"
        }

        contarBarrisPorStatus("No Cliente") { count ->
            txtBarrisCliente.text = "Barris no Cliente: $count"
        }
    }

    private fun contarBarrisPorStatus(status: String, onResult: (Int) -> Unit) {
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email != null) {
            firestore.collection("barris")
                .whereEqualTo("email", email)
                .whereEqualTo("status", status)
                .get()
                .addOnSuccessListener { result ->
                    val count = result.size()
                    onResult(count)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Erro ao contar barris: ${exception.message}", Toast.LENGTH_SHORT).show()
                    onResult(0)
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            onResult(0)
        }
    }

    private fun deslogarUsuario() {
        val auth = FirebaseAuth.getInstance()

        auth.signOut()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
