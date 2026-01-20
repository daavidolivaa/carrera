package es.etg.dam.partida;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import es.etg.dam.cliente.Cliente;
import es.etg.dam.conexion.Conexion;

public class Servidor {

    static final int PUERTO = 8888;
    public final static int NUM_JUG = 4;
    private final static String JUG = "Jugador registrado: ";

    public static void main(String[] args) throws IOException {

        Jugador[] jugadores = new Jugador[NUM_JUG];
        ServerSocket server = new ServerSocket(PUERTO);

        for (int i = 0; i < NUM_JUG; i++) {
            Socket socket = server.accept();
            String nombre = Conexion.recibir(socket);
            Conexion.enviar(Cliente.OK, socket);

            jugadores[i] = new Jugador(nombre, socket);
            System.out.println(JUG + nombre);
        }

        Thread carrera = new Thread(new Carrera(jugadores));
        carrera.start();

    }
}
