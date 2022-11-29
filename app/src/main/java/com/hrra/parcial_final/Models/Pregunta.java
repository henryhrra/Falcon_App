package com.hrra.parcial_final.Models;

public class Pregunta {
    String Fecha;
    String Pregunta;
    int Like;
    String Categoria;
    String Id_User;
    String Descripcion;

    public Pregunta(String fecha, String pregunta, int like, String categoria, String id_User, String descripcion) {
        Fecha = fecha;
        Pregunta = pregunta;
        Like = like;
        Categoria = categoria;
        Id_User = id_User;
        Descripcion = descripcion;
    }
    public Pregunta() {

    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getPregunta() {
        return Pregunta;
    }

    public void setPregunta(String pregunta) {
        Pregunta = pregunta;
    }

    public int getLike() {
        return Like;
    }

    public void setLike(int like) {
        Like = like;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public String getId_User() {
        return Id_User;
    }

    public void setId_User(String id_User) {
        Id_User = id_User;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }
}
