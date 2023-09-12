package org.rinha;

import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.rinha.Interpreter.interpret;

public class Execute {

    public static Object executeRinhaCode(String filePath, Map<String, Object> initialEnvironment) {
        try {
            // Read the AST
            String rawData = Files.readString(Path.of(filePath));

            // Convert rawData into JsonObject
            JsonObject ast = new JsonParser().parse(rawData).getAsJsonObject();

            // Create a new env
            Map<String, Object> environment = new HashMap<>(initialEnvironment);

            // Call the interpret passing the AST with the new env
            return interpret(ast.getAsJsonObject("expression"), environment);
        } catch (IOException e) {
            System.err.println("Erro ao executar o código da rinha: " + e.getMessage());
            return null;
        }
    }
}
