package br.ufal.ic.p2.jackut;

import java.io.Serializable;
import java.util.*;

public class Users implements Serializable {
    private String login;
    private String password;
    private String name;
    private List<String> friends;
    private Queue<String> messages;
    private Map<String, String> attributes; // Mapa para atributos personalizados
    private List<String> pendingFriendRequests; // Lista de convites pendentes

    public Users(String login, String password, String name) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.friends = new ArrayList<>();
        this.messages = new LinkedList<>();
        this.attributes = new HashMap<>();
        this.pendingFriendRequests = new ArrayList<>();
    }

    public void addFriend(String friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
        }
    }

    public boolean isFriend(String friend) {
        return friends.contains(friend);
    }

    public void addFriendRequest(String friend) {
        if (!pendingFriendRequests.contains(friend)) {
            pendingFriendRequests.add(friend);
        }
    }

    public boolean hasPendingRequest(String friend) {
        return pendingFriendRequests.contains(friend);
    }

    public void acceptFriendRequest(String friend) {
        if (pendingFriendRequests.remove(friend)) {
            addFriend(friend);
        }
    }

    public List<String> getFriends() {
        return friends;
    }

    public List<String> getPendingFriendRequests() {
        return pendingFriendRequests;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttribute(String attribute) {
        if (!attributes.containsKey(attribute)) {
            throw new RuntimeException("Atributo não preenchido.");
        }
        return attributes.get(attribute);
    }

    public void setAttribute(String attribute, String value) {
        attributes.put(attribute, value);
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public String readMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.poll();
    }
}