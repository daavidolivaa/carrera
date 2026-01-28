package es.etg.dam.partida;

import java.io.IOException;
import java.util.Random;

import es.etg.dam.cliente.Cliente;
import es.etg.dam.common.Conexion;

public class Carrera implements Runnable {
    private static final int MAX_PUNTOS = 100;
    private static final int MAX_AVANCE = 10;
    private static final int TIEMPO = 2000;
    private static final int UNO = 1;
    private final String PUNTOS = ":";
    private final String BARRA = "|";

    private Jugador[] jugadores;

    public Carrera(Jugador[] jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(TIEMPO);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }

        Jugador ganador = null;

        while (ganador == null) {
            Jugador j = avanzar();

            try {
                notificar(j);
            } catch (IOException ex) {
            }

            if (j.getPuntos() >= MAX_PUNTOS) {
                ganador = j;
            }
        }
        finalizar(ganador);
    }

    private Jugador avanzar() {
        int jug = random.nextInt(Servidor.NUM_JUG);
        int puntos = random.nextInt(MAX_AVANCE) + UNO;
        jugadores[jug].sumar(puntos);
        return jugadores[jug];
    }

    private void notificar(Jugador jugador) throws IOException {
        String msg = obtenerPuntos();
        Conexion.enviar(msg, jugador.getConexion());

    }

    private String obtenerPuntos() throws IOException {
        StringBuilder sb = new StringBuilder();

        for (Jugador j : jugadores) {
            sb.append(j.getNombre());
            sb.append(PUNTOS);
            sb.append(j.getPuntos());
            sb.append(BARRA);
        }

        return sb.toString();
    }

    private void finalizar(Jugador ganador) {
        for (Jugador j : jugadores) {
            try {
                if (j == ganador) {
                    Conexion.enviar(Cliente.MSG_GANADO, j.getConexion());
                } else {
                    Conexion.enviar(Cliente.MSG_PERDIDO, j.getConexion());
                }
                j.getConexion().close();

            } catch (IOException e) {
            }
        }
    }

}
