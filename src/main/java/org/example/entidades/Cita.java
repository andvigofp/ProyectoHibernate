package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;

import javax.print.Doc;
import java.time.LocalDate;

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

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        paciente.getCitas().add(this);
    }


    public void setDoctor(Doctor doctor) {
        if (this.doctor != null) {
            this.doctor.setCita(null); // Desvincular del doctor anterior
        }
        this.doctor = doctor;
        if (doctor != null) {
            doctor.setCita(this); // Vincular al nuevo doctor
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
