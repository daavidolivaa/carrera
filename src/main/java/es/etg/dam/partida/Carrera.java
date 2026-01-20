package es.etg.dam.partida;

import java.io.IOException;
import java.util.Random;

import es.etg.dam.cliente.Cliente;
import es.etg.dam.conexion.Conexion;

public class Carrera implements Runnable {
    private final int NUM_RANGO = 10;
    private final int NUM_PUNTOS = 100;
    private final int UNO = 1;
    private final int TIEMPO = 2000;
    private final String PUNTOS = ":";
    private final String BARRA = "|";

    private final Random random = new Random();

    private Jugador[] jugadores;

    public Carrera(Jugador[] jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(TIEMPO);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Jugador ganador = null;

        while (ganador == null) {
            Jugador j = avanzar();

            try {
                notificar(j);
            } catch (IOException ex) {
            }

            if (j.getPuntos() >= NUM_PUNTOS) {
                ganador = j;
            }
        }
        finalizar(ganador);
    }

    private Jugador avanzar() {
        int jug = random.nextInt(Servidor.NUM_JUG);
        int puntos = random.nextInt(NUM_RANGO) + UNO;
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
