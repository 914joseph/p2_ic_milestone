package br.ufal.ic.p2.jackut;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import br.ufal.ic.p2.jackut.exceptions.*;

/**
 * Classe Facade que gerencia as operações principais do sistema Jackut.
 */
public class Facade {
    private static final String DATA_FILE = "users.dat";
    private Map<String, Users> users;
    private Map<String, String> sessions;
    private int sessionCounter;

    /**
     * Construtor da classe Facade.
     * Inicializa os mapas de usuários e sessões e carrega os dados persistidos.
     */
    public Facade() {
        users = new HashMap<>();
        sessions = new HashMap<>();
        sessionCounter = 0;
        loadData();
    }

    /**
     * Reseta o sistema, limpando todos os dados de usuários e sessões.
     */
    public void resetSystem() {
        users.clear();
        sessions.clear();
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

        Users newUser = new Users(login, password, name);
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
     * Lê a próxima mensagem do usuário.
     *
     * @param sessionId ID da sessão do usuário.
     * @return Conteúdo da mensagem.
     * @throws UserNotFoundException Se a sessão não for encontrada.
     * @throws MessageException      Se não houver mensagens para ler.
     */
    public String readMessage(String sessionId) {
        if (!sessions.containsKey(sessionId)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        String login = sessions.get(sessionId);
        Users user = users.get(login);

        return user.readMessage();
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
    }
}
