package org.example.Repositorio;

import org.example.entidades.Cita;
import org.example.entidades.Doctor;
import org.example.entidades.Paciente;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class DoctorRepositorio implements Repositorio<Doctor> {
    private Session session;

    public DoctorRepositorio(Session session) {
        this.session = session;
    }

    //Método para generar un unico Id del doctor
    public int generarIdUncioDctor() {
        Transaction tx = session.beginTransaction();
        Integer maxId = session.createQuery("SELECT MAX(id) FROM Doctor", Integer.class).uniqueResult();
        tx.commit();
        return (maxId != null ? maxId + 1 : 1);
    }

    //Método para comprobar si eñ doctor ya existe
    public boolean doctorExiste(String nombre) {
        Long count = (Long) session.createQuery("SELECT COUNT(d.id) FROM Doctor d WHERE d.nombre = :nombre")
                .setParameter("nombre", nombre)
                .uniqueResult();
        return count != null && count > 0;
    }

    //Método para insertar un doctor
    @Override
    public void isnertarUno(Doctor doctor) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(doctor);
            System.out.println("Inserto con éxito");
            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }


    //Encontrar por uno un doctor por su id
    public Doctor encontrarUnoPorInt(int id) {
        return session.createQuery("FROM Doctor WHERE id = :id", Doctor.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    //Método para borrar un doctor por ID
    public void borrarPorID(int id) {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // Obtener el doctor
            Doctor doctor = session.get(Doctor.class, id);
            if (doctor != null) {
                // Verificar si tiene una cita asociada
                if (doctor.getCita() != null) {
                    Cita cita = doctor.getCita();

                    // Desvincular todas las relaciones en cascada
                    doctor.setCita(null);
                    cita.setDoctor(null);
                    session.update(cita); // Actualizar la cita para desvincular correctamente

                    // Verificar si el paciente tiene otras citas que requieran desvinculación
                    if (cita.getPaciente() != null) {
                        cita.getPaciente().getCitas().remove(cita);
                        session.update(cita.getPaciente());
                    }

                    // Ahora eliminar la cita
                    session.remove(cita);
                }

                // Asegurarse de que el doctor no esté asociado con ninguna otra entidad
                session.flush();

                // Eliminar el doctor
                session.remove(doctor);
                System.out.println("Doctor y su cita eliminados con éxito.");
            } else {
                System.out.println("Doctor no encontrado.");
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }


    //Método para encontrar un Doctor por su nombre
    public Doctor encontrarUnoPorNombre(String nombre) {
        System.out.println("Realizando consulta para el nombre: '" + nombre + "'");
        Doctor doctor = session.createQuery("FROM Doctor WHERE LOWER(nombre) = :nombre", Doctor.class)
                .setParameter("nombre", nombre.trim().toLowerCase())
                .uniqueResult();
        if (doctor != null) {
            System.out.println("Doctor encontrado: " + doctor);
        } else {
            System.out.println("No se encontró el doctor con nombre: '" + nombre + "'");
        }
        return doctor;
    }

    public void modificar(int id) {
        Scanner scanner = new Scanner(System.in);
        Transaction trx = null;
        try {
            trx = session.beginTransaction();

            // Crear el query para verificar si el doctor existe
            Query query = session.createQuery("SELECT d FROM Doctor d WHERE d.id = :id");
            query.setParameter("id", id);
            Doctor doctorExistente = (Doctor) query.uniqueResult();

            if (doctorExistente != null) {
                System.out.println("Inserta los datos para reemplazar");
                System.out.println("Dime el nombre del doctor");
                String nombre = scanner.nextLine();
                System.out.println("Dime su especialidad");
                String especialidad = scanner.nextLine();
                System.out.println("Ahora dime su telefono");
                String telefono = scanner.nextLine();

                // Actualizar los detalles del doctor
                doctorExistente.setNombre(nombre);
                doctorExistente.setEspecialidad(especialidad);
                doctorExistente.setTelefono(telefono);

                // Guardar el doctor actualizado
                session.update(doctorExistente);
                trx.commit();
                System.out.println("Se modificó el doctor satisfactoriamente");
            } else {
                System.out.println("No se encontró nadie con esa id");
            }
        } catch (Exception e) {
            if (trx != null) trx.rollback();
            e.printStackTrace();
        }
    }


    //Método para actualizar los datos del doctor
    @Override
    public void actualizar(Doctor doctor) {
        Transaction trx = null;
        try {
            trx = session.beginTransaction();

            // Buscar si el doctor existe
            Doctor doctorExistente = session.get(Doctor.class, doctor.getId());
            if (doctorExistente != null) {
                // Actualizar los datos
                doctorExistente.setNombre(doctor.getNombre());
                doctorExistente.setEspecialidad(doctor.getEspecialidad());
                doctorExistente.setTelefono(doctor.getTelefono());

                session.update(doctorExistente); // Hibernate detecta los cambios automáticamente
                trx.commit();
                System.out.println("Doctor actualizado correctamente.");
            } else {
                System.out.println("Doctor con ID " + doctor.getId() + " no encontrado.");
                trx.rollback();
            }
        } catch (Exception e) {
            if (trx != null) trx.rollback();
            e.printStackTrace();
        }
    }


    //Método para asignar un doctor a un paciente
    public boolean asignarDoctorAPaciente(String nombreDoctor, String nombrePaciente, LocalDate fecha, String estado) {
        Transaction tx = null;
        boolean exito = false;
        try {
            tx = session.beginTransaction();

            // Obtener el doctor por nombre
            Doctor doctor = (Doctor) session.createQuery("FROM Doctor WHERE nombre = :nombre", Doctor.class)
                    .setParameter("nombre", nombreDoctor)
                    .uniqueResult();

            if (doctor == null) {
                System.out.println("El doctor no existe: '" + nombreDoctor + "'");
                return false;
            }
            System.out.println("Doctor encontrado: " + doctor);

            // Obtener la lista de pacientes por nombre
            List<Paciente> pacientes = (List<Paciente>) session.createQuery("FROM Paciente WHERE nombre = :nombre", Paciente.class)
                    .setParameter("nombre", nombrePaciente)
                    .list();

            if (pacientes.isEmpty()) {
                System.out.println("No se encontraron pacientes con el nombre: '" + nombrePaciente + "'");
                return false;
            }
            System.out.println("Pacientes encontrados: " + pacientes);

            // Selecciona el primer paciente de la lista (puedes ajustar esta lógica según sea necesario)
            Paciente paciente = pacientes.get(0);
            System.out.println("Paciente seleccionado: " + paciente);

            // Verificar si ya existe una cita entre el doctor y el paciente
            Cita citaExistente = (Cita) session.createQuery("FROM Cita WHERE doctor.id = :doctorId AND paciente.id = :pacienteId", Cita.class)
                    .setParameter("doctorId", doctor.getId())
                    .setParameter("pacienteId", paciente.getId())
                    .uniqueResult();

            if (citaExistente != null) {
                System.out.println("El paciente ya tiene una cita asignada con este doctor.");
            } else {
                // Verificar si el doctor ya tiene alguna cita
                Long citasDelDoctor = (Long) session.createQuery("SELECT COUNT(c.id) FROM Cita c WHERE c.doctor.id = :doctorId")
                        .setParameter("doctorId", doctor.getId())
                        .uniqueResult();

                if (citasDelDoctor != null && citasDelDoctor > 0) {
                    System.out.println("El doctor ya tiene una cita asignada con otro paciente y no puede tener múltiples citas.");
                } else {
                    // Crear una nueva cita
                    Cita nuevaCita = new Cita(fecha, estado);
                    nuevaCita.setDoctor(doctor);

                    // Usar el método addCita para añadir la cita al paciente y mantener la relación bidireccional
                    paciente.addCita(nuevaCita);

                    // Guardar la cita en la base de datos
                    session.save(nuevaCita);
                    System.out.println("Nueva cita asignada con éxito: " + nuevaCita);
                    exito = true; // Indicar que la asignación fue exitosa
                }
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return exito;
    }
}