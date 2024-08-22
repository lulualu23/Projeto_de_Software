import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brewck.R

data class Barril(val nome: String, val capacidade: Int, val propriedade: String, val status: String, val liquido: String)

class BarrilAdapter(private val barris: List<Barril>, private val clickListener: (Barril) -> Unit) : RecyclerView.Adapter<BarrilAdapter.BarrilViewHolder>() {

    class BarrilViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeBarril: TextView = itemView.findViewById(R.id.txtNomeBarril)
        val capacidadeBarril: TextView = itemView.findViewById(R.id.txtBarrilCapacidade)
        val propriedadeBarril: TextView = itemView.findViewById(R.id.txtBarrilPropriedade)
        val statusBarril: TextView = itemView.findViewById(R.id.txtBarrilStatus)
        val liquidoBarril: TextView = itemView.findViewById(R.id.txtBarrilLiquido)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarrilViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cardbarril, parent, false)
        return BarrilViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BarrilViewHolder, position: Int) {
        val barril = barris[position]
        holder.nomeBarril.text = barril.nome
        holder.capacidadeBarril.text = barril.capacidade.toString()
        holder.propriedadeBarril.text = barril.propriedade
        holder.statusBarril.text = barril.status
        holder.liquidoBarril.text = barril.liquido

        holder.itemView.setOnClickListener {
            clickListener(barril)
        }
    }

    override fun getItemCount() = barris.size
}

