package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
@Table(name = "Hospital")
public class Hospital {

    @Id
    private int id;
    @NonNull
    private String nombre;
    private String ubicacion;

    //Uno a muchos
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Tratamiento> tratamientos = new ArrayList<>();

    public Hospital(int id, @NonNull String nombre, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }

    public void addTratamiento(Tratamiento tratamiento) {
        this.tratamientos.add(tratamiento);
        tratamiento.setHospital(this);
    }

    @Override
    public String toString() {
        return "Hospital{" +
                "nombre='" + nombre + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                '}';
    }
}
