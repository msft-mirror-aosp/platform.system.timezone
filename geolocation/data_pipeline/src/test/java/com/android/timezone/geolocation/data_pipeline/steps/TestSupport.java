/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.timezone.geolocation.data_pipeline.steps;

import static com.android.timezone.geolocation.data_pipeline.steps.LicenseSupport.LICENSE_FILE_NAME;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/** Helper methods for test code. */
public final class TestSupport {

    public static final String TEST_DATA_RESOURCE_DIR = "data/";

    private TestSupport() {
    }


    public static Path copyTestResource(Class<?> baseClass, String testResource, Path targetDir)
            throws IOException {

        Files.createDirectories(targetDir);

        return copyResourceAndLicense(
                baseClass, TEST_DATA_RESOURCE_DIR + testResource, targetDir);
    }

    private static Path copyResourceAndLicense(Class<?> baseClass,
            String relativeResourcePath, Path targetDir) throws IOException {

        String fileName = relativeResourcePath;
        String dirName = "";
        if (relativeResourcePath.contains("/")) {
            fileName = relativeResourcePath.substring(relativeResourcePath.lastIndexOf('/') + 1);
            dirName = relativeResourcePath.substring(0, relativeResourcePath.lastIndexOf('/'));
        }
        Path targetResourceFile = targetDir.resolve(fileName);
        copyResource(baseClass, relativeResourcePath, targetResourceFile);

        Path targetLicenseFile = targetDir.resolve(LICENSE_FILE_NAME);
        copyResource(baseClass, dirName + "/" + LICENSE_FILE_NAME, targetLicenseFile);

        return targetResourceFile;
    }

    private static void copyResource(Class<?> baseClass, String relativeResourcePath, Path target)
            throws IOException {
        try (InputStream inputStream = baseClass.getResourceAsStream(relativeResourcePath)) {
            if (inputStream == null) {
                fail("resource=" + relativeResourcePath + " not found");
            }
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static Path createTempDir(Class<?> testClass) throws IOException {
        return Files.createTempDirectory(testClass.getSimpleName());
    }

    public static void deleteDirectory(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes)
                    throws IOException {
                Files.deleteIfExists(path);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
                Files.delete(path);
                return FileVisitResult.CONTINUE;
            }
        });
        assertFalse(Files.exists(dir));
    }
}
