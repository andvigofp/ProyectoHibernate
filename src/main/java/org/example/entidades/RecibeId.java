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
    private int idPaciente;

    @Column(name = "tratamiento_id")
    private int idTratamiento;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    // Constructor
    public RecibeId(int idPaciente, int idTratamiento, LocalDate fechaInicio) {
        this.idPaciente = idPaciente;
        this.idTratamiento = idTratamiento;
        this.fechaInicio = fechaInicio;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPaciente, idTratamiento, fechaInicio);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RecibeId that = (RecibeId) obj;
        return idPaciente == that.idPaciente &&
                idTratamiento == that.idTratamiento &&
                Objects.equals(fechaInicio, that.fechaInicio);
    }

    @Override
    public String toString() {
        return "RecibeId{" +
                "idPaciente=" + idPaciente +
                ", idTratamiento=" + idTratamiento +
                ", fechaInicio=" + fechaInicio +
                '}';
    }
}
