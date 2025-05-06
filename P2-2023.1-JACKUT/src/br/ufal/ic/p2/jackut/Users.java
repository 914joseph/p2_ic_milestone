package br.ufal.ic.p2.jackut;

import java.io.Serializable;
import java.util.*;

import br.ufal.ic.p2.jackut.exceptions.*;

/**
 * Classe que representa um usuário no sistema Jackut.
 * 
 * Gerencia informações do perfil, amigos, comunidades, mensagens, ídolos, paqueras e inimigos.
 */
public class Users implements Serializable, Observer {
    private String login;
    private String password;
    private String name;
    private List<String> friends;
    private Queue<String> messages;
    private Map<String, String> attributes;
    private List<String> pendingFriendRequests;
    private List<String> communities;
    private List<String> idols;
    private List<String> crushes;
    private List<String> enemies;

    /**
     * Construtor da classe Users.
     *
     * @param login    Login do usuário.
     * @param password Senha do usuário.
     * @param name     Nome do usuário.
     */
    public Users(String login, String password, String name) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.friends = new ArrayList<>();
        this.messages = new LinkedList<>();
        this.attributes = new HashMap<>();
        this.pendingFriendRequests = new ArrayList<>();
        this.communities = new ArrayList<>();
        this.idols = new ArrayList<>();
        this.crushes = new ArrayList<>();
        this.enemies = new ArrayList<>();
    }

    /**
     * Adiciona um amigo à lista de amigos do usuário.
     *
     * @param friend Login do amigo a ser adicionado.
     */
    public void addFriend(String friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
        }
    }

    /**
     * Verifica se o usuário é amigo de outro usuário.
     *
     * @param friend Login do amigo a ser verificado.
     * @return {@code true} se forem amigos, {@code false} caso contrário.
     */
    public boolean isFriend(String friend) {
        return friends.contains(friend);
    }

    /**
     * Adiciona um pedido de amizade pendente.
     *
     * @param friend Login do amigo que enviou o pedido.
     */
    public void addFriendRequest(String friend) {
        if (!pendingFriendRequests.contains(friend)) {
            pendingFriendRequests.add(friend);
        }
    }

    /**
     * Verifica se há um pedido de amizade pendente de um amigo específico.
     *
     * @param friend Login do amigo a ser verificado.
     * @return {@code true} se houver um pedido pendente, {@code false} caso
     *         contrário.
     */
    public boolean hasPendingRequest(String friend) {
        return pendingFriendRequests.contains(friend);
    }

    /**
     * Aceita um pedido de amizade pendente.
     *
     * @param friend Login do amigo cujo pedido será aceito.
     */
    public void acceptFriendRequest(String friend) {
        if (pendingFriendRequests.remove(friend)) {
            addFriend(friend);
        }
    }

    /**
     * Retorna a lista de amigos do usuário.
     *
     * @return Lista de amigos.
     */
    public List<String> getFriends() {
        return friends;
    }

    /**
     * Retorna a lista de pedidos de amizade pendentes.
     *
     * @return Lista de pedidos de amizade pendentes.
     */
    public List<String> getPendingFriendRequests() {
        return pendingFriendRequests;
    }

    /**
     * Retorna o login do usuário.
     *
     * @return Login do usuário.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Retorna a senha do usuário.
     *
     * @return Senha do usuário.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retorna o nome do usuário.
     *
     * @return Nome do usuário.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome do usuário.
     *
     * @param name Novo nome do usuário.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retorna o valor de um atributo do perfil do usuário.
     *
     * @param attribute Nome do atributo.
     * @return Valor do atributo.
     * @throws AttributeNotFilledException Se o atributo não estiver preenchido.
     */
    public String getAttribute(String attribute) {
        if (!attributes.containsKey(attribute)) {
            throw new AttributeNotFilledException("Atributo não preenchido.");
        }
        return attributes.get(attribute);
    }

    /**
     * Define o valor de um atributo do perfil do usuário.
     *
     * @param attribute Nome do atributo.
     * @param value     Valor do atributo.
     */
    public void setAttribute(String attribute, String value) {
        attributes.put(attribute, value);
    }

    /**
     * Adiciona uma mensagem à fila de mensagens do usuário.
     *
     * @param message Conteúdo da mensagem.
     */
    public void addMessage(String message) {
        messages.add(message);
    }

    /**
     * Lê a próxima mensagem da fila de mensagens do usuário.
     *
     * @return Conteúdo da mensagem.
     * @throws MessageException Se não houver mensagens na fila.
     */
    public String readMessage() {
        if (messages.isEmpty()) {
            throw new MessageException("Não há recados.");
        }
        return messages.poll();
    }

    /**
     * Adiciona uma comunidade à lista de comunidades do usuário.
     *
     * @param communityName Nome da comunidade a ser adicionada.
     */
    public void addCommunity(String communityName) {
        if (!communities.contains(communityName)) {
            communities.add(communityName);
        }
    }

    /**
     * Remove uma comunidade da lista de comunidades do usuário.
     *
     * @param communityName Nome da comunidade a ser removida.
     */
    public void removeCommunity(String communityName) {
        communities.remove(communityName);
    }

    /**
     * Retorna a lista de comunidades do usuário.
     *
     * @return Lista de comunidades.
     */
    public List<String> getCommunities() {
        return Collections.unmodifiableList(communities);
    }

    /**
     * Remove mensagens enviadas por um usuário específico.
     *
     * @param senderLogin Login do remetente das mensagens a serem removidas.
     */
    public void removeMessagesFrom(String senderLogin) {
        messages.removeIf(message -> message.startsWith("Mensagem de " + senderLogin + ":"));
    }

    /**
     * Verifica se o usuário é fã de outro usuário.
     *
     * @param idol Login do ídolo a ser verificado.
     * @return {@code true} se for fã, {@code false} caso contrário.
     */
    public boolean isIdol(String idol) {
        return idols.contains(idol);
    }

    /**
     * Adiciona um ídolo à lista de ídolos do usuário.
     *
     * @param idol Login do ídolo a ser adicionado.
     */
    public void addIdol(String idol) {
        if (!idols.contains(idol)) {
            idols.add(idol);
        }
    }

    /**
     * Verifica se o usuário tem uma paixão por outro usuário.
     *
     * @param crush Login da paixão a ser verificada.
     * @return {@code true} se for paixão, {@code false} caso contrário.
     */
    public boolean isCrush(String crush) {
        return crushes.contains(crush);
    }

    /**
     * Adiciona uma paixão à lista de paixões do usuário.
     *
     * @param crush Login da paixão a ser adicionada.
     */
    public void addCrush(String crush) {
        if (!crushes.contains(crush)) {
            crushes.add(crush);
        }
    }

    /**
     * Verifica se o usuário é inimigo de outro usuário.
     *
     * @param enemy Login do inimigo a ser verificado.
     * @return {@code true} se for inimigo, {@code false} caso contrário.
     */
    public boolean isEnemy(String enemy) {
        return enemies.contains(enemy);
    }

    /**
     * Adiciona um inimigo à lista de inimigos do usuário.
     *
     * @param enemy Login do inimigo a ser adicionado.
     */
    public void addEnemy(String enemy) {
        if (!enemies.contains(enemy)) {
            enemies.add(enemy);
        }
    }

    @Override
    public void update(String message) {
        addMessage(message);
    }
}