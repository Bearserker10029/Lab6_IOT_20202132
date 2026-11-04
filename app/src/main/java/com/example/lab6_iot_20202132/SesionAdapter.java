package com.example.lab6_iot_20202132;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6_iot_20202132.databinding.ItemSesionBinding;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.SesionViewHolder> {

    private final Context context;
    private final List<Sesion> lista;
    private final List<String> ids;
    private final OnSesionListener listener;

    public interface OnSesionListener {
        void onEditar(int position);
        void onEliminar(int position);
    }

    public SesionAdapter(Context context, List<Sesion> lista, List<String> ids,
                         OnSesionListener listener) {
        this.context = context;
        this.lista = lista;
        this.ids = ids;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SesionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSesionBinding binding = ItemSesionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SesionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SesionViewHolder holder, int position) {
        Sesion p = lista.get(position);

        holder.binding.tvSelecciones.setText(p.getcurso());
        holder.binding.tvMarcador.setText(p.gethoras());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.binding.tvFecha.setText("Fecha: " + sdf.format(p.getFechaPartido().toDate()));

        holder.binding.tvEstado.setText(p.getEstado());
        switch (p.getEstado()) {
            case "Acertado": holder.binding.tvEstado.setTextColor(Color.parseColor("#2E7D32")); break;
            case "Fallado":  holder.binding.tvEstado.setTextColor(Color.parseColor("#C62828")); break;
            default:         holder.binding.tvEstado.setTextColor(Color.parseColor("#F9A825")); break;
        }

        holder.binding.cardSesion.setOnClickListener(v -> {
            if ("Pendiente".equals(p.getEstado())) {
                listener.onEditar(position);
            } else {
                Toast.makeText(context, "El Sesion esta " + p.getEstado()
                        + " y ya no se puede editar", Toast.LENGTH_SHORT).show();
            }
        });

        holder.binding.cardSesion.setOnLongClickListener(v -> {
            if ("Pendiente".equals(p.getEstado())) {
                listener.onEliminar(position);
            } else {
                Toast.makeText(context, "Solo se pueden eliminar Sesions pendientes",
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class SesionViewHolder extends RecyclerView.ViewHolder {
        ItemSesionBinding binding;
        SesionViewHolder(ItemSesionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}