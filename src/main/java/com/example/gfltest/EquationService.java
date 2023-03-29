package com.example.gfltest;


import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquationService {
    private final EquationRepository equationRepository;

    @SneakyThrows
    public Equation save(final Equation equation, final Double root) {
        log.info("Saving equation {}", equation.getId());
        equation.setRoot(root);
        validate(equation);
        return equationRepository.save(equation);
    }

    private static void validate(Equation equation) {
        if (equation.getRoot() == null) {
            validateWithoutRoot(equation.getEquation());
        } else {
            validateWithRoot(equation.getEquation(), equation.getRoot());
        }
    }

    @SneakyThrows
    private static void validateWithoutRoot(final String equation) {
        boolean isValid =  checkSigns(equation) && checkBrackets(equation);
        if (!isValid) {
            throw new ValidationException("Validation failed");
        }
    }

    @SneakyThrows
    private static void validateWithRoot(final String equation, final double root) {
        boolean isValid =  checkSigns(equation) && checkBrackets(equation) && checkRoot(equation, root);
        if (!isValid) {
            throw new ValidationException("Validation failed");
        }
    }

    private static boolean checkSigns(final String input) {
        final char[] arr = input.toCharArray();
        int count = 0;
        final Set<Character> signs = new HashSet<>(Arrays.asList('+', '-', '*', '/'));

        final Set<Character> numbers = new HashSet<>(Arrays.asList('0', '1','2','3','4','5','6','7','8','9'));

        for (int i = 0; i < arr.length; i++) {
            if (signs.contains(arr[i])) {
                count++;
            } else {
                count = 0;
            }

            if (arr[i] == '-' && count == 2 && numbers.contains(arr[i+1])) {
                count = 0;
            }

            if (count == 2) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkBrackets(String input) {
        final Stack<Character> stack = new Stack<>();

        try {
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c == '(') {
                    stack.push(c);
                } else if (c == ')') {
                    if (stack.peek() != '(') {
                        return false;
                    } else {
                        stack.pop();
                    }
                }
            }
        } catch (EmptyStackException e) {
            return false;
        }
        return stack.isEmpty();
    }


    private static boolean checkRoot(final String equation, final double root) throws ScriptException {
        String left = equation.split("=")[0];
        String right = equation.split("=")[1];

        final ScriptEngine nashornEngine = new NashornScriptEngineFactory().getScriptEngine();
        left = left.replace("x", String.valueOf(root));
        right = right.replace("x", String.valueOf(root));

        Object leftResult =  nashornEngine.eval(left);
        Object rightResult =  nashornEngine.eval(right);

        if (leftResult instanceof Integer) {
            leftResult = Double.valueOf(((Integer) leftResult).intValue());
        }

        if (rightResult instanceof Integer) {
            rightResult = Double.valueOf(((Integer) rightResult).intValue());
        }
        return leftResult.equals(rightResult);

    }
}
