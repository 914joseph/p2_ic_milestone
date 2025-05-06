package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando não há mensagens para o usuário.
 */
public class NoMessagesException extends RuntimeException {
    public NoMessagesException() {
        super("Não há mensagens.");
    }
}