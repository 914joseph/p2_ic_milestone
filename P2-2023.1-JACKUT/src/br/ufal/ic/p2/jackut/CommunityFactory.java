package br.ufal.ic.p2.jackut;

/**
 * Fábrica para criar instâncias de comunidades.
 */
public class CommunityFactory {
    /**
     * Cria uma nova comunidade.
     *
     * @param name        Nome da comunidade.
     * @param description Descrição da comunidade.
     * @param owner       Dono da comunidade.
     * @return Instância de `Community`.
     */
    public static Community createCommunity(String name, String description, String owner) {
        return new Community(name, description, owner);
    }
}