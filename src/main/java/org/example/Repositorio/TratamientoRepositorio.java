package org.example.Repositorio;

import org.example.entidades.*;
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

    // Método para verificar si el paciente existe
    public boolean pacienteExiste(String nombre) {
        System.out.println("Realizando consulta para el nombre: '" + nombre + "'");
        Long count = (Long) session.createQuery("SELECT COUNT(p.id) FROM Paciente p WHERE LOWER(p.nombre) = :nombre")
                .setParameter("nombre", nombre.trim().toLowerCase())
                .uniqueResult();
        boolean existe = count != null && count > 0;
        if (existe) {
            System.out.println("Paciente encontrado: '" + nombre + "'");
        } else {
            System.out.println("No se encontró el paciente con nombre: '" + nombre + "'");
        }
        return existe;
    }

    // Indicar la fecha del fin del tratamiento
    public void indicarFechaFinTratamiento(String nombrePaciente, LocalDate fechaInicio, int idTratamiento, LocalDate fechaFin) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // Verificar si el tratamiento existe
            Tratamiento tratamiento = session.get(Tratamiento.class, idTratamiento);
            if (tratamiento == null) {
                System.out.println("El tratamiento con ID " + idTratamiento + " no existe.");
                tx.rollback();
                return;
            }
            System.out.println("Tratamiento encontrado: " + tratamiento);

            // Verificar si el paciente existe
            if (!pacienteExiste(nombrePaciente)) {
                System.out.println("Paciente no encontrado con el nombre: " + nombrePaciente);
                tx.rollback();
                return; // Salir del método si el paciente no existe
            }

            // Obtener el paciente por nombre solo una vez
            Paciente paciente = (Paciente) session.createQuery("FROM Paciente WHERE LOWER(nombre) = :nombre")
                    .setParameter("nombre", nombrePaciente.trim().toLowerCase())
                    .uniqueResult();
            System.out.println("Paciente encontrado: " + paciente);

            // Verificar la entrada en Recibe con fechas coincidentes
            Recibe recibe = (Recibe) session.createQuery("FROM Recibe WHERE id.idPaciente = :idPaciente AND id.idTratamiento = :idTratamiento AND id.fechaInicio = :fechaInicio", Recibe.class)
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
                // Crear una nueva instancia de Recibe si no existe
                RecibeId recibeId = new RecibeId(paciente.getId(), tratamiento.getId(), fechaInicio);
                Recibe nuevoRecibe = new Recibe(recibeId, paciente, tratamiento, fechaFin);

                // Usar el método addRecibe para añadir el tratamiento al paciente y mantener la relación bidireccional
                paciente.addRecibe(nuevoRecibe);

                // Guardar la relación en la base de datos
                session.save(nuevoRecibe);
                tx.commit();
                System.out.println("Nuevo tratamiento asignado y fecha de fin establecida: " + nuevoRecibe);
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
    public void cambiarHospitalTratamiento(int tratamientoId, String nombreActualHospital, String nombreNuevoHospital) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // Obtener el tratamiento por su ID
            Tratamiento tratamiento = session.get(Tratamiento.class, tratamientoId);

            // Obtener el hospital actual por su nombre
            Hospital hospitalActual = session.createQuery("FROM Hospital WHERE nombre = :nombre", Hospital.class)
                    .setParameter("nombre", nombreActualHospital)
                    .uniqueResult();

            // Obtener el nuevo hospital por su nombre
            Hospital nuevoHospital = session.createQuery("FROM Hospital WHERE nombre = :nombre", Hospital.class)
                    .setParameter("nombre", nombreNuevoHospital)
                    .uniqueResult();

            // Verificar que el tratamiento, el hospital actual y el nuevo hospital existen
            if (tratamiento != null && hospitalActual != null && nuevoHospital != null) {
                // Eliminar el tratamiento de la lista del hospital actual
                hospitalActual.getTratamientos().remove(tratamiento);

                // Añadir el tratamiento al nuevo hospital utilizando el método addTratamiento
                nuevoHospital.addTratamiento(tratamiento);

                session.update(tratamiento);
                tx.commit();
                System.out.println("Hospital del tratamiento cambiado con éxito de " + hospitalActual.getNombre() + " a " + nuevoHospital.getNombre());
            } else {
                System.out.println("No se encontró el tratamiento, el hospital actual o el nuevo hospital.");
                if (tx != null) tx.rollback();
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
    public List<Object[]> contarTratamientosPorHospital() {
        return session.createQuery("SELECT t.hospital.nombre, COUNT(t) FROM Tratamiento t GROUP BY t.hospital.nombre", Object[].class)
                .list();
    }
}