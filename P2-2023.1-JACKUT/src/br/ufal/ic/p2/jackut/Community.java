package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.exceptions.NoMessagesException;

import java.io.Serializable;
import java.util.*;

/**
 * Classe que representa uma comunidade no sistema Jackut.
 */
public class Community implements Serializable {
    private String name;
    private String description;
    private String owner;
    private List<String> members;
    private Map<String, Queue<String>> messages;

    public Community(String name, String description, String owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.members = new ArrayList<>();
        this.members.add(owner);
        this.messages = new HashMap<>();
        this.messages.put(owner, new LinkedList<>());
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public List<String> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public void addMember(String member) {
        if (!members.contains(member)) {
            members.add(member);
            messages.put(member, new LinkedList<>());
        }
    }

    public void addMessage(String sender, String content) {
        for (String member : members) {
            messages.get(member).add("Mensagem de " + sender + ": " + content);
        }
    }

    public String readMessage(String member) {
        Queue<String> memberMessages = messages.get(member);
        if (memberMessages == null || memberMessages.isEmpty()) {
            throw new NoMessagesException();
        }
        return memberMessages.poll();
    }

    public void removeMember(String memberLogin) {
        if (members.contains(memberLogin)) {
            members.remove(memberLogin);
            messages.remove(memberLogin);
        }
    }
}