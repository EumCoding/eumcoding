package com.latteis.eumcoding.util.blockCoding;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaCompiler.CompilationTask;
import java.util.Arrays;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class JavaCodeExecutor {
    public String executeJavaCode(String javaCode) throws Exception {
        // Java 소스 파일로 변환
        JavaFileObject file = new JavaSourceFromString("Main", javaCode);

        // 컴파일러 얻기
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        // 파일 관리자와 소스 파일로 컴파일 작업 설정
        CompilationTask task = compiler.getTask(null, fileManager, null, null, null, Arrays.asList(file));

        // 컴파일 실행
        boolean success = task.call();
        if (!success) {
            throw new Exception("Compilation failed.");
        }

        // 메모리에서 컴파일된 클래스 로드
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File("").toURI().toURL() });
        Class<?> cls = Class.forName("Main", true, classLoader);
        Method method = cls.getDeclaredMethod("main", String[].class);

        // 표준 출력을 바이트 배열 출력 스트림으로 리디렉션
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(baos));

        // 메소드 실행
        method.invoke(null, (Object) new String[] {});

        // 원래의 표준 출력으로 복원
        System.setOut(System.out);

        return baos.toString();
    }

    // 컴파일을 위한 Java 소스 파일 클래스
    static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
