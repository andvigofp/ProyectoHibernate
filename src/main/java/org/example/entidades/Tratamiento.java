package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@SuperBuilder

@Entity
@Table(name = "Tratamiento")
public class Tratamiento {

    @Id
    private int id;
    @NonNull
    private String tipo;

    @Column(precision = 10, scale = 2) // Decimal(10, 2)
    private BigDecimal costo;

    @ManyToOne //Muchos a uno
    @JoinColumn(name = "id_hospital")
    private Hospital hospital;

    @OneToMany(mappedBy = "tratamiento")
    private List<Recibe> listaCitas;


    public Tratamiento(int id, @NonNull String tipo, BigDecimal costo) {
        this.id = id;
        this.tipo = tipo;
        this.costo = costo;
    }


    @Override
    public String toString() {
        return "Tratamiento{" +
                "costo=" + costo +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
