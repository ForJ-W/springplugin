/*
 * Copyright 2023 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springplugin.core.autoconfigure;

import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 自动装配导入工具
 * <p>
 * {@link org.springframework.boot.context.annotation.ImportCandidates}
 * <p>
 * Contains {@code @Configuration} import candidates, usually auto-configurations.
 * <p>
 * The {@link #load(Class, ClassLoader)} method can be used to discover the import
 * candidates.
 *
 * @author Moritz Halbritter
 * @author Scott Frederick
 * @since 2.7.0
 */
public final class ImportCandidates implements Iterable<String> {

    private static final String LOCATION = "META-INF/spring/%s.imports";

    private static final String COMMENT_START = "#";

    private final List<String> candidates;

    private ImportCandidates(List<String> candidates) {
        Assert.notNull(candidates, "'candidates' must not be null");
        this.candidates = Collections.unmodifiableList(candidates);
    }

    /**
     * Loads the names of import candidates from the classpath.
     * <p>
     * The names of the import candidates are stored in files named
     * {@code META-INF/spring/full-qualified-annotation-name.imports} on the classpath.
     * Every line contains the full qualified name of the candidate class. Comments are
     * supported using the # character.
     *
     * @param annotation  annotation to load
     * @param classLoader class loader to use for loading
     * @return list of names of annotated classes
     */
    public static ImportCandidates load(Class<?> annotation, ClassLoader classLoader) {
        Assert.notNull(annotation, "'annotation' must not be null");
        ClassLoader classLoaderToUse = decideClassloader(classLoader);
        String location = String.format(LOCATION, annotation.getName());
        Enumeration<URL> urls = findUrlsInClasspath(classLoaderToUse, location);
        List<String> importCandidates = new ArrayList<>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            importCandidates.addAll(readCandidateConfigurations(url));
        }
        return new ImportCandidates(importCandidates);
    }

    private static ClassLoader decideClassloader(ClassLoader classLoader) {
        if (classLoader == null) {
            return ImportCandidates.class.getClassLoader();
        }
        return classLoader;
    }

    public static Enumeration<URL> findUrlsInClasspath(ClassLoader classLoader, String location) {
        try {
            return classLoader.getResources(location);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to load configurations from location [" + location + "]", ex);
        }
    }

    public static List<String> readCandidateConfigurations(URL url) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new UrlResource(url).getInputStream(), StandardCharsets.UTF_8))) {
            List<String> candidates = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = stripComment(line);
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                candidates.add(line);
            }
            return candidates;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load configurations from location [" + url + "]", ex);
        }
    }

    private static String stripComment(String line) {
        int commentStart = line.indexOf(COMMENT_START);
        if (commentStart == -1) {
            return line;
        }
        return line.substring(0, commentStart);
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return this.candidates.iterator();
    }

    /**
     * Returns the list of loaded import candidates.
     *
     * @return the list of import candidates
     */
    public List<String> getCandidates() {
        return this.candidates;
    }

}
