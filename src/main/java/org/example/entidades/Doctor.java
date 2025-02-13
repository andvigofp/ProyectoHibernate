package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Objects;
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


    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, especialidad, telefono);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return id == doctor.id &&
                Objects.equals(nombre, doctor.nombre) &&
                Objects.equals(especialidad, doctor.especialidad) &&
                Objects.equals(telefono, doctor.telefono);
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
