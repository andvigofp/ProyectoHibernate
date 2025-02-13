package org.example.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

@Entity
@Table(name = "Recibe")
public class Recibe {

    @EmbeddedId
    private RecibeId id; // Clave primaria compuesta

    @ManyToOne() //Muchos a Uno
    @MapsId("idPaciente")
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @ManyToOne //Muchos a Uno
    @MapsId("idTratamiento")
    @JoinColumn(name = "tratamiento_id")
    private Tratamiento tratamiento;


    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    public LocalDate getFechaIncio() {
        return id.getFechaInicio();
    }

    

    @Override
    public String toString() {
        return "Recibe{" +
                "id=" + id +
                ", fechaFin=" + fechaFin +
                '}';


    }
}
