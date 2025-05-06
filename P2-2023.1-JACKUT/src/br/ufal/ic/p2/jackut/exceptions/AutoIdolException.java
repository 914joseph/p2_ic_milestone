package br.ufal.ic.p2.jackut.exceptions;

public class AutoIdolException extends RuntimeException {
    public AutoIdolException() {
        super("Usuário não pode ser fã de si mesmo.");
    }
}