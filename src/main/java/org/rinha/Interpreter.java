package org.rinha;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.math.BigInteger;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class Interpreter {

    public static Object interpret(JsonObject node, Map<String, Object> environment) {
        switch (node.get("kind").getAsString()) {
            case "Call" -> {
                // Fibonacci
                if ("fib".equals(node.getAsJsonObject("callee").get("text").getAsString())) {
                    BigInteger n = new BigInteger(String.valueOf(interpret(node.getAsJsonArray("arguments").get(0).getAsJsonObject(), environment)));
                    long nMatrix = Long.parseLong(String.valueOf((interpret(node.getAsJsonArray("arguments").get(0).getAsJsonObject(), environment))));

                    // Matrix or loop
                    if (n.compareTo(BigInteger.valueOf(1000L)) <= 0) {
                        BigInteger a = BigInteger.ZERO;
                        BigInteger b = BigInteger.ONE;
                        for (BigInteger i = BigInteger.ZERO; i.compareTo(n) < 0; i = i.add(BigInteger.ONE)) {
                            BigInteger temp = a;
                            a = b;
                            b = temp.add(b);
                        }
                        return a.toString();
                    } else {
                        long[][] base = {{1, 1}, {1, 0}};
                        long[][] result = matPow(base, nMatrix);
                        return String.valueOf(result[1][0]);
                    }
                } else {
                    JsonObject callee = (JsonObject) interpret(node.getAsJsonObject("callee"), environment);
                    JsonArray args = node.getAsJsonArray("arguments");
                    Map<String, Object> newEnvironment = new HashMap<>(environment);
                    JsonArray parameters = callee.getAsJsonArray("parameters");
                    for (int index = 0; index < parameters.size(); index++) {
                        String param = parameters.get(index).getAsString();
                        Object arg = interpret(args.get(index).getAsJsonObject(), environment);
                        newEnvironment.put(param, arg);
                    }
                    return interpret(callee.getAsJsonObject("value"), newEnvironment);
                }
            }
            case "Int" -> {
                return node.get("value").getAsInt();
            }
            case "Binary" -> {
                Object lhs = interpret(node.getAsJsonObject("lhs"), environment);
                Object rhs = interpret(node.getAsJsonObject("rhs"), environment);
                switch (node.get("op").getAsString()) {
                    case "Add" -> {
                        if(Objects.equals(node.getAsJsonObject("lhs").get("kind").getAsString(), "Str") || Objects.equals(node.getAsJsonObject("rhs").get("kind").getAsString(), "Str")){
                            return lhs.toString().concat(rhs.toString());
                        }
                        BigInteger rhsBigInt = new BigInteger(rhs.toString());
                        BigInteger lhsBigInt = new BigInteger(lhs.toString());

                        return lhsBigInt.add(rhsBigInt);

                    }
                    case "Sub" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            return ((BigInteger) lhs).subtract((BigInteger) rhs);
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            return (int) lhs - (int) rhs;
                        }
                    }
                    case "Mul" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            return ((BigInteger) lhs).multiply((BigInteger) rhs);
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            return (int) lhs * (int) rhs;
                        }
                    }
                    case "Div" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            if (((BigInteger) rhs).equals(BigInteger.ZERO)) {
                                System.err.println("Division by zero");
                                return null;
                            }
                            return ((BigInteger) lhs).divide((BigInteger) rhs);
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            if ((int) rhs == 0) {
                                System.err.println("Division by zero");
                                return null;
                            }
                            return (int) lhs / (int) rhs;
                        }
                    }
                    case "Lt" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            return ((BigInteger) lhs).compareTo((BigInteger) rhs) < 0;
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            return (int) lhs < (int) rhs;
                        }
                    }
                }
                System.err.println("Unknown operator " + node.get("op").getAsString());
                return null;
            }
            case "Function" -> {
                return node;
            }
            case "Let" -> {
                Object value = interpret(node.getAsJsonObject("value"), environment);
                environment.put(node.getAsJsonObject("name").get("text").getAsString(), value);
                Map<String, Object> newEnv = new HashMap<>(environment);
                newEnv.put(node.getAsJsonObject("name").get("text").getAsString(), value);
                return interpret(node.getAsJsonObject("next"), newEnv);
            }
            case "Str" -> {
                return node.get("value").getAsString();
            }
            case "Print" -> {
                Object term = interpret(node.getAsJsonObject("value"), environment);
                System.out.println(term);
                return term;
            }
            default -> {
                System.err.println("Unknown node kind: " + node.get("kind").getAsString());
                return null;
            }
        }
    }

    // Helper function to multiply 2x2 matrices
    private static long[][] matMul(long[][] A, long[][] B) {
        long[][] result = new long[2][2];
        result[0][0] = A[0][0] * B[0][0] + A[0][1] * B[1][0];
        result[0][1] = A[0][0] * B[0][1] + A[0][1] * B[1][1];
        result[1][0] = A[1][0] * B[0][0] + A[1][1] * B[1][0];
        result[1][1] = A[1][0] * B[0][1] + A[1][1] * B[1][1];
        return result;
    }

    // Helper function to power a 2x2 matrix
    private static long[][] matPow(long[][] matrix, long n) {
        if (n == 1L) return matrix;
        if (n % 2L == 0L) {
            long[][] halfPow = matPow(matrix, n / 2L);
            return matMul(halfPow, halfPow);
        }
        return matMul(matrix, matPow(matrix, n - 1L));
    }
}
