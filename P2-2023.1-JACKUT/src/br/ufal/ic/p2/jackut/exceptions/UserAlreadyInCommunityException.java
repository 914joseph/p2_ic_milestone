package br.ufal.ic.p2.jackut.exceptions;

public class UserAlreadyInCommunityException extends RuntimeException {
    public UserAlreadyInCommunityException() {
        super("Usuario já faz parte dessa comunidade.");
    }
}