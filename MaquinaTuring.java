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
        // Valida que el estado no esté vacío y tenga letras y números, no solo números
        return Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$", estado) && !estado.isEmpty();
    }

    private boolean validarEstadoSinSoloNumeros(String estado) {
        // Valida que el estado no sea solo números y no esté vacío
        return !estado.isEmpty() && !estado.matches("\\d+");
    }

    private boolean validarFormatoTransicion(String formato) {
        // Valida que el formato de transición sea a:b,c donde a y b son símbolos, y c es L, R o S
        return Pattern.matches("^[^\\s,:;]{1}:[^\\s,:;]{1},[LRS]$", formato);
    }

    public void solicitarDatos() {
        Scanner scanner = new Scanner(System.in);

        // Solicitar Estado Inicial
        while (true) {
            System.out.print("Estado inicial: ");
            estadoInicial = scanner.nextLine().trim();
            if (validarEstado(estadoInicial) && validarEstadoSinSoloNumeros(estadoInicial)) {
                break;
            } else {
                System.out.println("Error: El estado inicial debe contener letras y números, no puede estar vacío ni ser solo números.");
            }
        }

        // Solicitar Estados Finales
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

        // Solicitar Transiciones
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

            // Solicitar datos hasta que todos sean válidos
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

                // Si todos los datos son válidos, se agregan a la lista de transiciones
                datosValidos = true;
                char simboloLeido = formato.charAt(0);
                char simboloEscrito = formato.charAt(2);
                char movimiento = formato.charAt(4);
                transiciones.add(new Transicion(estadoInicialTransicion, simboloLeido, simboloEscrito, movimiento, nuevoEstado));
            }
        }
        scanner.close();
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

    public void evaluarCadenas() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Ingrese la cadena a evaluar (use % para representar un espacio en blanco o 'salir' para terminar): ");
            String cadena = scanner.nextLine();

            if (cadena.equalsIgnoreCase("salir")) {
                System.out.println("Fin de la evaluación de cadenas.");
                System.out.println("Gracias por utilizar el programa");
                break;
            }

            if (esAceptada(cadena)) {
                System.out.println("La cadena es aceptada por la máquina de Turing.");
            } else if (esDecidible(cadena)) {
                System.out.println("La cadena es decidible pero no es aceptada.");
            } else {
                System.out.println("La cadena no es aceptada ni decidible.");
            }
        }
        scanner.close();
    }

    private boolean esAceptada(String cadena) {
        // Inicia en el estado inicial y procesa la cadena
        String estadoActual = estadoInicial;
        int posicion = 0;

        while (posicion < cadena.length()) {
            char simboloLeido = cadena.charAt(posicion);
            boolean transicionEncontrada = false;

            // Busca una transición correspondiente al estado actual y símbolo leído
            for (Transicion t : transiciones) {
                if (t.estadoInicial.equals(estadoActual) && t.simboloLeido == simboloLeido) {
                    // Actualiza el estado y la cadena según la transición
                    cadena = cadena.substring(0, posicion) + t.simboloEscrito + cadena.substring(posicion + 1);
                    estadoActual = t.nuevoEstado;
                    posicion += (t.movimiento == 'R') ? 1 : (t.movimiento == 'L') ? -1 : 0; // Mueve la cabeza de lectura/escritura
                    transicionEncontrada = true;
                    break;
                }
            }

            // Si no se encontró una transición, la cadena no es aceptada
            if (!transicionEncontrada) {
                return false;
            }

            // Verifica si el estado actual es uno de los estados finales
            if (estadosFinales.contains(estadoActual)) {
                return true; // La cadena es aceptada
            }
        }

        // Si la cadena se ha procesado completamente pero no está en un estado final
        return estadosFinales.contains(estadoActual);
    }

    private boolean esDecidible(String cadena) {
        // Un lenguaje es decidible si se puede construir una máquina de Turing que siempre alcanza el estado de parada
        // En este caso, consideramos que si el estado final se alcanza al procesar la cadena, es decidible
        String estadoActual = estadoInicial;
        int posicion = 0;

        while (posicion < cadena.length()) {
            char simboloLeido = cadena.charAt(posicion);
            boolean transicionEncontrada = false;

            // Busca una transición correspondiente al estado actual y símbolo leído
            for (Transicion t : transiciones) {
                if (t.estadoInicial.equals(estadoActual) && t.simboloLeido == simboloLeido) {
                    // Actualiza el estado y la cadena según la transición
                    cadena = cadena.substring(0, posicion) + t.simboloEscrito + cadena.substring(posicion + 1);
                    estadoActual = t.nuevoEstado;
                    posicion += (t.movimiento == 'R') ? 1 : (t.movimiento == 'L') ? -1 : 0; // Mueve la cabeza de lectura/escritura
                    transicionEncontrada = true;
                    break;
                }
            }

            // Si no se encontró una transición, se detiene el proceso
            if (!transicionEncontrada) {
                break;
            }
        }

        // Verifica si el estado final se alcanzó o si se detuvo en un estado no final
        return estadosFinales.contains(estadoActual) || (posicion >= cadena.length());
    }

    public static void main(String[] args) {
        MaquinaTuring maquina = new MaquinaTuring();
        maquina.solicitarDatos();
        maquina.mostrarTablaTransiciones();
        maquina.evaluarCadenas();
    }
}
