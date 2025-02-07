package org.example.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

// RecibeId.java(Clase para la clave compuesta)
@Data
@Embeddable
public class RecibeId implements Serializable {
    @Column(name = "id_paciente")
    private int paciente;

    @Column(name = "tratamiento_id")
    private int tratamiento;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;


    @Override
    public int hashCode() {
        return Objects.hash(paciente, tratamiento, fechaInicio);
    }

    @Override
    public boolean equals(Object obj) {
        if (this== obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RecibeId that = (RecibeId) obj;
        return paciente == that.paciente &&
                tratamiento == that.tratamiento &&
                Objects.equals(fechaInicio, that.fechaInicio);
    }

    @Override
    public String toString() {
        return "RecibeId{" +
                "paciente=" + paciente +
                ", tratamiento=" + tratamiento +
                ", fechaInicio=" + fechaInicio +
                '}';
    }
}
