package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder

@Entity
@Table(name = "Paciente")
public class Paciente {

    @Id
    private int id; //La ID no es autoincremental
    @NonNull
    private String nombre;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    private String direccion;


    //Uno A Muchos
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Recibe> tratamientos = new ArrayList<>();

    //Uno A Muchos
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Cita> citas = new ArrayList<>();


    public Paciente(int id, @NonNull String nombre, LocalDate fechaNacimiento, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
    }
    
        public void addCita(Cita cita) {
        this.citas.add(cita);
        cita.setPaciente(this);
    }

    public void addRecibe(Recibe recibe) {
        this.tratamientos.add(recibe);
        recibe.setPaciente(this);
    }


    @Override
    public String toString() {
        return "Paciente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", direccion='" + direccion + '\'' +
                '}';
    }
}
