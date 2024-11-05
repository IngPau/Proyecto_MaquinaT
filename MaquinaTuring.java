import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

class Transicion {
    String estadoInicial;
    char simboloLeido;
    char simboloEscrito;
    char movimiento;
    String nuevoEstado;

    public Transicion(String estadoInicial, char simboloLeido, char simboloEscrito, char movimiento, String nuevoEstado) {
        this.estadoInicial = estadoInicial;
        this.simboloLeido = simboloLeido;
        this.simboloEscrito = simboloEscrito;
        this.movimiento = movimiento;
        this.nuevoEstado = nuevoEstado;
    }

    @Override
    public String toString() {
        return String.format("| %-15s | %-15s | %-15s | %-15s | %-10s |", estadoInicial, simboloLeido, nuevoEstado, simboloEscrito, movimiento);
    }
}

public class MaquinaTuring {
    private String estadoInicial;
    private Set<String> estadosFinales;
    private List<Transicion> transiciones;

    public MaquinaTuring() {
        this.estadosFinales = new HashSet<>();
        this.transiciones = new ArrayList<>();
    }

    private boolean validarEstado(String estado) {
        return Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$", estado) && !estado.isEmpty();
    }

    private boolean validarEstadoSinSoloNumeros(String estado) {
        return !estado.isEmpty() && !estado.matches("\\d+");
    }

    private boolean validarFormatoTransicion(String formato) {
        return Pattern.matches("^[^\\s,:;]{1}:[^\\s,:;]{1},[LRS]$", formato);
    }

    public void solicitarDatos() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n_____________________________");
            System.out.print("Estado inicial: ");
            estadoInicial = scanner.nextLine().trim();
            if (validarEstado(estadoInicial) && validarEstadoSinSoloNumeros(estadoInicial)) {
                break;
            } else {
                System.out.println("Error: El estado inicial debe contener letras y números, no puede estar vacío ni ser solo números.");
            }
        }

        int numEstadosFinales;
        while (true) {
            System.out.print("¿Cuántos estados finales hay? ");
            String entrada = scanner.nextLine();
            try {
                numEstadosFinales = Integer.parseInt(entrada);
                if (numEstadosFinales > 0) {
                    break;
                } else {
                    System.out.println("Error: El número de estados finales debe ser un entero positivo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Entrada inválida. Ingrese un número entero positivo.");
            }
        }

        for (int i = 0; i < numEstadosFinales; i++) {
            while (true) {
                System.out.print("Estado final " + (i + 1) + ": ");
                String estadoFinal = scanner.nextLine().trim();
                if (validarEstado(estadoFinal) && validarEstadoSinSoloNumeros(estadoFinal)) {
                    estadosFinales.add(estadoFinal);
                    break;
                } else {
                    System.out.println("Error: El estado final debe contener letras y números, no puede estar vacío ni ser solo números.");
                }
            }
        }

        int numTransiciones;
        while (true) {
            System.out.print("Número de transiciones que tendrá la máquina de Turing: ");
            String entrada = scanner.nextLine();
            try {
                numTransiciones = Integer.parseInt(entrada);
                if (numTransiciones > 0) {
                    break;
                } else {
                    System.out.println("Error: El número de transiciones debe ser un entero positivo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Entrada inválida. Ingrese un número entero positivo.");
            }
        }

        for (int i = 0; i < numTransiciones; i++) {
            System.out.println("\n_____________________________");
            System.out.println("Transición " + (i + 1));

            String estadoInicialTransicion, nuevoEstado;
            String formato;
            boolean datosValidos = false;

            while (!datosValidos) {
                System.out.print("Estado inicial: ");
                estadoInicialTransicion = scanner.nextLine().trim();
                if (!validarEstado(estadoInicialTransicion)) {
                    System.out.println("Error: El estado inicial debe contener letras y números.");
                    continue;
                }

                while (true) {
                    System.out.print("Formato (a:b,c): ");
                    formato = scanner.nextLine().trim();
                    if (validarFormatoTransicion(formato)) {
                        break;
                    } else {
                        System.out.println("Error: Formato inválido. Use el formato a:b,c donde 'a' y 'b' son símbolos y 'c' es L, R o S.");
                    }
                }

                System.out.print("Nuevo Estado: ");
                nuevoEstado = scanner.nextLine().trim();
                if (!validarEstado(nuevoEstado)) {
                    System.out.println("Error: El nuevo estado debe contener letras y números.");
                    continue;
                }

                datosValidos = true;
                char simboloLeido = formato.charAt(0);
                char simboloEscrito = formato.charAt(2);
                char movimiento = formato.charAt(4);
                transiciones.add(new Transicion(estadoInicialTransicion, simboloLeido, simboloEscrito, movimiento, nuevoEstado));
            }
        }
    }

    // Método para leer desde el archivo
    public void leerDesdeArchivo(String nombreArchivo) {
        try (Scanner scanner = new Scanner(new File(nombreArchivo))) {
            String linea = scanner.nextLine();

            // Separar la configuración inicial y las transiciones
            String[] partes = linea.split(";");
            String[] configuracionInicial = partes[0].split(",");

            // Leer el estado inicial
            estadoInicial = configuracionInicial[0];

            // Leer los estados finales
            int numEstadosFinales = Integer.parseInt(configuracionInicial[1]);
            for (int i = 0; i < numEstadosFinales; i++) {
                estadosFinales.add(configuracionInicial[2 + i]);
            }

            // Leer el número de transiciones
            int numTransiciones = Integer.parseInt(configuracionInicial[2 + numEstadosFinales]);

            // Leer cada transición
            for (int i = 1; i <= numTransiciones; i++) {
                String[] datosTransicion = partes[i].split(",");
                
                String estadoInicialTransicion = datosTransicion[0];
                String[] formato = datosTransicion[1].split(":");
                char simboloLeido = formato[0].charAt(0);
                char simboloEscrito = formato[1].charAt(0);
                char movimiento = datosTransicion[2].charAt(0);
                String nuevoEstado = datosTransicion[3];

                transiciones.add(new Transicion(estadoInicialTransicion, simboloLeido, simboloEscrito, movimiento, nuevoEstado));
            }

            System.out.println("Datos cargados correctamente desde el archivo.");
        } catch (FileNotFoundException e) {
            System.err.println("Error: No se encontró el archivo " + nombreArchivo);
        } catch (Exception e) {
            System.err.println("Error al leer los datos del archivo: " + e.getMessage());
        }
    }

    
    public void mostrarTablaTransiciones() {
        System.out.println("\n______________________________________________________________________________________________________");
        System.out.println("Tabla de Transiciones:");
        System.out.println("+---------------+-----------------+-----------------+-----------------+---------------+---------------+");
        System.out.println("| Transición    | Estado Inicial  | Símbolo Leído   | Nuevo Estado    | Símbolo Escr. | Movimiento |   ");
        System.out.println("+---------------+-----------------+-----------------+-----------------+---------------+---------------+");

        for (int i = 0; i < transiciones.size(); i++) {
            System.out.printf("| %-12d %s\n", i + 1, transiciones.get(i).toString());
        }

        System.out.println("+---------------+-----------------+-----------------+-----------------+---------------+---------------+");
    }

    // Método para evaluar cadenas
    public void evaluarCadenas() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n_______________________________________________________________________________________________");
            System.out.print("Ingrese la cadena a evaluar (use % para representar un espacio en blanco o 'salir' para terminar): ");
            String cadena = scanner.nextLine();

            if (cadena.equalsIgnoreCase("salir")) {
                System.out.println("________________________________");
                System.out.println("Fin de la evaluación de cadenas.");
                System.out.println("Gracias por utilizar el programa");
                break;
            }

            // Reemplazar '%' por espacio en blanco
            cadena = cadena.replace("%", " ");

            if (esAceptada(cadena)) {
                System.out.println("La cadena es aceptada por la máquina de Turing.");
            } else if (esDecidible(cadena)) {
                System.out.println("La cadena es decidible pero no es aceptada.");
            } else {
                System.out.println("La cadena no es aceptada ni decidible.");
            }
        }
    }

    private boolean esAceptada(String cadena) {
        String estadoActual = estadoInicial;
        int posicion = 0;

        while (posicion >= 0 && posicion < cadena.length()) {
            char simboloLeido = cadena.charAt(posicion);
            boolean transicionEncontrada = false;

            for (Transicion t : transiciones) {
                if (t.estadoInicial.equals(estadoActual) && t.simboloLeido == simboloLeido) {
                    // Modificar la cadena según el símbolo escrito
                    StringBuilder nuevaCadena = new StringBuilder(cadena);
                    nuevaCadena.setCharAt(posicion, t.simboloEscrito);
                    cadena = nuevaCadena.toString();

                    // Actualizar estado y posición
                    estadoActual = t.nuevoEstado;
                    posicion += (t.movimiento == 'R') ? 1 : (t.movimiento == 'L') ? -1 : 0;

                    transicionEncontrada = true;
                    break;
                }
            }

            // Si no se encuentra una transición válida, salimos
            if (!transicionEncontrada) {
                return false;
            }

            // Verificar si el estado actual es final
            if (estadosFinales.contains(estadoActual)) {
                return true;
            }
        }

        return estadosFinales.contains(estadoActual);
    }

    private boolean esDecidible(String cadena) {
        String estadoActual = estadoInicial;
        int posicion = 0;

        while (posicion >= 0 && posicion < cadena.length()) {
            char simboloLeido = cadena.charAt(posicion);
            boolean transicionEncontrada = false;

            for (Transicion t : transiciones) {
                if (t.estadoInicial.equals(estadoActual) && t.simboloLeido == simboloLeido) {
                    StringBuilder nuevaCadena = new StringBuilder(cadena);
                    nuevaCadena.setCharAt(posicion, t.simboloEscrito);
                    cadena = nuevaCadena.toString();

                    estadoActual = t.nuevoEstado;
                    posicion += (t.movimiento == 'R') ? 1 : (t.movimiento == 'L') ? -1 : 0;

                    transicionEncontrada = true;
                    break;
                }
            }

            if (!transicionEncontrada) {
                break;
            }
        }

        return estadosFinales.contains(estadoActual);
    }

    // Generación del archivo DOT y del PNG
    public void generarGrafoDot() {
        String nombreArchivoDot = "grafo.turing.dot";
        try (FileWriter writer = new FileWriter(nombreArchivoDot)) {
            writer.write("digraph G {\n");
            writer.write("  rankdir=LR;\n");
            writer.write("  node [shape = circle];\n");

            for (String estadoFinal : estadosFinales) {
                writer.write("  \"" + estadoFinal + "\" [shape=doublecircle];\n");
            }

            for (Transicion transicion : transiciones) {
                writer.write("  \"" + transicion.estadoInicial + "\" -> \"" + transicion.nuevoEstado + "\" ");
                writer.write("[label=\"" + transicion.simboloLeido + ":" + transicion.simboloEscrito + "," + transicion.movimiento + "\"];\n");
            }

            writer.write("}\n");
            System.out.println("Archivo grafo.turing.dot generado correctamente.");
        } catch (IOException e) {
            System.err.println("Error al generar el archivo DOT: " + e.getMessage());
            return;
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", nombreArchivoDot, "-o", "grafo.png");
            Process process = processBuilder.start();
            process.waitFor();
            System.out.println("Imagen grafo.png generada correctamente.");
            System.out.println("________________________________________");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al generar la imagen PNG: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        MaquinaTuring maquina = new MaquinaTuring();

        Scanner scanner = new Scanner(System.in);
        System.out.println("MENU DE INICIO\n1. Consola\n2. Leer el archivo");
        System.out.print("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        if (opcion == 1) {
            maquina.solicitarDatos();
        } else if (opcion == 2) {
            System.out.print("Ingrese el nombre del archivo .txt: ");
            String nombreArchivo = scanner.nextLine().trim();
            maquina.leerDesdeArchivo(nombreArchivo);
        } else {
            System.out.println("Opción no válida.");
            return;
        }

        maquina.mostrarTablaTransiciones();
        maquina.evaluarCadenas();
        maquina.generarGrafoDot();
        
    }
}
