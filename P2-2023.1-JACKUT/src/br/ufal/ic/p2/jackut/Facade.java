package br.ufal.ic.p2.jackut;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Facade {
    private static final String DATA_FILE = "users.dat";
    private Map<String, Users> users;
    private Map<String, String> sessions;
    private int sessionCounter;

    public Facade() {
        users = new HashMap<>();
        sessions = new HashMap<>();
        sessionCounter = 0;
        loadData(); // Garante que os dados sejam carregados ao iniciar o sistema
    }

    public void resetSystem() {
        users.clear();
        sessions.clear();
        sessionCounter = 0;
        saveData();
    }

    public void createUser(String login, String password, String name) {
        validateLogin(login);
        validatePassword(password);

        if (users.containsKey(login)) {
            throw new RuntimeException("Uma conta com este login já existe.");
        }

        Users newUser = new Users(login, password, name);
        users.put(login, newUser);
        saveData();
    }

    private void validateLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new RuntimeException("Login inválido.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Senha inválida.");
        }
    }

    public String openSession(String login, String password) {
        if (login == null || login.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                !users.containsKey(login) ||
                !users.get(login).getPassword().equals(password)) {
            throw new RuntimeException("Login ou senha inválidos.");
        }

        String sessionId = "session" + (++sessionCounter);
        sessions.put(sessionId, login);
        return sessionId;
    }

    public void addFriend(String sessionId, String friendLogin) {
        if (!users.containsKey(friendLogin)) {
            throw new RuntimeException("Usuário não cadastrado.");
        }
        if (!sessions.containsKey(sessionId)) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        String login = sessions.get(sessionId);
        Users user = users.get(login);

        if (login.equals(friendLogin)) {
            throw new RuntimeException("Usuário não pode adicionar a si mesmo como amigo.");
        }

        Users friend = users.get(friendLogin);

        if (user.isFriend(friendLogin)) {
            throw new RuntimeException("Usuário já está adicionado como amigo.");
        }

        if (user.hasPendingRequest(friendLogin)) {
            throw new RuntimeException("Usuário já está adicionado como amigo, esperando aceitação do convite.");
        }

        if (friend.hasPendingRequest(login)) {
            friend.acceptFriendRequest(login);
            user.addFriend(friendLogin);
        } else {
            friend.addFriendRequest(login);
        }

        saveData();
    }

    public boolean isFriend(String login, String friendLogin) {
        if (!users.containsKey(login)) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        Users user = users.get(login);
        return user.isFriend(friendLogin);
    }

    public String getFriends(String login) {
        if (!users.containsKey(login)) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        Users user = users.get(login);
        return String.join(",", user.getFriends());
    }

    public String getUserAttribute(String login, String attribute) {
        if (!users.containsKey(login)) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        Users user = users.get(login);

        // Verifica se o atributo solicitado foi preenchido
        if (attribute.equalsIgnoreCase("name")) {
            return user.getName();
        }

        try {
            return user.getAttribute(attribute);
        } catch (RuntimeException e) {
            throw new RuntimeException("Atributo não preenchido.");
        }
    }

    public void editProfile(String sessionId, String attribute, String value) {
        if (!sessions.containsKey(sessionId)) {
            throw new RuntimeException("Sessão inválida.");
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

    public void sendMessage(String sessionId, String recipientLogin, String message) {
        if (!sessions.containsKey(sessionId)) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        String senderLogin = sessions.get(sessionId);

        if (senderLogin.equals(recipientLogin)) {
            throw new RuntimeException("Usuário não pode enviar recado para si mesmo.");
        }

        if (!users.containsKey(recipientLogin)) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        Users recipient = users.get(recipientLogin);
        recipient.addMessage(message);
        saveData();
    }

    public String readMessage(String sessionId) {
        if (!sessions.containsKey(sessionId)) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        String login = sessions.get(sessionId);
        Users user = users.get(login);

        return user.readMessage();
    }

    public void closeSystem() {
        saveData();
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Erro ao salvar os dados: " + e.getMessage());
        }
    }

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
