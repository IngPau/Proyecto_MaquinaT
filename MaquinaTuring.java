import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
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
    private final Set<String> estadosFinales;
    private final List<Transicion> transiciones;

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

    public void exportarGrafo(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("digraph TuringMachine {\n");

            for (Transicion t : transiciones) {
                writer.write(String.format("  \"%s\" -> \"%s\" [label=\"%c:%c,%c\"];\n", 
                                           t.estadoInicial, t.nuevoEstado, 
                                           t.simboloLeido, t.simboloEscrito, t.movimiento));
            }

            writer.write(String.format("  \"%s\" [shape=doublecircle];\n", estadoInicial));
            for (String estadoFinal : estadosFinales) {
                writer.write(String.format("  \"%s\" [shape=doublecircle, style=filled, color=lightblue];\n", estadoFinal));
            }

            writer.write("}\n");
            System.out.println("Grafo exportado en formato DOT en el archivo " + filename);
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo DOT: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        MaquinaTuring maquina = new MaquinaTuring();
        maquina.solicitarDatos();
        maquina.mostrarTablaTransiciones();
        maquina.exportarGrafo("grafo_turing.dot");
    }
}
