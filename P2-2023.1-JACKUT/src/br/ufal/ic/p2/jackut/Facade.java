package br.ufal.ic.p2.jackut;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufal.ic.p2.jackut.exceptions.*;

/**
 * Classe Facade que gerencia as operações principais do sistema Jackut.
 * 
 * Esta classe centraliza todas as funcionalidades do sistema, como criação de usuários,
 * gerenciamento de comunidades, envio de mensagens, relacionamentos e remoção de contas.
 */
public class Facade {
    private static final String DATA_FILE = "users.dat";
    private Map<String, Users> users;
    private Map<String, String> sessions;
    private Map<String, Community> communities;
    private int sessionCounter;

    /**
     * Construtor da classe Facade.
     * Inicializa os mapas de usuários, comunidades e sessões e carrega os dados persistidos.
     */
    public Facade() {
        users = new HashMap<>();
        sessions = new HashMap<>();
        communities = new HashMap<>();
        sessionCounter = 0;
        loadData();
    }

    /**
     * Reseta o sistema, limpando todos os dados de usuários, comunidades e sessões.
     */
    public void resetSystem() {
        users.clear();
        sessions.clear();
        communities.clear();
        sessionCounter = 0;
        saveData();
    }

    /**
     * Cria um novo usuário no sistema.
     *
     * @param login    Login do usuário.
     * @param password Senha do usuário.
     * @param name     Nome do usuário.
     * @throws InvalidLoginException    Se o login for inválido ou já existir.
     * @throws InvalidPasswordException Se a senha for inválida.
     */
    public void createUser(String login, String password, String name) {
        validateLogin(login);
        validatePassword(password);

        if (users.containsKey(login)) {
            throw new InvalidLoginException("Uma conta com este login já existe.");
        }

        Users newUser = UserFactory.createUser(login, password, name);
        users.put(login, newUser);
        saveData();
    }

    /**
     * Valida o login do usuário.
     *
     * @param login Login do usuário.
     * @throws InvalidLoginException Se o login for nulo ou vazio.
     */
    private void validateLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new InvalidLoginException("Login inválido.");
        }
    }

    /**
     * Valida a senha do usuário.
     *
     * @param password Senha do usuário.
     * @throws InvalidPasswordException Se a senha for nula ou vazia.
     */
    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidPasswordException("Senha inválida.");
        }
    }

    /**
     * Abre uma sessão para o usuário.
     *
     * @param login    Login do usuário.
     * @param password Senha do usuário.
     * @return O ID da sessão criada.
     * @throws InvalidLoginException Se o login ou senha forem inválidos.
     */
    public String openSession(String login, String password) {
        if (login == null || login.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                !users.containsKey(login) ||
                !users.get(login).getPassword().equals(password)) {
            throw new InvalidLoginException("Login ou senha inválidos.");
        }

        String sessionId = "session" + (++sessionCounter);
        sessions.put(sessionId, login);
        return sessionId;
    }

    /**
     * Adiciona um amigo para o usuário.
     *
     * @param sessionId   ID da sessão do usuário.
     * @param friendLogin Login do amigo a ser adicionado.
     * @throws UserNotFoundException Se o usuário ou amigo não forem encontrados.
     * @throws FriendshipException   Se o usuário já for amigo ou se estiver
     *                               aguardando aceitação.
     */
    public void addFriend(String sessionId, String friendLogin) {
        if (!users.containsKey(friendLogin)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }
        if (!sessions.containsKey(sessionId)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        String login = sessions.get(sessionId);
        Users user = users.get(login);

        if (login.equals(friendLogin)) {
            throw new FriendshipException("Usuário não pode adicionar a si mesmo como amigo.");
        }

        Users friend = users.get(friendLogin);

        if (user.isFriend(friendLogin)) {
            throw new FriendshipException("Usuário já está adicionado como amigo.");
        }

        if (user.hasPendingRequest(friendLogin)) {
            throw new FriendshipException("Usuário já está adicionado como amigo, esperando aceitação do convite.");
        }

        if (friend.hasPendingRequest(login)) {
            friend.acceptFriendRequest(login);
            user.addFriend(friendLogin);
        } else {
            friend.addFriendRequest(login);
        }

        saveData();
    }

    /**
     * Verifica se dois usuários são amigos.
     *
     * @param login       Login do usuário.
     * @param friendLogin Login do amigo.
     * @return {@code true} se forem amigos, {@code false} caso contrário.
     * @throws UserNotFoundException Se o usuário não for encontrado.
     */
    public boolean isFriend(String login, String friendLogin) {
        if (!users.containsKey(login)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        Users user = users.get(login);
        return user.isFriend(friendLogin);
    }

    /**
     * Retorna a lista de amigos de um usuário.
     *
     * @param login Login do usuário.
     * @return Lista de amigos como uma string separada por vírgulas.
     * @throws UserNotFoundException Se o usuário não for encontrado.
     */
    public String getFriends(String login) {
        if (!users.containsKey(login)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        Users user = users.get(login);
        return String.join(",", user.getFriends());
    }

    /**
     * Obtém um atributo do perfil do usuário.
     *
     * @param login     Login do usuário.
     * @param attribute Nome do atributo.
     * @return Valor do atributo.
     * @throws UserNotFoundException       Se o usuário não for encontrado.
     * @throws AttributeNotFilledException Se o atributo não estiver preenchido.
     */
    public String getUserAttribute(String login, String attribute) {
        if (!users.containsKey(login)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        Users user = users.get(login);

        if (attribute.equalsIgnoreCase("name")) {
            throw new AttributeNotFilledException("Atributo não preenchido.");
        }

        try {
            return user.getAttribute(attribute);
        } catch (AttributeNotFilledException e) {
            throw new AttributeNotFilledException("Atributo não preenchido.");
        }
    }

    /**
     * Edita o perfil do usuário.
     *
     * @param sessionId ID da sessão do usuário.
     * @param attribute Nome do atributo a ser editado.
     * @param value     Novo valor do atributo.
     * @throws UserNotFoundException Se a sessão não for encontrada.
     */
    public void editProfile(String sessionId, String attribute, String value) {
        if (!sessions.containsKey(sessionId)) {
            throw new UserNotFoundException("Sessão inválida.");
        }

        String login = sessions.get(sessionId);
        Users user = users.get(login);

        if (attribute.equalsIgnoreCase("name")) {
            user.setName(value);
        } else {
            user.setAttribute(attribute, value);
        }
        saveData();
    }

    /**
     * Envia uma mensagem para outro usuário.
     *
     * @param sessionId      ID da sessão do remetente.
     * @param recipientLogin Login do destinatário.
     * @param message        Conteúdo da mensagem.
     * @throws UserNotFoundException Se o remetente ou destinatário não forem
     *                               encontrados.
     * @throws MessageException      Se o remetente tentar enviar uma mensagem para
     *                               si mesmo.
     */
    public void sendMessage(String sessionId, String recipientLogin, String message) {
        if (!sessions.containsKey(sessionId)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        String senderLogin = sessions.get(sessionId);

        if (senderLogin.equals(recipientLogin)) {
            throw new MessageException("Usuário não pode enviar recado para si mesmo.");
        }

        if (!users.containsKey(recipientLogin)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        Users recipient = users.get(recipientLogin);
        recipient.addMessage(message);
        saveData();
    }

    /**
     * Envia uma mensagem para uma comunidade.
     *
     * @param sessionId     ID da sessão do usuário.
     * @param communityName Nome da comunidade.
     * @param message       Conteúdo da mensagem.
     * @throws UserNotFoundException          Se o usuário não for encontrado.
     * @throws UserNotInCommunityException    Se o usuário não for membro da comunidade.
     */
    public void sendMessageToCommunity(String sessionId, String communityName, String message) {
        if (!sessions.containsKey(sessionId)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        String sender = sessions.get(sessionId);
        Community community = getCommunity(communityName);

        if (!community.getMembers().contains(sender)) {
            throw new UserNotInCommunityException();
        }

        community.addMessage(sender, message);
        saveData();
    }

    /**
     * Lê a próxima mensagem do usuário.
     *
     * @param sessionId ID da sessão do usuário.
     * @return Conteúdo da mensagem.
     * @throws UserNotFoundException Se a sessão não for encontrada.
     * @throws NoMessagesException   Se não houver mensagens para o usuário.
     */
    public String readMessage(String sessionId) {
        if (!sessions.containsKey(sessionId)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        String user = sessions.get(sessionId);
        for (Community community : communities.values()) {
            if (community.getMembers().contains(user)) {
                try {
                    return community.readMessage(user);
                } catch (NoMessagesException ignored) {
                    // Continue procurando em outras comunidades
                }
            }
        }
        throw new NoMessagesException();
    }

    /**
     * Cria uma nova comunidade no sistema.
     *
     * @param sessionId   ID da sessão do usuário.
     * @param name        Nome da comunidade.
     * @param description Descrição da comunidade.
     * @throws CommunityAlreadyExistsException Se a comunidade já existir.
     */
    public void createCommunity(String sessionId, String name, String description) {
        if (communities.containsKey(name)) {
            throw new CommunityAlreadyExistsException();
        }

        String owner = getSessionUser(sessionId);
        Community community = CommunityFactory.createCommunity(name, description, owner);
        communities.put(name, community);
        saveData();
    }

    /**
     * Adiciona um usuário a uma comunidade.
     *
     * @param sessionId     ID da sessão do usuário.
     * @param communityName Nome da comunidade.
     * @throws UserNotFoundException          Se o usuário não for encontrado.
     * @throws CommunityNotFoundException     Se a comunidade não for encontrada.
     * @throws UserAlreadyInCommunityException Se o usuário já estiver na comunidade.
     */
    public void addUserToCommunity(String sessionId, String communityName) {
        if (!sessions.containsKey(sessionId)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        String userLogin = sessions.get(sessionId);
        Community community = getCommunity(communityName);

        if (community.getMembers().contains(userLogin)) {
            throw new UserAlreadyInCommunityException();
        }

        Users user = users.get(userLogin);
        community.addMember(userLogin);
        community.addObserver(user);
        user.addCommunity(communityName);
        saveData();
    }

    /**
     * Obtém as comunidades de um usuário.
     *
     * @param login Login do usuário.
     * @return Comunidades do usuário como uma string separada por vírgulas.
     * @throws UserNotFoundException Se o usuário não for encontrado.
     */
    public String getUserCommunities(String login) {
        if (!users.containsKey(login)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        Users user = users.get(login);
        List<String> userCommunities = user.getCommunities();

        if (userCommunities.isEmpty()) {
            return "{}";
        }
        return "{" + String.join(",", userCommunities) + "}";
    }

    /**
     * Obtém a descrição de uma comunidade.
     *
     * @param name Nome da comunidade.
     * @return Descrição da comunidade.
     * @throws CommunityNotFoundException Se a comunidade não for encontrada.
     */
    public String getCommunityDescription(String name) {
        Community community = getCommunity(name);
        return community.getDescription();
    }

    /**
     * Obtém o dono de uma comunidade.
     *
     * @param name Nome da comunidade.
     * @return Dono da comunidade.
     * @throws CommunityNotFoundException Se a comunidade não for encontrada.
     */
    public String getCommunityOwner(String name) {
        Community community = getCommunity(name);
        return community.getOwner();
    }

    /**
     * Obtém os membros de uma comunidade.
     *
     * @param name Nome da comunidade.
     * @return Membros da comunidade como uma string separada por vírgulas.
     * @throws CommunityNotFoundException Se a comunidade não for encontrada.
     */
    public String getCommunityMembers(String name) {
        Community community = getCommunity(name);
        return "{" + String.join(",", community.getMembers()) + "}";
    }

    /**
     * Obtém uma comunidade pelo nome.
     *
     * @param name Nome da comunidade.
     * @return A comunidade correspondente.
     * @throws CommunityNotFoundException Se a comunidade não for encontrada.
     */
    private Community getCommunity(String name) {
        if (!communities.containsKey(name)) {
            throw new CommunityNotFoundException();
        }
        return communities.get(name);
    }

    /**
     * Obtém o usuário associado a uma sessão.
     *
     * @param sessionId ID da sessão.
     * @return Login do usuário.
     */
    private String getSessionUser(String sessionId) {
        if (!sessions.containsKey(sessionId)) {
            throw new UserNotFoundException("Sessão inválida.");
        }
        return sessions.get(sessionId);
    }

    /**
     * Salva os dados do sistema em um arquivo.
     */
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Erro ao salvar os dados: " + e.getMessage());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("communities.dat"))) {
            oos.writeObject(communities);
        } catch (IOException e) {
            System.err.println("Erro ao salvar os dados: " + e.getMessage());
        }
    }

    /**
     * Carrega os dados do sistema de um arquivo.
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            users = (Map<String, Users>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar os dados: " + e.getMessage());
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("communities.dat"))) {
            communities = (Map<String, Community>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            communities = new HashMap<>();
        }
    }

    /**
     * Adiciona um ídolo para o usuário.
     *
     * @param sessionId ID da sessão do usuário.
     * @param idolLogin Login do ídolo.
     * @throws AutoIdolException           Se o usuário tentar se adicionar como ídolo.
     * @throws IdolAlreadyExistsException  Se o ídolo já estiver adicionado.
     * @throws InteractionWithEnemyException Se o ídolo for inimigo do usuário.
     */
    public void addIdol(String sessionId, String idolLogin) {
        String userLogin = getSessionUser(sessionId);
        Users user = users.get(userLogin);
        Users idol = users.get(idolLogin);

        if (userLogin.equals(idolLogin)) {
            throw new AutoIdolException();
        }

        if (user.isEnemy(idolLogin) || idol.isEnemy(userLogin)) {
            throw new InteractionWithEnemyException(idol.getName());
        }

        if (user.isIdol(idolLogin)) {
            throw new IdolAlreadyExistsException();
        }

        user.addIdol(idolLogin);
        saveData();
    }

    /**
     * Adiciona uma paquera para o usuário.
     *
     * @param sessionId  ID da sessão do usuário.
     * @param crushLogin Login da paquera.
     * @throws AutoCrushException          Se o usuário tentar se adicionar como paquera.
     * @throws CrushAlreadyExistsException Se a paquera já estiver adicionada.
     * @throws InteractionWithEnemyException Se a paquera for inimiga do usuário.
     */
    public void addCrush(String sessionId, String crushLogin) {
        String userLogin = getSessionUser(sessionId);
        Users user = users.get(userLogin);
        Users crush = users.get(crushLogin);

        if (userLogin.equals(crushLogin)) {
            throw new AutoCrushException();
        }

        if (user.isEnemy(crushLogin) || crush.isEnemy(userLogin)) {
            throw new InteractionWithEnemyException(crush.getName());
        }

        if (user.isCrush(crushLogin)) {
            throw new CrushAlreadyExistsException();
        }

        user.addCrush(crushLogin);

        if (crush.isCrush(userLogin)) {
            sendMessage("Jackut", crushLogin, user.getName() + " é seu paquera - Recado do Jackut.");
            sendMessage("Jackut", userLogin, crush.getName() + " é seu paquera - Recado do Jackut.");
        }

        saveData();
    }

    /**
     * Adiciona um inimigo para o usuário.
     *
     * @param sessionId  ID da sessão do usuário.
     * @param enemyLogin Login do inimigo.
     * @throws AutoEnemyException          Se o usuário tentar se adicionar como inimigo.
     * @throws EnemyAlreadyExistsException Se o inimigo já estiver adicionado.
     */
    public void addEnemy(String sessionId, String enemyLogin) {
        String userLogin = getSessionUser(sessionId);
        Users user = users.get(userLogin);
        

        if (userLogin.equals(enemyLogin)) {
            throw new AutoEnemyException();
        }

        if (user.isEnemy(enemyLogin)) {
            throw new EnemyAlreadyExistsException();
        }

        user.addEnemy(enemyLogin);
        saveData();
    }

    /**
     * Remove um usuário do sistema.
     *
     * @param sessionId ID da sessão do usuário.
     * @throws UserNotFoundException Se o usuário não for encontrado.
     */
    public void removeUser(String sessionId) {
        if (!sessions.containsKey(sessionId)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        String userLogin = sessions.get(sessionId);
        Users user = users.get(userLogin);

        // Remover o usuário de todas as comunidades
        for (Community community : communities.values()) {
            if (community.getOwner().equals(userLogin)) {
                communities.remove(community.getName());
            } else {
                community.removeMember(userLogin);
            }
        }

        // Remover mensagens enviadas pelo usuário
        for (Users otherUser : users.values()) {
            otherUser.removeMessagesFrom(userLogin);
        }

        // Remover o usuário do sistema
        users.remove(userLogin);
        sessions.remove(sessionId);

        saveData();
    }
}
