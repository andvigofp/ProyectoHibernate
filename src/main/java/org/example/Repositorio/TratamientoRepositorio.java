package org.example.Repositorio;

import org.example.entidades.Hospital;
import org.example.entidades.Paciente;
import org.example.entidades.Recibe;
import org.example.entidades.Tratamiento;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class TratamientoRepositorio {
    private Session session;

    public TratamientoRepositorio(Session session) {
        this.session = session;
    }

    //Método para comprobar si existe el tipo de tratamiento
    public boolean tipoExiste(String tipo) {
        Long count = (Long) session.createQuery("SELECT COUNT(t) FROM Tratamiento t WHERE t.tipo = :tipo")
                .setParameter("tipo", tipo)
                .uniqueResult();
        return count > 0;
    }

    //Método para comprobar que existe la id de ese tratamiento
    public boolean tratamientoExiste(int idTratamiento) {
        Long count = (Long) session.createQuery("SELECT COUNT(t.id) FROM Tratamiento t WHERE t.id = :id")
                .setParameter("id", idTratamiento)
                .uniqueResult();
        return count != null && count > 0;
    }


    // Indicar la fecha del fin del tratamiento
    public void indicarFechaFinTratamiento(String nombrePaciente, LocalDate fechaInicio, int idTratamiento, LocalDate fechaFin) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // Verificar si el tratamiento existe
            Tratamiento tratamiento = (Tratamiento) session.get(Tratamiento.class, idTratamiento);
            if (tratamiento == null) {
                System.out.println("El tratamiento con ID " + idTratamiento + " no existe.");
                tx.rollback();
                return;
            }

            // Obtener el paciente por nombre
            Paciente paciente = (Paciente) session.createQuery("FROM Paciente WHERE LOWER(nombre) = :nombre")
                    .setParameter("nombre", nombrePaciente.trim().toLowerCase())
                    .uniqueResult();

            if (paciente != null) {
                // Verificar la entrada en Recibe con fechas coincidentes
                Recibe recibe = (Recibe) session.createQuery("FROM Recibe WHERE id.paciente = :idPaciente AND id.tratamiento = :idTratamiento AND id.fechaInicio = :fechaInicio", Recibe.class)
                        .setParameter("idPaciente", paciente.getId())
                        .setParameter("idTratamiento", idTratamiento)
                        .setParameter("fechaInicio", fechaInicio)
                        .uniqueResult();

                if (recibe != null) {
                    // Establecer la fecha de fin del tratamiento
                    recibe.setFechaFin(fechaFin);
                    session.update(recibe);
                    tx.commit();
                    System.out.println("Fecha de fin del tratamiento actualizada con éxito.");
                } else {
                    System.out.println("Tratamiento no encontrado para el paciente: " + nombrePaciente + " con la fecha de inicio " + fechaInicio + ".");
                    tx.rollback();
                }
            } else {
                System.out.println("Paciente no encontrado con el nombre: " + nombrePaciente);
                tx.rollback();
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }



    //Método para comprobar si existe con ese nombre el hospital
    public boolean hospitalExiste(String nombreHospital) {
        Long count = (Long) session.createQuery("SELECT COUNT(h) FROM Hospital h WHERE h.nombre = :nombreHospital")
                .setParameter("nombreHospital", nombreHospital)
                .uniqueResult();
        return count > 0;
    }



    // Método para cambiar el hospital de un tratamiento
    public void cambiarHospitalTratamiento(int tratamientoId, String nombreNuevoHospital) {
        Transaction tx = session.beginTransaction();
        try {
            // Obtener el tratamiento por su ID
            Tratamiento tratamiento = session.get(Tratamiento.class, tratamientoId);

            // Obtener el nuevo hospital por su nombre
            Hospital nuevoHospital = session.createQuery("FROM Hospital WHERE nombre = :nombre", Hospital.class)
                    .setParameter("nombre", nombreNuevoHospital)
                    .uniqueResult();

            // Verificar que el tratamiento y el nuevo hospital existen
            if (tratamiento != null && nuevoHospital != null) {
                tratamiento.setHospital(nuevoHospital);
                session.update(tratamiento);
                tx.commit();
                System.out.println("Hospital del tratamiento cambiado con éxito.");
            } else {
                System.out.println("No se encontró el tratamiento o el nuevo hospital.");
                tx.rollback();
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }


    //Mostrar los datos del taratamiento de un hospital
    public List<Tratamiento> mostrarDatosTratamientosHospital(String nombreHospital) {
        return session.createQuery("FROM Tratamiento t WHERE t.hospital.nombre = :nombreHospital", Tratamiento.class)
                .setParameter("nombreHospital", nombreHospital)
                .list();
    }


    //Mostrar el total de tartamientos de un hospital
    public Long contarTratamientosPorHospital(String nombreHospital) {
        return session.createQuery("SELECT COUNT(t) FROM Tratamiento t WHERE t.hospital.nombre = :nombre", Long.class)
                .setParameter("nombre", nombreHospital)
                .uniqueResult();
    }
}
