package br.ufal.ic.p2.jackut;

/**
 * Interface para observadores que serão notificados de eventos.
 */
public interface Observer {
    void update(String message);
}