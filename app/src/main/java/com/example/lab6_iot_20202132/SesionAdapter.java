package com.example.lab6_iot_20202132;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
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
        Sesion s = lista.get(position);

        holder.binding.tvSelecciones.setText(s.getCurso());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.binding.tvFecha.setText(sdf.format(s.getFecha().toDate())
                + " \u00b7 " + formatearHoras(s.getHoras()) + " h");

        holder.binding.tvEstado.setText(s.getTipo());
        switch (s.getTipo()) {
            case "Teoria":
                holder.binding.tvEstado.setTextColor(Color.parseColor("#1565C0"));
                break;
            case "Practica":
                holder.binding.tvEstado.setTextColor(Color.parseColor("#EF6C00"));
                break;
            default:
                holder.binding.tvEstado.setTextColor(Color.parseColor("#2E7D32"));
                break;
        }

        holder.binding.btnEditar.setOnClickListener(v -> listener.onEditar(position));
        holder.binding.btnEliminar.setOnClickListener(v -> listener.onEliminar(position));
    }

    private String formatearHoras(double horas) {
        if (horas == Math.floor(horas)) {
            return String.valueOf((int) horas);
        }
        return String.valueOf(horas);
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