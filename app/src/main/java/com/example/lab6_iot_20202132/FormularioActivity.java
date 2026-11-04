package com.example.lab6_iot_20202132;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
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
                .collection("usuarios").document(uid).collection("Sesiones");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pendiente", "Acertado", "Fallado"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEstado.setAdapter(spinnerAdapter);

        binding.btnFecha.setOnClickListener(v -> mostrarDatePicker());

        if ("editar".equals(getIntent().getStringExtra("modo"))) {
            modoEditar = true;
            idDocumento = getIntent().getStringExtra("id");
            binding.tvTitulo.setText("Editar sesion");
            binding.spinnerEstado.setVisibility(View.VISIBLE);
            binding.etCurso.setText(getIntent().getStringExtra("Curso"));
            binding.etTipo.setText(getIntent().getStringExtra("Tipo"));
            binding.etHoras.setText(String.valueOf(getIntent().getIntExtra("horas", 0)));
            fechaSeleccionada.setTimeInMillis(getIntent().getLongExtra("fecha", System.currentTimeMillis()));
            String estado = getIntent().getStringExtra("estado");
            binding.spinnerEstado.setSelection(
                    estado.equals("Acertado") ? 1 : estado.equals("Fallado") ? 2 : 0);
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
        binding.btnFecha.setText("Fecha del partido: " + sdf.format(fechaSeleccionada.getTime()));
    }

    private void guardar() {
        String selA = binding.etCurso.getText().toString().trim();
        String selB = binding.etTipo.getText().toString().trim();
        String sHorasA = binding.etHoras.getText().toString().trim();

        if (selA.isEmpty() || selB.isEmpty() || sHorasA.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selA.equalsIgnoreCase(selB)) {
            Toast.makeText(this, "Las selecciones no pueden ser iguales",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int horas;
        try {
            horas = Integer.parseInt(sHorasA);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Los horas deben ser numeros enteros",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (horas < 0) {
            Toast.makeText(this, "Los horas no pueden ser negativos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Timestamp fecha = new Timestamp(fechaSeleccionada.getTime());

        if (!modoEditar) {
            Sesion p = new Sesion(selA, selB, fecha, horas);
            ref.add(p).addOnSuccessListener(doc -> {
                NotificationHelper.notificar(this, "Sesion registrado",
                        selA + " " + horas + " - " + " " + selB);
                finish();
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show());
        } else {
            Sesion p = new Sesion(selA, selB, fecha, horas);
            p.setEstado(binding.spinnerEstado.getSelectedItem().toString());
            ref.document(idDocumento).set(p).addOnSuccessListener(unused -> {
                NotificationHelper.notificar(this, "Sesion actualizado",
                        selA + " vs " + selB + " -> " + p.getEstado());
                finish();
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show());
        }
    }
}