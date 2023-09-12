package org.rinha;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Object> emptyEnv = new HashMap<>();
        Execute.executeRinhaCode("src/main/resources/source.rinha.json", emptyEnv);
    }
}