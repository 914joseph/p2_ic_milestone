package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando um usuário tenta realizar uma ação em uma comunidade da qual não faz parte.
 */
public class UserNotInCommunityException extends RuntimeException {
    public UserNotInCommunityException() {
        super("Usuário não é membro da comunidade.");
    }
}