package br.ufal.ic.p2.jackut.exceptions;

public class CrushAlreadyExistsException extends RuntimeException {
    public CrushAlreadyExistsException() {
        super("Usuário já está adicionado como paquera.");
    }
}