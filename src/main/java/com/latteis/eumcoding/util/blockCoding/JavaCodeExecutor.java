package com.latteis.eumcoding.util.blockCoding;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class JavaCodeExecutor {

    // Java 코드를 실행하는 메소드
    public String executeJavaCode(String javaCode) throws Exception {
        // Java 소스 파일로 변환
        JavaFileObject file = new JavaSourceFromString("Main", javaCode);

        // 시스템 Java 컴파일러를 얻는다.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("시스템 Java 컴파일러를 찾을 수 없습니다. class path가 tools.jar를 포함하는지 확인하세요.");
        }

        // 파일 관리자 초기화
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            // 컴파일 작업 설정
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, Arrays.asList(file));

            // 컴파일 실행
            boolean success = task.call();
            if (!success) {
                throw new Exception("컴파일 실패.");
            }

            // 메모리에서 컴파일된 클래스를 로드하기 위한 클래스 로더
            try (URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File("").toURI().toURL()})) {
                Class<?> cls = Class.forName("Main", true, classLoader);
                Method method = cls.getDeclaredMethod("main", String[].class);

                // 표준 출력을 바이트 배열 출력 스트림으로 리디렉션
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream oldOut = System.out;
                System.setOut(new PrintStream(baos));

                try {
                    // 메소드 실행
                    method.invoke(null, (Object) new String[]{});
                } finally {
                    // 원래의 표준 출력으로 복원
                    System.setOut(oldOut);
                }

                return baos.toString();
            }
        }
    }

    // 컴파일을 위한 Java 소스 파일 클래스
    static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}