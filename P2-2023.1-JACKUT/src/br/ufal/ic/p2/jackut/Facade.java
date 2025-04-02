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

    public String getUserAttribute(String login, String attribute) {
        if (!users.containsKey(login)) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        Users user = users.get(login);
        if (attribute.equalsIgnoreCase("name")) {
            return user.getName();
        }
        throw new RuntimeException("Atributo não encontrado.");
    }

    public void editProfile(String sessionId, String attribute, String value) {
        if (!sessions.containsKey(sessionId)) {
            throw new RuntimeException("Sessão inválida.");
        }

        String login = sessions.get(sessionId);
        Users user = users.get(login);

        switch (attribute.toLowerCase()) {
            case "name":
                user.setName(value);
                break;
            default:
                throw new RuntimeException("Atributo não encontrado.");
        }
        saveData();
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
