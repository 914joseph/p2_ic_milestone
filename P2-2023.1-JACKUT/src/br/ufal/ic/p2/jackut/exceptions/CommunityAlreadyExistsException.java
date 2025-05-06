package br.ufal.ic.p2.jackut.exceptions;

public class CommunityAlreadyExistsException extends RuntimeException {
    public CommunityAlreadyExistsException() {
        super("Comunidade com esse nome já existe.");
    }
}