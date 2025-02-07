package org.example;

import org.example.Repositorio.DoctorRepositorio;
import org.example.Repositorio.PacienteRepositorio;
import org.example.Repositorio.TratamientoRepositorio;
import org.example.entidades.*;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */

public class App {
    static Session session;
    static Scanner teclado;
    static DoctorRepositorio doctorRepo;
    static PacienteRepositorio pacienteRepo;
    static TratamientoRepositorio tratamientoRepo;

    public static void main(String[] args) {
        System.out.println("301");

        session = HibernateUtil.get().openSession();

        doctorRepo = new DoctorRepositorio(session);
        pacienteRepo = new PacienteRepositorio(session);
        tratamientoRepo = new TratamientoRepositorio(session);

        teclado = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("Seleccione una opción:");
            System.out.println("1. Crear Doctor");
            System.out.println("2. Borrar Doctor por ID");
            System.out.println("3. Modificar Doctor");
            System.out.println("4. Crear Paciente");
            System.out.println("5. Borrar Paciente por Nombre");
            System.out.println("6. Modificar Paciente");
            System.out.println("7. Asignar Doctor a Paciente");
            System.out.println("8. Indicar Fecha de Fin del Tratamiento de un Paciente");
            System.out.println("9. Cambiar Hospital de un Tratamiento");
            System.out.println("10. Mostrar Datos de un Paciente");
            System.out.println("11. Mostrar Datos de los Tratamientos y el Hospital");
            System.out.println("12. Mostrar Número Total de Tratamientos por Hospital");
            System.out.println("0. Salir");

            opcion = teclado.nextInt();
            teclado.nextLine(); // Consumir nueva línea

            switch (opcion) {
                case 1:
                    //Crear Doctor
                    crearDoctor();
                    break;
                case 2:
                    //Borrar Doctor por ID
                    borrarDoctorPorId();
                    break;
                case 3:
                    //Modificar datos del Doctor
                    modificarDoctor();
                    break;
                case 4:
                    //Crear Paciente
                    crearPaciente();
                    break;
                case 5:
                    //Borrar Paciente por Nombre
                    borrarPacientePorNombre();
                    break;
                case 6:
                    //Modificar Datos del paciente
                    modificarPaciente();
                    break;
                case 7:
                    //La asignación se hará a partir del nombre del doctor y del paciente.
                    //Se pedirá por teclado introducir el nombre del doctor y del paciente
                    asignarDoctorAPaciente();
                    break;
                case 8:
                    //El método recibirá el nombre del paciente, la fecha de inicio, el tipo y la fecha de fin del tratamiento.
                    indicarFechaFinTratamiento();
                    break;
                case 9:
                    //El método recibirá el id del tratamiento,
                    // el nombre del hospital en donde está ahora el tratamiento y el nombre del hospital
                    // en dónde se va a realizar el tratamiento a partir de ahora.
                    cambiarHospitalTratamiento();
                    break;
                case 10:
                    //La consulta se hará a partir del nombre del Paciente que introduzca el usuario.
                    mostrarDatosPaciente();
                    break;
                case 11:
                    //La consulta se hará a partir del nombre del hospital que introduzca el usuario.
                    mostrarDatosTratamientosHospital();
                    break;
                case 12:
                    //La consulta se hará a partir del nombre del hospital que introduzca el usuario.
                    mostrarNumeroTratamientosHospital();
                    break;
                case 0:
                    System.out.println("Fin del programa");
                    break;
                default:
                    System.out.println("Opción no válida, por favor intente de nuevo.");
                    break;
            }
        }

        session.close();
        System.out.println("Finalizando la conexion a MySQL");
    }

    //Método para pedir por teclado tipo Int
    public static int pintarPedirInt(String mensaje) {
        int entrada;
        while (true) {
            try {
                System.out.println(mensaje);
                entrada = Integer.parseInt(teclado.nextLine().trim());
                return entrada;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese solo números.");
            }
        }
    }


    //Método para pedir por teclado tipo String
    public static String pintarPedirString(String mensaje) {
        System.out.println(mensaje);
        return teclado.nextLine();
    }


    //Método para crear un doctor
    private static void crearDoctor() {
        try {
            String nombre;

            // Validar que el nombre solo contenga letras, puntos, espacios y tildes
            while (true) {
                nombre = pintarPedirString("Introduzca el nombre del doctor:");
                if (nombre != null && !nombre.trim().isEmpty() && nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚ. ]+")) {
                    // Verificar si el doctor ya existe con el mismo nombre
                    boolean doctorExiste = doctorRepo.doctorExiste(nombre);
                    if (doctorExiste) {
                        System.out.println("Ya existe un doctor con el nombre '" + nombre + "'. Intente con un nombre diferente.");
                        nombre = null;
                    } else {
                        break;
                    }
                } else {
                    System.out.println("El nombre del doctor solo debe contener letras, puntos, espacios, tildes. Intente nuevamente.");
                }
            }

            String especialidad;
            // Validar que la especialidad solo contenga letras, puntos, espacios y tildes
            while (true) {
                especialidad = pintarPedirString("Introduzca la especialidad del doctor:");
                if (especialidad != null && !especialidad.trim().isEmpty() && especialidad.matches("[a-zA-ZáéíóúÁÉÍÓÚ. ]+")) {
                    break;
                } else {
                    System.out.println("La especialidad solo debe contener letras, puntos, espacios y tildes. Intente nuevamente.");
                }
            }

            String telefono;
            // Validar que el teléfono solo contenga números
            while (true) {
                try {
                    telefono = pintarPedirString("Introduzca el teléfono del doctor:");
                    if (telefono.matches("[0-9]+")) {
                        break;
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("El teléfono solo debe contener números. Intente nuevamente.");
                }
            }

            // Generar ID manualmente
            int id = doctorRepo.generarIdUncioDctor();

            Doctor doctor = new Doctor(id, nombre, especialidad, telefono);
            doctorRepo.isnertarUno(doctor);
            System.out.println("Doctor creado con éxito: " + doctor);

        } catch (Exception e) {
            System.out.println("Ocurrió un error durante la creación del doctor. Por favor, intente nuevamente.");
            e.printStackTrace();
        }
    }


    //Método para borrar el doctor por id
    private static void borrarDoctorPorId() {
        while (true) {
            int id = pintarPedirInt("Introduzca el ID del doctor a borrar:");

            try {
                Doctor doctor = doctorRepo.encontrarUnoPorInt(id);

                if (doctor != null) {
                    doctorRepo.borrarPorID(id);
                    System.out.println("Doctor borrado con éxito: " + doctor);
                    break; // Salir del bucle después de borrar con éxito
                } else {
                    System.out.println("Doctor no encontrado: " + id + ". Intente nuevamente.");
                }
            } catch (Exception e) {
                System.out.println("Ocurrió un error durante el proceso de borrado del doctor. Por favor, intente nuevamente.");
                e.printStackTrace();
            }
        }
    }


    //Método para modificar datos del doctor
    private static void modificarDoctor() {
        try {
            while (true) {
                int id = pintarPedirInt("Ingrese el ID del doctor (0 para salir):");
                if (id == 0) {
                    System.out.println("Saliendo...");
                    return;
                }

                // Buscar si el doctor existe
                Doctor doctorExistente = doctorRepo.encontrarUnoPorInt(id);
                if (doctorExistente != null) {
                    String nuevoNombre;
                    while (true) {
                        nuevoNombre = pintarPedirString("Ingrese el nuevo nombre:");
                        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty() && nuevoNombre.matches("[a-zA-ZáéíóúÁÉÍÓÚ. ]+")) {
                            break;
                        } else {
                            System.out.println("El nombre solo debe contener letras, puntos, espacios y tildes. Intente nuevamente.");
                        }
                    }

                    String nuevaEspecialidad;
                    while (true) {
                        nuevaEspecialidad = pintarPedirString("Ingrese la nueva especialidad:");
                        if (nuevaEspecialidad != null && !nuevaEspecialidad.trim().isEmpty() && nuevaEspecialidad.matches("[a-zA-ZáéíóúÁÉÍÓÚ.]+")) {
                            break;
                        } else {
                            System.out.println("La especialidad solo debe contener letras, puntos, espacios y tildes. Intente nuevamente.");
                        }
                    }

                    String nuevoTelefono;
                    while (true) {
                        nuevoTelefono = pintarPedirString("Ingrese el nuevo teléfono:");
                        if (nuevoTelefono.matches("[0-9]+")) {
                            break;
                        } else {
                            System.out.println("El teléfono solo debe contener números. Intente nuevamente.");
                        }
                    }

                    // Crear el objeto actualizado
                    Doctor doctorActualizado = new Doctor(id, nuevoNombre, nuevaEspecialidad, nuevoTelefono);

                    // Llamar al método de actualización
                    doctorRepo.actualizar(doctorActualizado);
                    System.out.println("Doctor actualizado con éxito: " + doctorActualizado);
                    break; // Salir del bucle después de actualizar
                } else {
                    System.out.println("Doctor con ID " + id + " no encontrado. Intente nuevamente.");
                }
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al modificar del doctor. Por favor, intente nuevamente.");
            e.printStackTrace();
        }
    }

    //Método para crear el paciente
    private static void crearPaciente() {
        try {
            String nombre = null;
            while (true) {
                nombre = pintarPedirString("Introduzca el nombre del paciente:");
                if (nombre != null && !nombre.trim().isEmpty() && nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                    // Verificar si el paciente ya existe con el mismo nombre
                    boolean pacienteExiste = pacienteRepo.pacienteExiste(nombre);
                    if (pacienteExiste) {
                        System.out.println("Ya existe un paciente con el nombre '" + nombre + "'. Intente con un nombre diferente.");
                        nombre = null;
                    } else {
                        break;
                    }
                } else {
                    System.out.println("El nombre solo debe contener letras y tildes. Intente nuevamente.");
                }
            }

            String direccion = null;
            while (direccion == null || direccion.trim().isEmpty()) {
                direccion = pintarPedirString("Introduzca la dirección del paciente:");
                if (direccion == null || direccion.trim().isEmpty()) {
                    System.out.println("La dirección no puede estar vacía.");
                }
            }

            LocalDate fechaNacimiento = null;
            while (fechaNacimiento == null) {
                try {
                    String fechaNacimientoStr = pintarPedirString("Introduzca la fecha de nacimiento del paciente (YYYY-MM-DD):");
                    fechaNacimiento = LocalDate.parse(fechaNacimientoStr);
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de fecha incorrecto. Por favor, ingrese la fecha en el formato YYYY-MM-DD.");
                }
            }

            // Generar ID manualmente
            int id = pacienteRepo.generarIdUnicoPaciente();

            Paciente paciente = new Paciente(id, nombre, fechaNacimiento, direccion);
            pacienteRepo.isnertarUno(paciente);

            System.out.println("Paciente creado con éxito: " + paciente);
        } catch (Exception e) {
            System.out.println("Ocurrió un error durante la creación del paciente. Por favor, intente nuevamente.");
            e.printStackTrace();
        }
    }


    //Método para borrar un paciente por el nombre
    private static void borrarPacientePorNombre() {
        try {
            while (true) {
                String nombre = null;
                while (nombre == null || nombre.trim().isEmpty() || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                    nombre = pintarPedirString("Ingrese el nombre del paciente a eliminar:");
                    if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                        System.out.println("El nombre solo puede contener letras y tildes. Intente nuevamente.");
                    }
                }

                // Verificar si el paciente existe
                Paciente paciente = pacienteRepo.encontrarUnoPorNombre(nombre);
                if (paciente != null) {
                    // Llamar al método para borrar
                    pacienteRepo.eliminarPacientePorNombre(nombre);
                    System.out.println("Paciente eliminado con éxito: " + nombre);
                    break; // Salir del bucle después de eliminar el paciente
                } else {
                    System.out.println("Paciente no encontrado: " + nombre + ". Intente nuevamente.");
                }
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al borrar del paciente. Por favor, intente nuevamente.");
            e.printStackTrace();
        }
    }


    //Método para modificar el paciente por su nombre
    private static void modificarPaciente() {
        try {
            while (true) {
                int idPaciente = pintarPedirInt("Ingrese el ID del paciente a actualizar (0 para salir):");

                if (idPaciente == 0) {
                    System.out.println("Saliendo...");
                    return;
                }

                // Buscar el paciente en la BD
                Paciente paciente = pacienteRepo.encontrarUnoPorInt(idPaciente);

                if (paciente != null) {
                    String nuevoNombre = null;
                    while (nuevoNombre == null || nuevoNombre.trim().isEmpty() || !nuevoNombre.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                        nuevoNombre = pintarPedirString("Ingrese su nuevo nombre:");
                        if (nuevoNombre == null || nuevoNombre.trim().isEmpty() || !nuevoNombre.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                            System.out.println("El nombre solo puede contener letras y tildes. Intente nuevamente.");
                        }
                    }

                    String nuevaDireccion = null;
                    while (nuevaDireccion == null || nuevaDireccion.trim().isEmpty()) {
                        nuevaDireccion = pintarPedirString("Ingrese la nueva dirección:");
                        if (nuevaDireccion == null || nuevaDireccion.trim().isEmpty()) {
                            System.out.println("La  dirección no puede estar vacío.");
                        }
                    }

                    LocalDate nuevaFechaNacimiento = null;
                    while (nuevaFechaNacimiento == null) {
                        try {
                            nuevaFechaNacimiento = LocalDate.parse(pintarPedirString("Ingrese la nueva fecha de nacimiento (YYYY-MM-DD):"));
                        } catch (DateTimeParseException e) {
                            System.out.println("Formato de fecha incorrecto. Por favor, ingrese la fecha en el formato YYYY-MM-DD.");
                        }
                    }

                    // Modificar los datos
                    paciente.setNombre(nuevoNombre);
                    paciente.setDireccion(nuevaDireccion);
                    paciente.setFechaNacimiento(nuevaFechaNacimiento);

                    // Llamar al método actualizar
                    pacienteRepo.actualizar(paciente);
                    System.out.println("Paciente actualizado con éxito: " + paciente);
                    break; // Salir del bucle después de actualizar
                } else {
                    System.out.println("No se encontró un paciente con ID: " + idPaciente + ". Intente nuevamente.");
                }
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al modificar el paciente. Por favor, intente nuevamente.");
            e.printStackTrace();
        }
    }


    //Método para asignar el Doctor a un Paciente
    private static void asignarDoctorAPaciente() {
        try {
            String nombreDoctor = null;
            while (true) {
                while (nombreDoctor == null || nombreDoctor.trim().isEmpty() || !nombreDoctor.matches("[a-zA-ZáéíóúÁÉÍÓÚ. ]+")) {
                    nombreDoctor = pintarPedirString("Introduzca el nombre del doctor:");
                    if (nombreDoctor == null || nombreDoctor.trim().isEmpty() || !nombreDoctor.matches("[a-zA-ZáéíóúÁÉÍÓÚ. ]+")) {
                        System.out.println("El nombre del doctor solo puede contener letras, puntos, espacios y tildes. Intente nuevamente.");
                    }
                }

                // Verificar si el doctor existe
                Doctor doctor = doctorRepo.encontrarUnoPorNombre(nombreDoctor);
                if (doctor == null) {
                    System.out.println("El doctor no existe. Intente nuevamente.");
                    nombreDoctor = null;
                } else {
                    System.out.println("Doctor encontrado: " + doctor);
                    break;
                }
            }

            String nombrePaciente = null;
            while (true) {
                while (nombrePaciente == null || nombrePaciente.trim().isEmpty() || !nombrePaciente.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                    nombrePaciente = pintarPedirString("Introduzca el nombre del paciente:");
                    if (nombrePaciente == null || nombrePaciente.trim().isEmpty() || !nombrePaciente.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                        System.out.println("El nombre del paciente solo puede contener letras y tildes. Intente nuevamente.");
                    }
                }

                // Verificar si el paciente existe
                List<Paciente> pacientes = pacienteRepo.encontrarPorNombre(nombrePaciente);
                if (pacientes.isEmpty()) {
                    System.out.println("El paciente no existe. Intente nuevamente.");
                    nombrePaciente = null;
                } else {
                    System.out.println("Pacientes encontrados: " + pacientes);
                    break;
                }
            }

            // Asignar el doctor al paciente
            boolean exito = doctorRepo.asignarDoctorAPaciente(nombreDoctor, nombrePaciente);
            if (exito) {
                System.out.println("Doctor asignado al paciente con éxito.");
            } else {
                System.out.println("No se pudo asignar el doctor al paciente. Intente nuevamente.");
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al asignar un doctor a un paciente. Por favor, intente nuevamente.");
            e.printStackTrace();
        }
    }


    //Método para indicar al paciente su fecha de inicio del tratameineto y fecha fin del tratameineto
    private static void indicarFechaFinTratamiento() {
        try {
            String nombrePaciente = null;
            while (nombrePaciente == null || nombrePaciente.trim().isEmpty() || !nombrePaciente.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                nombrePaciente = pintarPedirString("Introduzca el nombre del paciente:");
                if (nombrePaciente == null || nombrePaciente.trim().isEmpty() || !nombrePaciente.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                    System.out.println("El nombre del paciente solo puede contener letras y tildes. Intente nuevamente.");
                }
            }

            // Solicitar y verificar la fecha de inicio
            LocalDate fechaInicio = null;
            while (fechaInicio == null) {
                try {
                    fechaInicio = LocalDate.parse(pintarPedirString("Introduzca la fecha de inicio del tratamiento (YYYY-MM-DD):"));
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de fecha incorrecto. Por favor, ingrese la fecha en el formato YYYY-MM-DD.");
                }
            }

            // Solicitar y verificar el ID del tratamiento
            int idTratamiento = -1;
            while (idTratamiento < 0) {
                try {
                    idTratamiento = Integer.parseInt(pintarPedirString("Introduzca el ID del tratamiento:"));

                    // Verificar si el tratamiento existe
                    boolean tratamientoExiste = tratamientoRepo.tratamientoExiste(idTratamiento);
                    if (!tratamientoExiste) {
                        System.out.println("El ID del tratamiento no existe. Intente nuevamente.");
                        idTratamiento = -1;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("El ID del tratamiento debe ser un número. Intente nuevamente.");
                }
            }

            // Solicitar y verificar la fecha de fin
            LocalDate fechaFin = null;
            while (fechaFin == null) {
                try {
                    fechaFin = LocalDate.parse(pintarPedirString("Introduzca la fecha de fin del tratamiento (YYYY-MM-DD):"));
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de fecha incorrecto. Por favor, ingrese la fecha en el formato YYYY-MM-DD.");
                }
            }

            // Llamar al método para indicar la fecha de fin del tratamiento
            tratamientoRepo.indicarFechaFinTratamiento(nombrePaciente, fechaInicio, idTratamiento, fechaFin);

        } catch (Exception e) {
            System.out.println("Ocurrió un error al indicar el inicio y fin del tratamiento. Por favor, intente nuevamente.");
            e.printStackTrace();
        }
    }

    // Método para cambiar de hospital un tratamiento
    private static void cambiarHospitalTratamiento() {
        try {
            int tratamientoId = -1;
            String nombreNuevoHospital = null;

            // Verificar existencia del tratamiento
            while (tratamientoId <= 0) {
                tratamientoId = pintarPedirInt("Introduzca el ID del tratamiento:");

                boolean tratamientoExiste = tratamientoRepo.tratamientoExiste(tratamientoId);
                if (!tratamientoExiste) {
                    System.out.println("El ID del tratamiento no existe. Intente nuevamente.");
                    tratamientoId = -1;
                }
            }

            // Verificar existencia del hospital
            while (nombreNuevoHospital == null || nombreNuevoHospital.trim().isEmpty() || !nombreNuevoHospital.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                nombreNuevoHospital = pintarPedirString("Introduzca el nombre del nuevo hospital:");
                if (nombreNuevoHospital == null || nombreNuevoHospital.trim().isEmpty() || !nombreNuevoHospital.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                    System.out.println("El nombre del hospital no puede estar vacío. Intente nuevamente.");
                } else {
                    boolean hospitalExiste = tratamientoRepo.hospitalExiste(nombreNuevoHospital);
                    if (!hospitalExiste) {
                        System.out.println("El nombre del hospital no existe. Intente nuevamente.");
                        nombreNuevoHospital = null;
                    }
                }
            }

            // Llamar al método para cambiar el hospital del tratamiento
            tratamientoRepo.cambiarHospitalTratamiento(tratamientoId, nombreNuevoHospital);
        } catch (Exception e) {
            System.out.println("Ocurrió un error al intentar cambiar el hospital y su tratamiento. Por favor, intente nuevamente.");
            e.printStackTrace();
        }
    }


    private static void mostrarDatosPaciente() {
        String nombrePaciente = null;
        while (nombrePaciente == null || nombrePaciente.trim().isEmpty() || !nombrePaciente.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
            nombrePaciente = pintarPedirString("Introduzca el nombre del paciente:");
            if (nombrePaciente == null || nombrePaciente.trim().isEmpty() || !nombrePaciente.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                System.out.println("El nombre del paciente solo puede contener letras, tildes y vacío. Intente nuevamente.");
            }
        }

        try {
            Paciente paciente = pacienteRepo.obtenerDatosPaciente(nombrePaciente);
            if (paciente != null) {
                System.out.println("ID: " + paciente.getId());
                System.out.println("Nombre: " + paciente.getNombre());
                System.out.println("Fecha de Nacimiento: " + paciente.getFechaNacimiento());
                System.out.println("Dirección: " + paciente.getDireccion());

                System.out.println("Tratamientos:");
                for (Recibe tratamiento : paciente.getTratamientos()) {
                    System.out.println("  - Tipo: " + tratamiento.getTratamiento().getTipo());
                    System.out.println("    Fecha de Inicio: " + tratamiento.getId().getFechaInicio());
                    System.out.println("    Fecha de Fin: " + tratamiento.getFechaFin());
                }

                System.out.println("Citas:");
                for (Cita cita : paciente.getCitas()) {
                    System.out.println("  - Doctor: " + cita.getDoctor().getNombre());
                    System.out.println("    Fecha: " + cita.getFecha());
                    System.out.println("    Estado: " + cita.getEstado());
                }
            } else {
                System.out.println("Paciente no encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al intentar ver los datos del paciente. Por favor, intente nuevamente.");
            e.printStackTrace();
        }
    }


    //Método para mostrar los datos de tratamiento de un hospital
    private static void mostrarDatosTratamientosHospital() {
        String nombreHospital = null;

        // Verificar el nombre del hospital
        while (nombreHospital == null || nombreHospital.trim().isEmpty() || !nombreHospital.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
            nombreHospital = pintarPedirString("Introduzca el nombre del hospital:");

            if (nombreHospital == null || nombreHospital.trim().isEmpty() || !nombreHospital.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                System.out.println("El nombre del hospital no puede estar vacío y solo debe contener letras y tildes. Intente nuevamente.");
            }
        }

        try {
            // Obtener los tratamientos del hospital
            List<Tratamiento> tratamientos = tratamientoRepo.mostrarDatosTratamientosHospital(nombreHospital);

            // Mostrar los tratamientos si existen
            if (tratamientos != null && !tratamientos.isEmpty()) {
                tratamientos.forEach(tratamiento -> {
                    System.out.println("ID: " + tratamiento.getId());
                    System.out.println("Tipo: " + tratamiento.getTipo());
                    System.out.println("Costo: " + tratamiento.getCosto());
                    System.out.println("Hospital: " + tratamiento.getHospital().getNombre());
                });
            } else {
                System.out.println("No se encontraron tratamientos para el hospital: " + nombreHospital);
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al intentar ver los datos del tratamiento del hospital. Por favor, intente nuevamente.");
            e.printStackTrace();
        }
    }


    //Método para mostrar el número de tratamientos en un  hospital
    private static void mostrarNumeroTratamientosHospital() {
        String nombreHospital = null;

        // Verificar el nombre del hospital
        while (nombreHospital == null || nombreHospital.trim().isEmpty() || !nombreHospital.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
            nombreHospital = pintarPedirString("Introduzca el nombre del hospital:");

            if (nombreHospital == null || nombreHospital.trim().isEmpty() || !nombreHospital.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
                System.out.println("El nombre del hospital solo puede contener letras y tildes. Intente nuevamente.");
            }
        }

        try {
            // Contar el número de tratamientos por hospital
            Long numeroTratamientos = tratamientoRepo.contarTratamientosPorHospital(nombreHospital);

            // Mostrar el nombre del hospital y el número total de tratamientos
            if (numeroTratamientos != null && numeroTratamientos > 0) {
                System.out.println("El número total de tratamientos en el hospital " + nombreHospital + " es: " + numeroTratamientos);

                // Mostrar los tratamientos si existen
                List<Tratamiento> tratamientos = tratamientoRepo.mostrarDatosTratamientosHospital(nombreHospital);
                tratamientos.forEach(tratamiento -> {
                    System.out.println("ID: " + tratamiento.getId());
                    System.out.println("Tipo: " + tratamiento.getTipo());
                    System.out.println("Costo: " + tratamiento.getCosto());
                    System.out.println("Hospital: " + tratamiento.getHospital().getNombre());
                });
            } else {
                System.out.println("No se encontraron tratamientos para el hospital: " + nombreHospital);
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al mostrar el número de tratamientos. Por favor, intente nuevamente.");
            e.printStackTrace();
        }
    }
}


