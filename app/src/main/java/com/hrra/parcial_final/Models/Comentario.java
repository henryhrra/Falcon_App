package com.hrra.parcial_final.Models;

public class Comentario {
    String comentario;
    String idPregunta;
    String idUsuerio;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(String idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getIdUsuerio() {
        return idUsuerio;
    }

    public void setIdUsuerio(String idUsuerio) {
        this.idUsuerio = idUsuerio;
    }

    public Comentario(String comentario, String idPregunta, String idUsuerio) {
        this.comentario = comentario;
        this.idPregunta = idPregunta;
        this.idUsuerio = idUsuerio;
    }

    public Comentario() {

    }
}
