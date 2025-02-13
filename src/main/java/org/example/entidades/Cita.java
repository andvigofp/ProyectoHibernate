package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;

import javax.print.Doc;
import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor

@Entity
@Table(name = "Cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //La ID tiene Autoincremental
    private int id;
    @NonNull
    private LocalDate fecha;
    private String estado;

    @ManyToOne //Muchos a uno
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @OneToOne //Uno a Uno
    @JoinColumn(name = "id_doctor")
    private Doctor doctor;


    public Cita(LocalDate fecha, String estado) {
        super();
        this.fecha = fecha;
        this.estado = estado;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fecha, estado);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cita cita = (Cita) o;
        return id == cita.id &&
                Objects.equals(fecha, cita.fecha) &&
                Objects.equals(estado, cita.estado);
    }


        public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        if (paciente != null && !paciente.getCitas().contains(this)) {
            paciente.getCitas().add(this);
        }
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        if (doctor != null && doctor.getCita() != this) {
            doctor.setCita(this);
        }
    }

        @Override
    public String toString() {
        return "Cita{" +
                "fecha=" + fecha +
                ", estado='" + estado + '\'' +
                '}';
    }
}
