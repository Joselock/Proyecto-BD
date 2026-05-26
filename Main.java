import services.reportes.*;
import entidades.*;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;
        while (!exit) {
            printMenu();
            int option = readInt("Seleccione una opción: ");
            switch (option) {
                case 1:
                    reporte1();
                    break;
                case 2:
                    reporte2();
                    break;
                case 3:
                    reporte3();
                    break;
                case 4:
                    reporte4();
                    break;
                case 5:
                    reporte5();
                    break;
                case 6:
                    reporte6();
                    break;
                case 7:
                    reporte7();
                    break;
                case 8:
                    reporte8();
                    break;
                case 9:
                    reporte9();
                    break;
                case 0:
                    exit = true;
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
            }
            if (!exit) {
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                scanner.nextLine(); // consume the newline
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n===== SISTEMA DE REPORTES HOSPITALARIOS =====");
        System.out.println("1. Listado de pacientes por área (Hospital/Departamento/Unidad)");
        System.out.println("2. Listado de médicos");
        System.out.println("3. Resumen de hospitales");
        System.out.println("4. Hospitales con más de 100 pacientes (top 5)");
        System.out.println("5. Informe durante consultas por área");
        System.out.println("6. Listado de pacientes no atendidos por área");
        System.out.println("7. Resumen del proceso");
        System.out.println("8. Unidades que deben revisar turno");
        System.out.println("9. Resumen de consultas exitosas por área");
        System.out.println("0. Salir");
    }

    // Reporte 1: Pacientes por área
    private static void reporte1() {
        System.out.println("\n--- Listado de pacientes por área ---");
        String[] areaInfo = askAreaAndCode();
        if (areaInfo == null) return;

        PacienteService service = new PacienteService();
        try {
            LinkedList<Hospital> hospitales = service.obtenerPacientesDepartamento(areaInfo[0], areaInfo[1]);
            if (hospitales.isEmpty()) {
                System.out.println("No se encontraron pacientes para el área especificada.");
            } else {
                displayHospitalStructure(hospitales, true);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el listado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Reporte 2: Listado de médicos
    private static void reporte2() {
        System.out.println("\n--- Listado de médicos ---");
        MedicoService service = new MedicoService();
        try {
            LinkedList<Hospital> hospitales = service.obtenerListadosMedicos();
            if (hospitales.isEmpty()) {
                System.out.println("No se encontraron médicos.");
            } else {
                displayHospitalStructure(hospitales, false);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el listado: " + e.getMessage());
        }
    }

    // Reporte 3: Resumen de hospitales
    private static void reporte3() {
        System.out.println("\n--- Resumen de hospitales ---");
        HospitalService service = new HospitalService();
        try {
            LinkedList<Hospital> resumen = service.resumenHospitales();
            if (resumen.isEmpty()) {
                System.out.println("No hay registros de hospitales.");
            } else {
                System.out.printf("%-30s %-15s %-15s %-15s %-15s%n", "Hospital", "Departamentos", "Unidades", "Médicos", "Pacientes");
                for (Hospital h : resumen) {
                    System.out.printf("%-30s %-15d %-15d %-15d %-15d%n",
                            h.getNombreHos(), h.getCantDep(), h.getCantUni(),
                            h.getCantMed(), h.getCantPac());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el resumen: " + e.getMessage());
        }
    }

    // Reporte 4: Hospitales con más de 100 pacientes
    private static void reporte4() {
        System.out.println("\n--- Hospitales con más de 100 pacientes (top 5) ---");
        HospitalService service = new HospitalService();
        LinkedList<Hospital> hospitales = service.hospitalesMayorCantidadPacientes();
        if (hospitales.isEmpty()) {
            System.out.println("No se encontraron hospitales con más de 100 pacientes.");
        } else {
            System.out.printf("%-30s %-20s%n", "Hospital", "Cantidad de Pacientes");
            for (Hospital h : hospitales) {
                System.out.printf("%-30s %-20d%n", h.getNombreHos(), h.getCantPac());
            }
        }
    }

    // Reporte 5: Informe durante consultas
    private static void reporte5() {
        System.out.println("\n--- Informe durante consultas por área ---");
        String[] areaInfo = askAreaAndCode();
        if (areaInfo == null) return;

        ConsultaService service = new ConsultaService();
        try {
            LinkedList<Hospital> hospitales = service.obtenerInformeHospital(areaInfo[0], areaInfo[1]);
            if (hospitales.isEmpty()) {
                System.out.println("No se encontraron datos para el área especificada.");
            } else {
                displayInformesTurnos(hospitales);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el informe: " + e.getMessage());
        }
    }

    // Reporte 6: Pacientes no atendidos
    private static void reporte6() {
        System.out.println("\n--- Listado de pacientes no atendidos por área ---");
        String[] areaInfo = askAreaAndCode();
        if (areaInfo == null) return;

        PacienteService service = new PacienteService();
        try {
            LinkedList<Hospital> hospitales = service.listadoNoAtendidos(areaInfo[0], areaInfo[1]);
            if (hospitales.isEmpty()) {
                System.out.println("No se encontraron pacientes no atendidos para el área especificada.");
            } else {
                displayTurnosConCausa(hospitales);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el listado: " + e.getMessage());
        }
    }

    // Reporte 7: Resumen del proceso
    private static void reporte7() {
        System.out.println("\n--- Resumen del proceso ---");
        HospitalService service = new HospitalService();
        try {
            LinkedList<Hospital> hospitales = service.resumenProceso();
            if (hospitales.isEmpty()) {
                System.out.println("No hay datos para el resumen del proceso.");
            } else {
                displayResumenProceso(hospitales);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el resumen: " + e.getMessage());
        }
    }

    // Reporte 8: Unidades que deben revisar turno
    private static void reporte8() {
        System.out.println("\n--- Unidades que deben revisar turno ---");
        UnidadService service = new UnidadService();
        try {
            LinkedList<Hospital> hospitales = service.listadoUnidades();
            if (hospitales.isEmpty()) {
                System.out.println("No se encontraron unidades con revisión de turnos requerida.");
            } else {
                displayUnidadesRevision(hospitales);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el listado: " + e.getMessage());
        }
    }

    // Reporte 9: Resumen consultas exitosas
    private static void reporte9() {
        System.out.println("\n--- Resumen de consultas exitosas por área ---");
        System.out.print("Tipo de área (Hospital/Unidad): ");
        String area = scanner.nextLine().trim();
        if (!area.equalsIgnoreCase("Hospital") && !area.equalsIgnoreCase("Unidad")) {
            System.out.println("Área inválida. Debe ser 'Hospital' o 'Unidad'.");
            return;
        }
        System.out.print("Código del hospital o unidad: ");
        String codigo = scanner.nextLine().trim();
        if (codigo.isEmpty()) {
            System.out.println("Código no puede estar vacío.");
            return;
        }

        ConsultaService service = new ConsultaService();
        try {
            LinkedList<Hospital> hospitales = service.resumenConsultasExitosas(area, codigo);
            if (hospitales.isEmpty()) {
                System.out.println("No se encontraron datos para el área especificada.");
            } else {
                displayConsultasExitosas(hospitales);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el resumen: " + e.getMessage());
        }
    }

    // Helper: preguntar área y código
    private static String[] askAreaAndCode() {
        System.out.print("Tipo de área (Hospital/Departamento/Unidad): ");
        String area = scanner.nextLine().trim();
        if (!area.equalsIgnoreCase("Hospital") && !area.equalsIgnoreCase("Departamento") && !area.equalsIgnoreCase("Unidad")) {
            System.out.println("Área inválida. Debe ser 'Hospital', 'Departamento' o 'Unidad'.");
            return null;
        }
        System.out.print("Código del " + area + ": ");
        String codigo = scanner.nextLine().trim();
        if (codigo.isEmpty()) {
            System.out.println("Código no puede estar vacío.");
            return null;
        }
        return new String[]{area, codigo};
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Entrada inválida. Ingrese un número: ");
            scanner.next();
        }
        int num = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return num;
    }

    // Métodos de visualización según la estructura de las entidades.
    // Se basan en los getters que presumiblemente existen.

    private static void displayHospitalStructure(LinkedList<Hospital> hospitales, boolean showPacientes) {
        for (Hospital h : hospitales) {
            System.out.println("\n🏥 HOSPITAL: " + h.getNombreHos());
            for (Departamento d : h.getDepartamentos()) {
                System.out.println("  📁 Departamento: " + d.getNombreDep());
                for (Unidad u : d.getUnidades()) {
                    System.out.println("    🏥 Unidad: " + u.getNombreUni());
                    if (showPacientes && u.getRegistro() != null && u.getRegistro().getPacientes() != null) {
                        System.out.println("      👥 Pacientes:");
                        for (Paciente p : u.getRegistro().getPacientes()) {
                            System.out.printf("        - %s (%s) - %s%n", p.getNombrePac(), p.getNumHisCli(), p.getDireccionP());
                        }
                    } else if (!showPacientes && u.getMedicos() != null) {
                        System.out.println("      👨‍⚕️ Médicos:");
                        for (Medico m : u.getMedicos()) {
                            System.out.printf("        - %s (Lic: %s, Esp: %s, Tel: %s)%n",
                                    m.getNombreMed(), m.getNumeroLic(), m.getEspecialidad(), m.getTelefono());
                        }
                    }
                }
            }
        }
    }

    private static void displayInformesTurnos(LinkedList<Hospital> hospitales) {
        for (Hospital h : hospitales) {
            System.out.println("\n🏥 HOSPITAL: " + h.getNombreHos());
            for (Departamento d : h.getDepartamentos()) {
                System.out.println("  📁 Departamento: " + d.getNombreDep());
                for (Unidad u : d.getUnidades()) {
                    System.out.println("    🏥 Unidad: " + u.getNombreUni());
                    if (u.getInformes() != null) {
                        for (Informe i : u.getInformes()) {
                            System.out.printf("      📄 Informe: %s, Fecha: %s, Hora: %s, Cant.Inicial: %d, Admitidos: %d, Altas: %d, Anteriores: %d, Del día: %d%n",
                                    i.getNumIn(), i.getFecha(), i.getHora(), i.getCantIni(),
                                    i.getCantAdm(), i.getPacAlta(), i.getCantAnterior(), i.getTotal());
                        }
                    }
                    if (u.getTurnos() != null) {
                        for (Turno t : u.getTurnos()) {
                            System.out.printf("      🎫 Turno N°%d%n", t.getNumTurn());
                        }
                    }
                }
            }
        }
    }

    private static void displayTurnosConCausa(LinkedList<Hospital> hospitales) {
        for (Hospital h : hospitales) {
            System.out.println("\n🏥 HOSPITAL: " + h.getNombreHos());
            for (Departamento d : h.getDepartamentos()) {
                System.out.println("  📁 Departamento: " + d.getNombreDep());
                for (Unidad u : d.getUnidades()) {
                    System.out.println("    🏥 Unidad: " + u.getNombreUni());
                    for (Turno t : u.getTurnos()) {
                        if (t.getConsulta() != null && t.getConsulta().getPaciente() != null) {
                            Paciente p = t.getConsulta().getPaciente();
                            System.out.printf("      🚫 Turno %d - Paciente: %s (%s) - Causa: %s%n",
                                    t.getNumTurn(), p.getNombrePac(), p.getNumHisCli(),
                                    t.getConsulta().getCausa());
                        } else {
                            System.out.printf("      🚫 Turno %d - Cantidad no atendidos: %d%n",
                                    t.getNumTurn(), t.getCantNoAten());
                        }
                    }
                }
            }
        }
    }

    private static void displayResumenProceso(LinkedList<Hospital> hospitales) {
        for (Hospital h : hospitales) {
            System.out.println("\n🏥 HOSPITAL: " + h.getNombreHos());
            for (Departamento d : h.getDepartamentos()) {
                System.out.println("  📁 Departamento: " + d.getNombreDep());
                for (Unidad u : d.getUnidades()) {
                    System.out.println("    🏥 Unidad: " + u.getNombreUni());
                    for (Turno t : u.getTurnos()) {
                        System.out.printf("      🎫 Turno N°%d%n", t.getNumTurn());
                    }
                    for (Informe i : u.getInformes()) {
                        System.out.printf("      📊 Informe: Hora %s, Atendidos: %d, Altas: %d, Total: %d, %%. Atend: %.2f%%, Extranjeros: %d, Provincia: %d, OtraUnidad: %d, OtrasCausas: %d, Desconoce: %d%n",
                                i.getHora(), i.getPacAtend(), i.getPacAlta(), i.getTotal(),
                                i.getPorcentajePacAtend(), i.getCantPacExtranjero(), i.getCantPacProvincia(),
                                i.getCantPacOtraUnidad(), i.getCantPacOtrasCausas(), i.getCantPacDesconoce());
                    }
                }
            }
        }
    }

    private static void displayUnidadesRevision(LinkedList<Hospital> hospitales) {
        for (Hospital h : hospitales) {
            System.out.println("\n🏥 HOSPITAL: " + h.getNombreHos());
            for (Departamento d : h.getDepartamentos()) {
                System.out.println("  📁 Departamento: " + d.getNombreDep());
                for (Unidad u : d.getUnidades()) {
                    System.out.println("    🏥 Unidad: " + u.getNombreUni());
                    if (u.getMedicos() != null) {
                        for (Medico m : u.getMedicos()) {
                            System.out.printf("      👨‍⚕️ Médico responsable: %s%n", m.getNombreMed());
                        }
                    }
                    if (u.getInformes() != null) {
                        for (Informe i : u.getInformes()) {
                            System.out.printf("      📈 Total pacientes: %d, Atendidos: %d, Porcentaje: %.2f%%%n",
                                    i.getTotal(), i.getPacAtend(), i.getPorcentajePacAtend());
                        }
                    }
                }
            }
        }
    }

    private static void displayConsultasExitosas(LinkedList<Hospital> hospitales) {
        for (Hospital h : hospitales) {
            System.out.println("\n🏥 HOSPITAL: " + h.getNombreHos());
            for (Departamento d : h.getDepartamentos()) {
                System.out.println("  📁 Departamento: " + d.getNombreDep());
                for (Unidad u : d.getUnidades()) {
                    System.out.println("    🏥 Unidad: " + u.getNombreUni());
                    for (Turno t : u.getTurnos()) {
                        if (t.getMedico() != null) {
                            System.out.printf("      ✅ Turno %d - Médico: %s, Pacientes atendidos en turno: %d%n",
                                    t.getNumTurn(), t.getMedico().getNombreMed(), t.getCantAten());
                        } else {
                            System.out.printf("      ✅ Turno %d - Pacientes atendidos en turno: %d%n",
                                    t.getNumTurn(), t.getCantAten());
                        }
                    }
                    for (Informe i : u.getInformes()) {
                        System.out.printf("      📈 Total pacientes atendidos: %d%n", i.getPacAtend());
                    }
                }
            }
        }
    }
}