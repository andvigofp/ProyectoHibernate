package org.example.Repositorio;


import jakarta.persistence.criteria.CriteriaBuilder;
import org.example.entidades.Cita;
import org.example.entidades.Doctor;
import org.example.entidades.Paciente;
import org.example.entidades.Recibe;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PacienteRepositorio implements Repositorio<Paciente>{
    private Session session;

    public PacienteRepositorio(Session session) {
        this.session = session;
    }


    //Método para generar un unico Id del paciente
    public int generarIdUnicoPaciente() {
        Transaction tx = session.beginTransaction();
        Integer maxId = session.createQuery("SELECT MAX(id) FROM Paciente", Integer.class).uniqueResult();
        tx.commit();
        return (maxId != null ? maxId + 1 : 1);
    }

    //Método para comporbar si el apciente existe
    public boolean pacienteExiste(String nombre) {
        Long count = (Long) session.createQuery("SELECT COUNT(p.id) FROM Paciente p WHERE p.nombre = :nombre")
                .setParameter("nombre", nombre)
                .uniqueResult();
        return count != null && count > 0;
    }


    @Override
    public void isnertarUno(Paciente paciente) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            this.session.persist(paciente);
            tx.commit();
        }catch (Exception e) {
            if (tx !=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public Paciente encontrarUnoPorNombre(String nombre) {
        System.out.println("Realizando consulta para el nombre: '" + nombre + "'");
        Paciente paciente = session.createQuery("FROM Paciente WHERE LOWER(nombre) = :nombre", Paciente.class)
                .setParameter("nombre", nombre.trim().toLowerCase())
                .uniqueResult();
        if (paciente != null) {
            System.out.println("Paciente encontrado: " + paciente);
        } else {
            System.out.println("No se encontró el paciente con nombre: '" + nombre + "'");
        }
        return paciente;
    }

    public List<Paciente> encontrarPorNombre(String nombre) {
        System.out.println("Realizando consulta para el nombre: '" + nombre + "'");
        List<Paciente> pacientes = session.createQuery("FROM Paciente WHERE LOWER(nombre) = :nombre", Paciente.class)
                .setParameter("nombre", nombre.trim().toLowerCase())
                .list();
        if (!pacientes.isEmpty()) {
            System.out.println("Pacientes encontrados: " + pacientes);
        } else {
            System.out.println("No se encontró el paciente con nombre: '" + nombre + "'");
        }
        return pacientes;
    }




    //Método para encontrar por id el paciente
    public Paciente encontrarUnoPorInt(int id) {
        return session.createQuery("FROM Paciente WHERE id = :id", Paciente.class)
                .setParameter("id", id)
                .uniqueResult();
    }



    //Elinar Paciente por su nombre
    public void eliminarPacientePorNombre(String nombre) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // Obtener el paciente por nombre
            Paciente paciente = encontrarUnoPorNombre(nombre);

            if (paciente != null) {
                // Eliminar citas asociadas al paciente
                for (Cita cita : paciente.getCitas()) {
                    // Desvincular la relación con el paciente y el doctor
                    cita.setPaciente(null);
                    if (cita.getDoctor() != null) {
                        Doctor doctor = cita.getDoctor();
                        cita.setDoctor(null); // Desvincular la cita del doctor
                        doctor.setCita(null); // Desvincular el doctor de la cita
                        session.update(doctor); // Actualizar el doctor para desvincular correctamente
                    }
                    session.update(cita); // Actualizar la cita para desvincular correctamente
                    session.remove(cita); // Eliminar la cita
                }

                // Eliminar entradas en Recibe asociadas al paciente
                for (Recibe recibe : paciente.getTratamientos()) {
                    // Desvincular la relación con el paciente y el tratamiento
                    recibe.setPaciente(null);
                    recibe.setTratamiento(null);
                    session.update(recibe); // Actualizar para desvincular correctamente
                    session.remove(recibe); // Eliminar la entrada de Recibe
                }

                // Finalmente, eliminar el paciente
                session.remove(paciente);
                session.flush(); // Asegurarse de que la eliminación se procese inmediatamente
                System.out.println("Paciente y sus relaciones eliminados con éxito.");
            } else {
                System.out.println("Paciente no encontrado.");
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }


    //Método para actualizar los datos del paciente
    @Override
    public void actualizar(Paciente paciente) {
        Transaction trx = null;
        try {
            trx = session.beginTransaction();
            session.update(paciente);  // Actualiza el paciente en la base de datos
            trx.commit();
            System.out.println("Paciente actualizado correctamente.");
        } catch (Exception e) {
            if (trx != null) trx.rollback();
            e.printStackTrace();
        }
    }

    // Método para obtener los datos de un paciente, sus tratamientos y citas
    public Paciente obtenerDatosPaciente(String nombrePaciente) {
        Transaction tx = null;
        Paciente paciente = null;
        try {
            tx = session.beginTransaction();
            // Obtener el paciente por nombre
            paciente = (Paciente) session.createQuery("FROM Paciente WHERE nombre = :nombre")
                    .setParameter("nombre", nombrePaciente)
                    .uniqueResult();

            if (paciente != null) {
                // Inicializar las colecciones de tratamientos y citas
                paciente.getTratamientos().size(); // Cargar tratamientos
                paciente.getCitas().size(); // Cargar citas
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return paciente;
    }
}
