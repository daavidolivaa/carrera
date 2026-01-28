package es.etg.dam.partida;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import es.etg.dam.cliente.Cliente;
import es.etg.dam.common.Conexion;

public class Servidor {

    public static final String HOST = "localhost";
    public static final int PUERTO = 8888;
    public final static int NUM_JUG = 4;
    private final static String MSG_JUGADOR_REGISTRADO = "Jugador registrado: %s";

    public static void main(String[] args) throws IOException {

        try (ServerSocket server = new ServerSocket(PUERTO)) {

            while (true) {
                Jugador[] jugadores = new Jugador[NUM_JUG];

                for (int i = 0; i < NUM_JUG; i++) {
                    Socket socket = server.accept();
                    String nombre = Conexion.recibir(socket);
                    Conexion.enviar(Cliente.OK, socket);

                    jugadores[i] = new Jugador(nombre, socket);
                    System.out.println(String.format(MSG_JUGADOR_REGISTRADO, nombre));
                }

                Thread carrera = new Thread(new Carrera(jugadores));
                carrera.start();

            }
        }
    }
}
