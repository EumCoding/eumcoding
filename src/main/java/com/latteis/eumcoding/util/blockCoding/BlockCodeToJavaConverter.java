package com.latteis.eumcoding.util.blockCoding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Component
public class BlockCodeToJavaConverter {

    public String convertToJavaCode(List<Block> blocks) {
        int printCnt = 0; // print는 단 한번만 올 수 있음.

        StringBuilder javaCode = new StringBuilder();
        javaCode.append("public class Main {\n");
        javaCode.append("    public static void main(String[] args) {\n");

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            switch (block.getBlock()) {
                case "[numberVal]":
                    javaCode.append("int ").append(block.getValue()).append("");
                    // 그 다음 블럭이 [number]이면 그 값을 추가하고, for, if, print, StringVal, enter가 오면 해당 변수에 0을 대입. 단,  >=, >, ==, <, <=, !=, String, print, +, -, *, /, {, } 가 오면 예외처리
                    if (i + 1 < blocks.size()) {
                        Block nextBlock = blocks.get(i + 1);
                        if ("[number]".equals(nextBlock.getBlock())) {
                            javaCode.append(block.getValue()).append(" = ").append(nextBlock.getValue()).append(";\n");
                            i++; // 다음 블록을 처리했으므로 인덱스 증가
                            // 그 다음 블럭에 number, +, -, /, * 가 오지 않을 때까지 반복하고 그 다음 블럭이 [enter]이면 ;를 추가
                            while (i + 1 < blocks.size()) {
                                nextBlock = blocks.get(i + 1);
                                if ("[number]".equals(nextBlock.getBlock()) || "+".equals(nextBlock.getBlock()) || "-".equals(nextBlock.getBlock()) || "*".equals(nextBlock.getBlock()) || "/".equals(nextBlock.getBlock())) {
                                    javaCode.append(nextBlock.getValue());
                                    i++; // 다음 블록을 처리했으므로 인덱스 증가
                                } else if ("[enter]".equals(nextBlock.getBlock())) {
                                    javaCode.append(";\n");
                                    i++; // 다음 블록을 처리했으므로 인덱스 증가
                                    break;
                                } else {
                                    // 예외처리 - 몇번째 줄인지 알려줌
                                    return "convert error line : " + i;
                                }
                            }
                        } else if ("[for]".equals(nextBlock.getBlock()) || "[if]".equals(nextBlock.getBlock()) || "[print]".equals(nextBlock.getBlock()) || "[StringVal]".equals(nextBlock.getBlock()) || "[enter]".equals(nextBlock.getBlock())) {
                            javaCode.append(block.getValue()).append(" = 0;\n");
                        } else if (">=".equals(nextBlock.getBlock()) || ">".equals(nextBlock.getBlock()) || "==".equals(nextBlock.getBlock()) || "<".equals(nextBlock.getBlock()) || "<=".equals(nextBlock.getBlock()) || "!=".equals(nextBlock.getBlock()) || "[String]".equals(nextBlock.getBlock()) || "[print]".equals(nextBlock.getBlock()) || "+".equals(nextBlock.getBlock()) || "-".equals(nextBlock.getBlock()) || "*".equals(nextBlock.getBlock()) || "/".equals(nextBlock.getBlock()) || "{".equals(nextBlock.getBlock()) || "}".equals(nextBlock.getBlock())) {
                            // 예외처리 - 몇번째 줄인지 알려줌
                            return "convert error line : " + i;
                        }
                    }
                    break;
                case "[StringVal]":
                    javaCode.append("String ").append(block.getValue()).append("");
                    // 그 다음 블럭이 [String]이면 그 값을 추가하고, for, if, print, StringVal, enter가 오면 해당 변수에 0을 대입. 단,  >=, >, ==, <, <=, !=, number, print, +, -, *, /, {, } 가 오면 예외처리
                    if (i + 1 < blocks.size()) {
                        Block nextBlock = blocks.get(i + 1);
                        if ("[String]".equals(nextBlock.getBlock())) {
                            javaCode.append(block.getValue()).append(" = ").append(nextBlock.getValue()).append(";\n");
                            i++; // 다음 블록을 처리했으므로 인덱스 증가
                            // 그 다음 블럭에 String, + 가 오지 않을 때까지 반복하고 그 다음 블럭이 [enter]이면 ;를 추가
                            while (i + 1 < blocks.size()) {
                                nextBlock = blocks.get(i + 1);
                                if ("[String]".equals(nextBlock.getBlock()) || "+".equals(nextBlock.getBlock())) {
                                    javaCode.append(nextBlock.getValue());
                                    i++; // 다음 블록을 처리했으므로 인덱스 증가
                                } else if ("[enter]".equals(nextBlock.getBlock())) {
                                    javaCode.append(";\n");
                                    i++; // 다음 블록을 처리했으므로 인덱스 증가
                                    break;
                                } else {
                                    // 예외처리 - 몇번째 줄인지 알려줌
                                    return "convert error line : " + i;
                                }
                            }
                        } else if ("[for]".equals(nextBlock.getBlock()) || "[if]".equals(nextBlock.getBlock()) || "[print]".equals(nextBlock.getBlock()) || "[numberVal]".equals(nextBlock.getBlock()) || "[enter]".equals(nextBlock.getBlock())) {
                            javaCode.append(block.getValue()).append(" = 0;\n");
                        } else if (">=".equals(nextBlock.getBlock()) || ">".equals(nextBlock.getBlock()) || "==".equals(nextBlock.getBlock()) || "<".equals(nextBlock.getBlock()) || "<=".equals(nextBlock.getBlock()) || "!=".equals(nextBlock.getBlock()) || "[number]".equals(nextBlock.getBlock()) || "[print]".equals(nextBlock.getBlock()) || "+".equals(nextBlock.getBlock()) || "-".equals(nextBlock.getBlock()) || "*".equals(nextBlock.getBlock()) || "/".equals(nextBlock.getBlock()) || "{".equals(nextBlock.getBlock()) || "}".equals(nextBlock.getBlock())) {
                            // 예외처리 - 몇번째 줄인지 알려줌
                            return "convert error line : " + i;
                        }
                    }
                    break;
                case "[for]":
                    //javaCode.append("for (int i = 0; i < 2; i++) {\n");
                    javaCode.append("for (int i = 0; i < ");
                    //그 다음 블럭이 [number] 또는 [String]이면 그 값을 추가
                    if (i + 1 < blocks.size()) {
                        Block nextBlock = blocks.get(i + 1);
                        if ("[number]".equals(nextBlock.getBlock()) || "[String]".equals(nextBlock.getBlock())) {
                            javaCode.append(nextBlock.getValue());
                            i++; // 다음 블록을 처리했으므로 인덱스 증가
                        }
                    }
                    javaCode.append("; i++) \n");
                    break;
                case "[if]":
                    //javaCode.append("if (/* condition */) {\n");
                    javaCode.append("if (");
                    //그 다음 블럭이 [number] 또는 [String]이면 그 값을 추가
                    if (i + 1 < blocks.size()) {
                        Block nextBlock = blocks.get(i + 1);
                        if ("[number]".equals(nextBlock.getBlock()) || "[String]".equals(nextBlock.getBlock())) {
                            javaCode.append(nextBlock.getValue() + " "); // 값
                            // 그 다음 블럭이 [>, <, ==, !=]이면 그 값을 추가
                            if (i + 2 < blocks.size()) {
                                Block nextNextBlock = blocks.get(i + 2);
                                if (">".equals(nextNextBlock.getBlock()) || "<".equals(nextNextBlock.getBlock()) || "==".equals(nextNextBlock.getBlock()) || "!=".equals(nextNextBlock.getBlock())) {
                                    javaCode.append(nextNextBlock.getValue() + " "); // 연산자
                                    // 그 다음 블럭이 [numberVal] 또는 [StringVal]이면 그 변수명을 추가
                                    if (i + 3 < blocks.size()) {
                                        Block nextNextNextBlock = blocks.get(i + 3);
                                        if ("[numberVal]".equals(nextNextNextBlock.getBlock()) || "[StringVal]".equals(nextNextNextBlock.getBlock())) {
                                            javaCode.append(nextNextNextBlock.getValue() + ")"); // 변수명
                                            i++; // 다음 블록을 처리했으므로 인덱스 증가
                                        }else{
                                            // 예외처리 - 몇번째 줄인지 알려줌
                                            return "convert error line : " + i;
                                        }

                                    }
                                }else{
                                    // 예외처리
                                    return "convert error line : " + i;
                                }
                                i++;
                            }
                            i++; // 다음 블록을 처리했으므로 인덱스 증가
                        }
                    }
                    //그 다음 블럭이 [numberVal] 또는 [StringVal]이면 그 값을 추가
                    if (i + 1 < blocks.size()) {
                        Block nextBlock = blocks.get(i + 1);
                        if ("[numberVal]".equals(nextBlock.getBlock()) || "[StringVal]".equals(nextBlock.getBlock())) {
                            javaCode.append(block.getValue() + " "); // 변수명
                            // 그 다음 블럭이 [>, <, ==, !=]이면 그 값을 추가
                            if (i + 2 < blocks.size()) {
                                Block nextNextBlock = blocks.get(i + 2);
                                if (">".equals(nextNextBlock.getBlock()) || "<".equals(nextNextBlock.getBlock()) || "==".equals(nextNextBlock.getBlock()) || "!=".equals(nextNextBlock.getBlock())) {
                                    javaCode.append(nextNextBlock.getValue() + " "); // 연산자
                                    // 그 다음 블럭이 [number] 또는 [String]이면 그 변수명을 추가
                                    if (i + 3 < blocks.size()) {
                                        Block nextNextNextBlock = blocks.get(i + 3);
                                        if ("[number]".equals(nextNextNextBlock.getBlock()) || "[String]".equals(nextNextNextBlock.getBlock())) {
                                            javaCode.append(nextBlock.getValue() + ")"); // 값
                                            i++; // 다음 블록을 처리했으므로 인덱스 증가
                                        }else{
                                            // 예외처리 - 몇번째 줄인지 알려줌
                                            return "convert error line : " + i;
                                        }

                                    }
                                }else{
                                    // 예외처리
                                    return "convert error line : " + i;
                                }
                                i++;
                            }
                            i++; // 다음 블록을 처리했으므로 인덱스 증가
                        }
                    }
                    javaCode.append(") \n");
                    break;
                case "[print]":
                    // print가 이미 1번 나왔으면 예외처리
                    if (printCnt == 1) {
                        return "convert error line : " + i;
                    }
                    //javaCode.append("System.out.println(").append(block.getValue()).append(");\n");
                    javaCode.append("System.out.println(");
                    //그 다음 블럭이 [number] 또는 [String]이면 그 값을 추가
                    if (i + 1 < blocks.size()) {
                        Block nextBlock = blocks.get(i + 1);
                        if ("[number]".equals(nextBlock.getBlock()) || "[String]".equals(nextBlock.getBlock())) {
                            javaCode.append(nextBlock.getValue());
                            i++; // 다음 블록을 처리했으므로 인덱스 증가
                        }
                    }
                    //그 다음 블럭이 [numberVal] 또는 [StringVal]이면 그 변수명을 추가
                    if (i + 1 < blocks.size()) {
                        Block nextBlock = blocks.get(i + 1);
                        if ("[numberVal]".equals(nextBlock.getBlock()) || "[StringVal]".equals(nextBlock.getBlock())) {
                            javaCode.append(block.getValue());
                            i++; // 다음 블록을 처리했으므로 인덱스 증가
                        }
                    }
                    javaCode.append(");\n");
                    break;
                case "[+]":
                    javaCode.append(block.getValue()).append(" += "); // 다음 블록의 값이 추가될 것으로 예상
                    break;
                case "[-]":
                    javaCode.append(block.getValue()).append(" -= "); // 다음 블록의 값이 추가될 것으로 예상
                    break;
                case "[*]":
                    javaCode.append(block.getValue()).append(" *= "); // 다음 블록의 값이 추가될 것으로 예상
                    break;
                case "[/]":
                    javaCode.append(block.getValue()).append(" /= "); // 다음 블록의 값이 추가될 것으로 예상
                    break;
                case "{":
                    javaCode.append("{\n");
                    break;
                case "}":
                    javaCode.append("}\n");
                    break;
                case "[number]":
                case "[String]":
                    // 이전에 입력된 값 다음에 block.getValue()를 추가
                    javaCode.append(block.getValue());
                    break;
                case "[enter]":
                    javaCode.append(";\n"); // 다음줄
                    break;
                default:
                    // 처리되지 않은 블록 타입에 대한 처리
                    break;
            }
        }

        javaCode.append("    }\n");
        javaCode.append("}\n");

        log.info("변환된 자바 코드...");
        log.info(javaCode.toString());

        return javaCode.toString();
    }

    private void processBlock(Block block, StringBuilder javaCode) {
        switch (block.getBlock()) {
            case "[numberVal]":
                javaCode.append("int ").append(block.getValue()).append(" = 0;\n");
                break;
            case "[StringVal]":
                javaCode.append("String ").append(block.getValue()).append(" = \"\";\n");
                break;
            case "[for]":
                javaCode.append("for (int i = 0; i < 2; i++) {\n");
                break;
            case "[if]":
                //javaCode.append("if (/* condition */) {\n");
                javaCode.append("if (");
                //그 다음 블럭이 [number] 또는 [String]이면 그 값을 추가

                break;
            case "[print]":
                javaCode.append("System.out.println(").append(block.getValue()).append(");\n");
                break;
            case "[+]":
                javaCode.append(block.getValue()).append(" += "); // 다음 블록의 값이 추가될 것으로 예상
                break;
            case "[-]":
                javaCode.append(block.getValue()).append(" -= "); // 다음 블록의 값이 추가될 것으로 예상
                break;
            case "[*]":
                javaCode.append(block.getValue()).append(" *= "); // 다음 블록의 값이 추가될 것으로 예상
                break;
            case "[/]":
                javaCode.append(block.getValue()).append(" /= "); // 다음 블록의 값이 추가될 것으로 예상
                break;
            case "{":
                javaCode.append("{\n");
                break;
            case "}":
                javaCode.append("}\n");
                break;
            case "[number]":
            case "[String]":
                // 이전에 입력된 값 다음에 block.getValue()를 추가
                javaCode.append(block.getValue());
                break;
            case "[enter]":
                javaCode.append(";\n"); // 다음줄
                break;
            default:
                // 처리되지 않은 블록 타입에 대한 처리
                break;
        }
    }
}
