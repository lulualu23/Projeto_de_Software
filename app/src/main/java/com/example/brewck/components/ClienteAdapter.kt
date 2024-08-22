import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brewck.R

data class Cliente(val nome: String, val cpf: String, val barril: String, val endereco: String)

class ClienteAdapter(private val clientes: List<Cliente>, private val clickListener: (Cliente) -> Unit) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeCliente: TextView = itemView.findViewById(R.id.txtClienteNome)
        val cpfCliente: TextView = itemView.findViewById(R.id.txtClienteCPF)
        val barrilCliente: TextView = itemView.findViewById(R.id.txtClienteStatus)
        val enderecoCliente: TextView = itemView.findViewById(R.id.txtClienteEndereco)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cardcliente, parent, false)
        return ClienteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = clientes[position]
        holder.nomeCliente.text = cliente.nome
        holder.cpfCliente.text = formatarCPF(cliente.cpf)
        holder.barrilCliente.text = cliente.barril
        holder.enderecoCliente.text = cliente.endereco

        holder.itemView.setOnClickListener {
            clickListener(cliente)
        }
    }

    override fun getItemCount() = clientes.size

    fun formatarCPF(cpf: String): String {

        return if (cpf.length == 11) {
            cpf.substring(0, 3) + "." +
                    cpf.substring(3, 6) + "." +
                    cpf.substring(6, 9) + "-" +
                    cpf.substring(9)
        } else {
            cpf
        }
    }
}

