package org.rinha;

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

                        BigInteger[][] base = {
                                {BigInteger.ONE, BigInteger.ONE},
                                {BigInteger.ONE, BigInteger.ZERO}};

                        BigInteger[][] result = matPow(base, n);
                        return String.valueOf(result[1][0]);
                    }
                }
                else if ("sum".equals(node.getAsJsonObject("callee").get("text").getAsString())){

                    int args = Integer.parseInt(String.valueOf(interpret(node.getAsJsonArray("arguments").get(0).getAsJsonObject(), environment)));

                    int total = 0;
                    for (int i = 1; i <= args; i++) {
                        total += i;
                    }
                    return total;

                } else{

                    int n = Integer.parseInt(String.valueOf(interpret(node.getAsJsonArray("arguments").get(0).getAsJsonObject(), environment)));
                    int k = Integer.parseInt(String.valueOf(interpret(node.getAsJsonArray("arguments").get(1).getAsJsonObject(), environment)));

                    if (k < 0 || k > n) {
                        return 0;
                    }

                    int result = 1;

                    if (k > n - k) {
                        k = n - k;
                    }

                    for (int i = 0; i < k; i++) {
                        result *= (n - i);
                        result /= (i + 1);
                    }

                    return result;
                }
            }
            case "Int" -> {
                return node.get("value").getAsInt();
            }
            case "Var" -> {
                return node.get("text").getAsString();
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
                    case "Eq" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            return (lhs).equals(rhs);
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            return (int) lhs == (int) rhs;
                        }
                    }
                    case "Neq" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            return ((BigInteger) lhs).compareTo((BigInteger) rhs) != 0;
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            return (int) lhs != (int) rhs;
                        }
                    }
                    case "Gt" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            return ((BigInteger) lhs).compareTo((BigInteger) rhs) > 0;
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            return (int) lhs > (int) rhs;
                        }
                    }
                    case "Gte" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            return ((BigInteger) lhs).compareTo((BigInteger) rhs) >= 0;
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            return (int) lhs >= (int) rhs;
                        }
                    }
                    case "Lte" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            return ((BigInteger) lhs).compareTo((BigInteger) rhs) <= 0;
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            return (int) lhs <= (int) rhs;
                        }
                    }
                    case "And" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            return ((BigInteger) lhs).and((BigInteger) rhs);
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            return (int) lhs & (int) rhs;
                        }
                    }
                    case "Or" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            return ((BigInteger) lhs).or((BigInteger) rhs);
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            return (int) lhs | (int) rhs;
                        }
                    }
                    case "Rem" -> {
                        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
                            return ((BigInteger) lhs).divideAndRemainder((BigInteger) rhs);
                        } else if (lhs instanceof Integer && rhs instanceof Integer) {
                            return (int) lhs % (int) rhs;
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

    private static BigInteger[][] matMul(BigInteger[][] A, BigInteger[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;
        BigInteger[][] result = new BigInteger[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                result[i][j] = BigInteger.ZERO;
                for (int k = 0; k < colsA; k++) {
                    result[i][j] = result[i][j].add(A[i][k].multiply(B[k][j]));
                }
            }
        }

        return result;
    }

    private static BigInteger[][] matPow(BigInteger[][] matrix, BigInteger n) {
        if (n.equals(BigInteger.ONE)) return matrix;
        if (n.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            BigInteger[][] halfPow = matPow(matrix, n.divide(BigInteger.valueOf(2)));
            return matMul(halfPow, halfPow);
        }
        return matMul(matrix, matPow(matrix, n.subtract(BigInteger.ONE)));
    }
}
