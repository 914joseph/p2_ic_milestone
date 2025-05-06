package br.ufal.ic.p2.jackut.exceptions;

public class AutoEnemyException extends RuntimeException {
    public AutoEnemyException() {
        super("Usuário não pode ser inimigo de si mesmo.");
    }
}