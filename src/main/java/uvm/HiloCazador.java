package uvm;

import javax.swing.*;
import java.awt.*;

public class HiloCazador extends Thread {
    private final VentanaMatrizHilos ventana;
    private volatile boolean activo = true;
    private volatile boolean pausa = false;
    private int encontrados = 0;

    public HiloCazador(VentanaMatrizHilos ventana) {
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

                boolean encontrado = false;
                JLabel[][] matriz = ventana.getMatrizLabels();

                for (int f = 0; f < 15 && activo && !encontrado; f++) {
                    for (int c = 0; c < 15 && activo; c++) {
                        esperarPausa();
                        if (!activo) break;

                        if ("@".equals(matriz[f][c].getText())) {
                            encontrado = true;

                            ventana.actualizarCelda(
                                    f, c, "@", Color.RED
                            );

                            ventana.activarClima();

                            long inicio = System.currentTimeMillis();
                            while (activo
                                    && System.currentTimeMillis() - inicio < 1000) {
                                esperarPausa();
                                if (!activo) break;
                                Thread.sleep(50);
                            }

                            if (activo) {
                                ventana.actualizarCelda(
                                        f, c, "X", Color.BLACK
                                );
                                encontrados++;
                                ventana.actualizarContador(encontrados);
                            }

                            break;
                        }
                    }
                }

                if (!encontrado) Thread.sleep(100);

            } catch (InterruptedException e) {
                if (!activo) break;
            }
        }
    }
}