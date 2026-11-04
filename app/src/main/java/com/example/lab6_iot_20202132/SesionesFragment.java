package com.example.lab6_iot_20202132;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab6_iot_20202132.databinding.FragmentSesionesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SesionesFragment extends Fragment implements SesionAdapter.OnSesionListener {

    private FragmentSesionesBinding binding;
    private final List<Sesion> lista = new ArrayList<>();
    private final List<String> ids = new ArrayList<>();
    private SesionAdapter adapter;
    private CollectionReference ref;
    private ListenerRegistration registration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSesionesBinding.inflate(inflater, container, false);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseFirestore.getInstance()
                .collection("usuarios").document(uid).collection("pronosticos");

        adapter = new SesionAdapter(requireContext(), lista, ids, this);
        binding.rvPronosticos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPronosticos.setAdapter(adapter);

        binding.fabAgregar.setOnClickListener(v ->
                startActivity(new Intent(getContext(), FormularioActivity.class)));

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        registration = ref.orderBy("fechaPartido")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    lista.clear();
                    ids.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Sesion p = doc.toObject(Sesion.class);
                        lista.add(p);
                        ids.add(doc.getId());
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    // Buena practica: quitar el listener al pausar (Clase 09.2, pag. 49)
    @Override
    public void onStop() {
        super.onStop();
        if (registration != null) registration.remove();
    }

    @Override
    public void onEditar(int position) {
        Sesion p = lista.get(position);
        Intent intent = new Intent(getContext(), FormularioActivity.class);
        intent.putExtra("modo", "editar");
        intent.putExtra("id", ids.get(position));
        intent.putExtra("curso", p.getcurso());
        intent.putExtra("tipo", p.gettipo());
        intent.putExtra("horas", p.gethoras());
        intent.putExtra("fecha", p.getFechaPartido().toDate().getTime());
        intent.putExtra("estado", p.getEstado());
        startActivity(intent);
    }

    @Override
    public void onEliminar(int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar pronostico")
                .setMessage("Se eliminara el pronostico "
                        + lista.get(position).getcurso() + " vs "
                        + lista.get(position).gettipo() + ". Esta operacion no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) ->
                        ref.document(ids.get(position)).delete()
                                .addOnSuccessListener(unused ->
                                        NotificationHelper.notificar(requireContext(),
                                                "Pronostico eliminado",
                                                "El pronostico fue eliminado correctamente."))
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Error al eliminar",
                                                Toast.LENGTH_SHORT).show()))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}