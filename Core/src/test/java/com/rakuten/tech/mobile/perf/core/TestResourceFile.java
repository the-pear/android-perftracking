package com.rakuten.tech.mobile.perf.core;

import org.junit.rules.ExternalResource;

import java.io.InputStream;
import java.util.Scanner;

class TestResourceFile extends ExternalResource {

    private final String fileName;
    private Scanner scanner;
    String content = "";

    TestResourceFile(String resourceFileName) {
        this.fileName = resourceFileName;
    }

    @Override protected void before() throws Throwable {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(fileName);
        scanner = new Scanner(stream);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNext()) sb.append(scanner.next());
        content = sb.toString();
    }

    @Override protected void after() {
        scanner.close();
    }
}
