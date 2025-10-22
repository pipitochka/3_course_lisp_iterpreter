package com.example.minilisp.enviroment;

import com.example.minilisp.parser.Expression;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

public class Environment {

    private final HashMap<String, Expression> variables = new HashMap<>();

    private final Environment parent;

    public Environment() {
        this.parent = null;
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public void define(String name, Expression value) {
        if (variables.containsKey(name)) {
            throw new RuntimeException("Variable already defined: " + name);
        }
        variables.put(name, value);
    }

    public void set(String name, Expression value) {
        if (variables.containsKey(name)) {
            variables.put(name, value);
        } else if (parent != null) {
            parent.set(name, value);
        } else {
            throw new RuntimeException("Undefined variable: " + name);
        }
    }

    public Expression get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else if (parent != null) {
            return parent.get(name);
        } else {
            throw new RuntimeException("Undefined variable: " + name);
        }
    }

    public boolean contains(String name) {
        if (variables.containsKey(name)) return true;
        if (parent != null) return parent.contains(name);
        return false;
    }

}
