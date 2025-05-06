package br.ufal.ic.p2.jackut.exceptions;

public class AutoCrushException extends RuntimeException {
    public AutoCrushException() {
        super("Usuário não pode ser paquera de si mesmo.");
    }
}