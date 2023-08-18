package com.mmalotky.worldswitch.IO;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class IOMethodsTest {
    @Test
    void shouldCopyAndDeleteDirectories() {
        Path ioPath = Path.of("./TestFiles/IOCopy");
        assertFalse(Files.exists(ioPath));
        IOMethods.copyDirectory(Path.of("./TestFiles/IOTest"), ioPath);
        assertTrue(Files.exists(ioPath));

        File ioCopy = new File(ioPath.toUri());
        assertNotNull(ioCopy.list());
        assertEquals(2, ioCopy.list().length);

        IOMethods.deleteDirectory(ioCopy);
        assertFalse(Files.exists(ioPath));
    }

    @Test
    void shouldDeleteLinks() throws IOException {
        Path ioPath = Path.of("./TestFiles/IOLink");
        Files.createSymbolicLink(ioPath, Path.of("./TestFiles/IOLinkTest"));
        assertTrue(Files.isSymbolicLink(ioPath));

        IOMethods.deleteDirectory(new File(ioPath.toUri()));
        assertFalse(Files.exists(ioPath));
        assertTrue(Files.exists(Path.of("./TestFiles/IOLinkTest/Test")));
    }
}