package com.latteis.eumcoding.util;

import java.io.*;
import java.util.*;

public class BlockCoding {

    private StringBuilder codeBuilder; // 생성된 자바 코드를 저장할 StringBuilder
    private List<String> inputVariables; // 입력 변수의 이름들을 저장할 리스트

    public BlockCoding() {
        codeBuilder = new StringBuilder(); // codeBuilder 초기화
        inputVariables = new ArrayList<>(); // inputVariables 초기화
    }

    // 변수 할당 코드를 추가하는 메서드
    public void addVariableAssignment(String variableName, String value) {
        codeBuilder.append(variableName + " = " + value + ";\n");
    }

    // 출력 코드를 추가하는 메서드
    public void addPrint(String value) {
        codeBuilder.append("System.out.println(" + value + ");\n");
    }

    // 사용자 입력 코드를 추가하고, 해당 입력 변수 이름을 저장하는 메서드
    public void addInput(String variableName) {
        codeBuilder.append("Scanner scanner = new Scanner(System.in);\n");
        codeBuilder.append(variableName + " = scanner.nextLine();\n");
        inputVariables.add(variableName); // 입력 변수 이름 저장
    }

    // if 문 코드를 추가하는 메서드
    public void addIfStatement(String condition, String... actions) {
        codeBuilder.append("if (" + condition + ") {\n");
        for (String action : actions) {
            codeBuilder.append("    " + action + ";\n");
        }
        codeBuilder.append("}\n");
    }

    // for 반복문 코드를 추가하는 메서드
    public void addForLoop(String loopVariable, int start, int end, String... actions) {
        codeBuilder.append("for (int " + loopVariable + " = " + start + "; " + loopVariable + " < " + end + "; " + loopVariable + "++) {\n");
        for (String action : actions) {
            codeBuilder.append("    " + action + ";\n");
        }
        codeBuilder.append("}\n");
    }

    // 현재까지 생성된 코드를 완전한 자바 프로그램 형식으로 변환하여 반환하는 메서드
    public String generateCode() {
        StringBuilder fullCode = new StringBuilder();
        fullCode.append("import java.util.Scanner;\n\n"); // Scanner 클래스를 사용하기 위한 import
        fullCode.append("public class BlockProgram {\n"); // 클래스 선언 시작
        fullCode.append("    public static void main(String[] args) {\n"); // main 메서드 선언 시작

        // 저장된 입력 변수들을 선언하는 부분
        for (String var : inputVariables) {
            fullCode.append("    String " + var + ";\n");
        }

        // 실제 생성된 코드를 추가
        fullCode.append(codeBuilder.toString());

        fullCode.append("    }\n"); // main 메서드 선언 종료
        fullCode.append("}\n"); // 클래스 선언 종료

        return fullCode.toString();
    }

    // 현재까지 생성된 코드를 출력하는 메서드
    public void printGeneratedCode() {
        System.out.println(generateCode());
    }

    // 생성된 자바 코드를 컴파일하고 실행한 후, 예상 출력값과 실제 출력값을 비교하여 일치하는지 검증하는 메서드
    public static boolean compileAndRun(String code, List<String> inputs, String expectedOutput) {
        try {
            // 생성된 코드를 파일로 저장
            File sourceFile = new File("BlockProgram.java");
            try (PrintWriter out = new PrintWriter(new FileWriter(sourceFile))) {
                out.println(code);
            }

            // 저장된 파일을 컴파일
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", sourceFile.getPath());
            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor();

            // 컴파일된 클래스 파일을 실행
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "BlockProgram");
            runProcessBuilder.redirectErrorStream(true);
            Process runProcess = runProcessBuilder.start();

            // 실행 프로세스에 입력값을 전달
            try (PrintWriter out = new PrintWriter(new OutputStreamWriter(runProcess.getOutputStream()))) {
                for (String input : inputs) {
                    out.println(input);
                }
            }

            // 프로세스의 출력값을 읽어와 StringBuilder에 저장
            StringBuilder outputBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputBuilder.append(line).append("\n");
                }
            }

            // 예상 출력값과 실제 출력값을 비교하여 일치하는지 확인
            String actualOutput = outputBuilder.toString().trim();
            return expectedOutput.equals(actualOutput);

        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
            return false;
        }
    }
}