package com.example.lab6_iot_20202132;

import com.google.firebase.Timestamp;

public class Sesion {

    private String curso;
    private String tipo;
    private Timestamp fecha;
    private double horas;

    public Sesion() {
    }

    public Sesion(String curso, String tipo, Timestamp fecha, double horas) {
        this.curso = curso;
        this.tipo = tipo;
        this.fecha = fecha;
        this.horas = horas;
    }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public double getHoras() { return horas; }
    public void setHoras(double horas) { this.horas = horas; }
}