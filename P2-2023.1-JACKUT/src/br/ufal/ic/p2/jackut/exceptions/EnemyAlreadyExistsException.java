package br.ufal.ic.p2.jackut.exceptions;

public class EnemyAlreadyExistsException extends RuntimeException {
    public EnemyAlreadyExistsException() {
        super("Usuário já está adicionado como inimigo.");
    }
}