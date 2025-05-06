package br.ufal.ic.p2.jackut;

/**
 * Fábrica para criar instâncias de usuários.
 */
public class UserFactory {
    /**
     * Cria um novo usuário.
     *
     * @param login    Login do usuário.
     * @param password Senha do usuário.
     * @param name     Nome do usuário.
     * @return Instância de `Users`.
     */
    public static Users createUser(String login, String password, String name) {
        return new Users(login, password, name);
    }
}