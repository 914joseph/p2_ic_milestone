package br.ufal.ic.p2.jackut;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Users implements Serializable {
    private String login;
    private String password;
    private String name;
    private List<String> friends;
    private Queue<String> messages;

    public Users(String login, String password, String name) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.friends = new ArrayList<>();
        this.messages = new LinkedList<>();
    }

    public void addFriend(String friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
        }
    }

    public boolean isFriend(String friend) {
        return friends.contains(friend);
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

    public List<String> getFriends() {
        return friends;
    }
}