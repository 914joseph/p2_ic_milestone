package br.ufal.ic.p2.jackut.exceptions;

public class InteractionWithEnemyException extends RuntimeException {
    public InteractionWithEnemyException(String enemyName) {
        super("Função inválida: " + enemyName + " é seu inimigo.");
    }
}