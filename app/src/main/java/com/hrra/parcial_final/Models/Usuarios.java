package com.hrra.parcial_final.Models;

public class Usuarios {
    String Username;
    String Email;
    //String Password;
    String Id;
    String Imagen;
    int Edad;

    public Usuarios( ) {

    }

    public Usuarios(String username, String email, String id, String imagen, int edad) {
        Username = username;
        Email = email;
        Id = id;
        Edad = edad;
        Imagen = imagen;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }

    public int getEdad() {
        return Edad;
    }

    public void setEdad(int edad) {
        Edad = edad;
    }

}
