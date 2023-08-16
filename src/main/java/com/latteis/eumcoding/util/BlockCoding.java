// 입력된 코드블록으로 자바 코드를 생성하는 클래스

package com.latteis.eumcoding.util;

import java.io.*;
import java.util.*;

public class BlockCoding {

    private StringBuilder codeBuilder;
    private List<String> inputVariables;

    public BlockCoding() {
        codeBuilder = new StringBuilder();
        inputVariables = new ArrayList<>();
    }

    public void addVariableAssignment(String variableName, String value) {
        codeBuilder.append(variableName + " = " + value + ";\n");
    }

    public void addPrint(String value) {
        codeBuilder.append("System.out.println(" + value + ");\n");
    }

    public void addInput(String variableName) {
        codeBuilder.append("Scanner scanner = new Scanner(System.in);\n");
        codeBuilder.append(variableName + " = scanner.nextLine();\n");
        inputVariables.add(variableName);
    }

    public void addIfStatement(String condition, String... actions) {
        codeBuilder.append("if (" + condition + ") {\n");
        for (String action : actions) {
            codeBuilder.append("    " + action + ";\n");
        }
        codeBuilder.append("}\n");
    }

    public void addForLoop(String loopVariable, int start, int end, String... actions) {
        codeBuilder.append("for (int " + loopVariable + " = " + start + "; " + loopVariable + " < " + end + "; " + loopVariable + "++) {\n");
        for (String action : actions) {
            codeBuilder.append("    " + action + ";\n");
        }
        codeBuilder.append("}\n");
    }

    public String generateCode() {
        StringBuilder fullCode = new StringBuilder();
        fullCode.append("import java.util.Scanner;\n\n");
        fullCode.append("public class BlockProgram {\n");
        fullCode.append("    public static void main(String[] args) {\n");

        for (String var : inputVariables) {
            fullCode.append("    String " + var + ";\n");
        }

        fullCode.append(codeBuilder.toString());

        fullCode.append("    }\n");
        fullCode.append("}\n");

        return fullCode.toString();
    }

    public void printGeneratedCode() {
        System.out.println(generateCode());
    }

    // 입력값(들)-출력값 쌍의 테스트 오라클을 검증하기 위해 사용하는 함수
    public static boolean compileAndRun(String code, List<String> inputs, String expectedOutput) {
        try {
            // 코드 저장
            File sourceFile = new File("BlockProgram.java");
            try (PrintWriter out = new PrintWriter(new FileWriter(sourceFile))) {
                out.println(code);
            }

            // 컴파일
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", sourceFile.getPath());
            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor();

            // 실행
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "BlockProgram");
            runProcessBuilder.redirectErrorStream(true);
            Process runProcess = runProcessBuilder.start();

            // 입력 전달
            try (PrintWriter out = new PrintWriter(new OutputStreamWriter(runProcess.getOutputStream()))) {
                for (String input : inputs) {
                    out.println(input);
                }
            }

            // 출력 읽기
            StringBuilder outputBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputBuilder.append(line).append("\n");
                }
            }

            String actualOutput = outputBuilder.toString().trim();
            return expectedOutput.equals(actualOutput);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}