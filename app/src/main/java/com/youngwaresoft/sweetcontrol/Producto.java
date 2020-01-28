package com.youngwaresoft.sweetcontrol;

public class Producto {
    private String nombre;
    private double precio;
    private String desc;
    private String codigo;

    public Producto() {
        this.nombre="null";
        this.precio=0;
        this.desc="null";
        this.codigo="null";
    }

    public Producto(String nombre) {
        this.nombre = nombre;
        this.precio=0;
        this.desc="null";
        this.codigo="null";
    }

    public Producto(String nombre, double precio, String desc, String codigo) {
        this.nombre = nombre;
        this.precio = precio;
        this.desc = desc;
        this.codigo = codigo;
    }




    //Getters and setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
