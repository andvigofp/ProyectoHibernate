package org.example.Repositorio;

import java.util.List;

//Repositorio para los métodos
public interface Repositorio <T>{
    void isnertarUno(T t);

    void actualizar(T t);
}
