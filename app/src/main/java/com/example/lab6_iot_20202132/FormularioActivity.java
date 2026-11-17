package com.example.lab6_iot_20202132;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6_iot_20202132.databinding.ActivityFormularioBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FormularioActivity extends AppCompatActivity {

    private static final String[] TIPOS = {"Teoria", "Practica", "Laboratorio"};

    private ActivityFormularioBinding binding;
    private CollectionReference ref;
    private boolean modoEditar = false;
    private String idDocumento;
    private Calendar fechaSeleccionada = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormularioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseFirestore.getInstance()
                .collection("usuarios").document(uid).collection("sesiones");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TIPOS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipo.setAdapter(spinnerAdapter);

        binding.btnFecha.setOnClickListener(v -> mostrarDatePicker());

        if ("editar".equals(getIntent().getStringExtra("modo"))) {
            modoEditar = true;
            idDocumento = getIntent().getStringExtra("id");
            binding.tvTitulo.setText("Editar sesion");
            binding.etCurso.setText(getIntent().getStringExtra("curso"));
            binding.etHoras.setText(String.valueOf(getIntent().getDoubleExtra("horas", 0)));
            fechaSeleccionada.setTimeInMillis(getIntent().getLongExtra("fecha", System.currentTimeMillis()));
            String tipo = getIntent().getStringExtra("tipo");
            for (int i = 0; i < TIPOS.length; i++) {
                if (TIPOS[i].equals(tipo)) {
                    binding.spinnerTipo.setSelection(i);
                    break;
                }
            }
        }
        actualizarBotonFecha();

        binding.btnGuardar.setOnClickListener(v -> guardar());
    }

    private void mostrarDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            fechaSeleccionada.set(year, month, dayOfMonth);
            actualizarBotonFecha();
        }, fechaSeleccionada.get(Calendar.YEAR),
                fechaSeleccionada.get(Calendar.MONTH),
                fechaSeleccionada.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void actualizarBotonFecha() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        binding.btnFecha.setText("Fecha: " + sdf.format(fechaSeleccionada.getTime()));
    }

    private void guardar() {
        String curso = binding.etCurso.getText().toString().trim();
        String sHoras = binding.etHoras.getText().toString().trim();

        if (curso.isEmpty() || sHoras.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double horas;
        try {
            horas = Double.parseDouble(sHoras);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Las horas deben ser un numero", Toast.LENGTH_SHORT).show();
            return;
        }
        if (horas <= 0) {
            Toast.makeText(this, "Las horas deben ser mayores a cero", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipo = binding.spinnerTipo.getSelectedItem().toString();
        Timestamp fecha = new Timestamp(fechaSeleccionada.getTime());
        Sesion sesion = new Sesion(curso, tipo, fecha, horas);

        if (!modoEditar) {
            ref.add(sesion).addOnSuccessListener(doc -> {
                NotificationHelper.notificar(this, "Sesion registrada",
                        curso + " - " + horas + " h");
                finish();
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show());
        } else {
            ref.document(idDocumento).set(sesion).addOnSuccessListener(unused -> {
                NotificationHelper.notificar(this, "Sesion actualizada",
                        curso + " - " + horas + " h");
                finish();
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show());
        }
    }
}