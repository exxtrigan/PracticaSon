package uvm;

import javax.swing.*;
import java.awt.*;

public class HiloLimpiador extends Thread {
    private final VentanaMatrizHilos ventana;
    private volatile boolean activo = true;
    private volatile boolean pausa = false;

    public HiloLimpiador(VentanaMatrizHilos ventana) {
        this.ventana = ventana;
    }

    public void pausarOContinuar() {
        pausa = !pausa;
        synchronized (this) {
            notifyAll();
        }
    }

    public boolean estaPausado() {
        return pausa;
    }

    public void terminar() {
        activo = false;
        synchronized (this) {
            notifyAll();
        }
        interrupt();
    }

    private void esperarPausa() throws InterruptedException {
        synchronized (this) {
            while (pausa && activo) wait();
        }
    }

    @Override
    public void run() {
        while (activo) {
            try {
                esperarPausa();
                if (!activo) break;

                JLabel[][] matriz = ventana.getMatrizLabels();
                int modificadas = 0;

                for (int f = 0; f < 15; f++) {
                    for (int c = 0; c < 15; c++) {
                        if (!"-".equals(matriz[f][c].getText())
                                || !matriz[f][c].getForeground()
                                .equals(Color.BLACK)) {
                            modificadas++;
                        }
                    }
                }

                if (modificadas > 112) {
                    ventana.limpiarBloque();
                }

                Thread.sleep(500);
            } catch (InterruptedException e) {
                if (!activo) break;
            }
        }
    }
}