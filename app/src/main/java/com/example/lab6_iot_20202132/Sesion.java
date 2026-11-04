package com.example.lab6_iot_20202132;

import com.google.firebase.Timestamp;

public class Sesion {

    private String curso;
    private String tipo;
    private Timestamp fechaPartido;
    private int horas;
    private String estado;

    public Sesion() {
    }

    public Sesion(String curso, String tipo, Timestamp fechaPartido,
                  int horas) {
        this.curso = curso;
        this.tipo = tipo;
        this.fechaPartido = fechaPartido;
        this.horas = horas;
        this.estado = "Pendiente";
    }

    public String getcurso() { return curso; }
    public void setcurso(String curso) { this.curso = curso; }

    public String gettipo() { return tipo; }
    public void settipo(String tipo) { this.tipo = tipo; }

    public Timestamp getFechaPartido() { return fechaPartido; }
    public void setFechaPartido(Timestamp fechaPartido) { this.fechaPartido = fechaPartido; }

    public int gethoras() { return horas; }
    public void sethoras(int horas) { this.horas = horas; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}