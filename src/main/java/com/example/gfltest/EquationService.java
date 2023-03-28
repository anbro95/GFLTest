package com.example.gfltest;


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
    private final RootRepository rootRepository;

    @SneakyThrows
    public Equation save(Equation equation, Double root) {
        log.info("Saving equation {}", equation.getId());
        if (root != null) {
            Root root1 = Root.builder().value(root).build();
            rootRepository.save(root1);
            equation.setRoot(root1);
        }
        boolean isValid = true;
        if (root == null) {
            isValid = validate(equation.getEquation());
        } else {
            isValid = validate(equation.getEquation(), root);
        }

        if (isValid) {
            log.info("Equation is valid");
            return equationRepository.save(equation);
        } else {
            log.info("Equation is NOT valid");
            return null;
        }
    }


    public static boolean validate(String equation) {
        return checkSigns(equation) && checkBrackets(equation);
    }

    public static boolean validate(String equation, double root) throws ScriptException {
        return checkSigns(equation) && checkBrackets(equation) && checkRoot(equation, root);
    }

    private static boolean checkSigns(String input) {
        char[] arr = input.toCharArray();
        int count = 0;
        Set<Character> signs = new HashSet<>(Arrays.asList('+', '-', '*', '/'));

        Set<Character> numbers = new HashSet<>(Arrays.asList('1','2','3','4','5','6','7','8','9'));

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
//        Stack<Character> stack = new Stack<>();
//        Map<Character, Character> map = new HashMap<>();
//        map.put('(', ')');
//
//
//        if (input.charAt(0) != '(') return false;
//
//
//        for(int i=0; i < input.length(); i++) {
//            char c = input.charAt(i);
//            if(stack.isEmpty() && c != '(') return false;
//            if(c == '(') {
//                stack.push(c);
//            }else if (c == ')'){
//                if(map.get(stack.peek()) != c) {
//                    return false;
//                }else {
//                    stack.pop();
//                }
//            }
//        }
//        return stack.isEmpty();

        return true;
    }


    private static boolean checkRoot(String equation, double root) throws ScriptException {
        String left = equation.split("=")[0];
        String right = equation.split("=")[1];

        ScriptEngine nashornEngine = new NashornScriptEngineFactory().getScriptEngine();
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
