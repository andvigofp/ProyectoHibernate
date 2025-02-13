package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder

@Entity
@Table(name = "Doctor")
public class Doctor {

    @Id // ID no es autoincremental
    private int id;
    @NonNull
    private String nombre;
    private String especialidad;
    private String telefono;

    //Uno a Uno
    @OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL, fetch =   FetchType.EAGER)
    private Cita cita;


    public Doctor(int id, @NonNull String nombre, String especialidad, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.telefono = telefono;
    }


    public void setCita(Cita cita) {
        this.cita = cita;
        if (cita != null && cita.getDoctor() != this) {
            cita.setDoctor(this);
        }
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
