package com.example.lab6_iot_20202132;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab6_iot_20202132.databinding.FragmentEstadisticasBinding;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EstadisticasFragment extends Fragment {

    private static final String TODOS = "Todos los meses";

    private FragmentEstadisticasBinding binding;
    private ListenerRegistration registration;
    private final List<Sesion> sesiones = new ArrayList<>();
    private final List<String> meses = new ArrayList<>();
    private String mesFiltro = TODOS;
    private boolean spinnerInicializado = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstadisticasBinding.inflate(inflater, container, false);

        binding.barChart.getDescription().setEnabled(false);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setHoleRadius(45f);
        binding.pieChart.setEntryLabelColor(Color.BLACK);
        binding.pieChart.setCenterTextSize(16f);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection("usuarios").document(uid).collection("sesiones");

        SimpleDateFormat formatoMes = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

        registration = ref.addSnapshotListener((snapshots, e) -> {
            if (e != null || snapshots == null) return;

            sesiones.clear();
            meses.clear();
            meses.add(TODOS);
            for (QueryDocumentSnapshot doc : snapshots) {
                Sesion s = doc.toObject(Sesion.class);
                sesiones.add(s);
                String mes = formatoMes.format(s.getFecha().toDate());
                if (!meses.contains(mes)) {
                    meses.add(mes);
                }
            }

            if (!spinnerInicializado) {
                spinnerInicializado = true;
                ArrayAdapter<String> adapterMeses = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, meses);
                adapterMeses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerMes.setAdapter(adapterMeses);
                binding.spinnerMes.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view,
                                               int position, long id) {
                        mesFiltro = meses.get(position);
                        dibujarGraficos();
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    }
                });
            } else {
                ((ArrayAdapter<?>) binding.spinnerMes.getAdapter()).notifyDataSetChanged();
            }

            dibujarGraficos();
        });
    }

    private void dibujarGraficos() {
        SimpleDateFormat formatoMes = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

        double totalHoras = 0;
        Map<String, Double> porMes = new LinkedHashMap<>();
        Map<String, Double> porTipo = new LinkedHashMap<>();
        porTipo.put("Teoria", 0.0);
        porTipo.put("Practica", 0.0);
        porTipo.put("Laboratorio", 0.0);

        for (Sesion s : sesiones) {
            String mes = formatoMes.format(s.getFecha().toDate());
            if (!TODOS.equals(mesFiltro) && !mes.equals(mesFiltro)) {
                continue;
            }
            totalHoras += s.getHoras();
            porMes.merge(mes, s.getHoras(), Double::sum);
            porTipo.merge(s.getTipo(), s.getHoras(), Double::sum);
        }

        binding.tvTotal.setText("Total de horas: " + totalHoras);

        List<BarEntry> barEntries = new ArrayList<>();
        List<String> etiquetasMes = new ArrayList<>(porMes.keySet());
        for (int i = 0; i < etiquetasMes.size(); i++) {
            barEntries.add(new BarEntry(i, porMes.get(etiquetasMes.get(i)).floatValue()));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "Horas por mes");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextSize(12f);
        binding.barChart.getXAxis().setValueFormatter(
                new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(etiquetasMes));
        binding.barChart.getXAxis().setGranularity(1f);
        binding.barChart.setData(new BarData(barDataSet));
        binding.barChart.invalidate();

        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : porTipo.entrySet()) {
            if (entry.getValue() > 0) {
                pieEntries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            }
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(14f);
        binding.pieChart.setData(new PieData(pieDataSet));
        binding.pieChart.setCenterText(totalHoras + "\nhoras");
        binding.pieChart.invalidate();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null) registration.remove();
    }
}