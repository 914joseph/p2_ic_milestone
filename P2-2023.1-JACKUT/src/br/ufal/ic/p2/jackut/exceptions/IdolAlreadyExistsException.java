package br.ufal.ic.p2.jackut.exceptions;

public class IdolAlreadyExistsException extends RuntimeException {
    public IdolAlreadyExistsException() {
        super("Usuário já está adicionado como ídolo.");
    }
}