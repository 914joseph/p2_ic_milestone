package br.ufal.ic.p2.jackut;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Community implements Serializable {
    private String name;
    private String description;
    private String owner;
    private List<String> members;

    public Community(String name, String description, String owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.members = new ArrayList<>();
        this.members.add(owner);
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
        }
    }
}