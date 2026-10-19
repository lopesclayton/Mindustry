package wigo;

import arc.files.Fi;
import arc.files.ZipFi;
import mindustry.mod.WigoModPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static org.junit.jupiter.api.Assertions.*;

class ModPolicyTests{
    @TempDir Path directory;

    @Test void dataModIsAllowed(){
        Fi root = new Fi(directory.toFile());
        root.child("content/blocks/block.hjson").writeString("name: block");
        root.child("maps/example.msav").writeString("test fixture");
        assertDoesNotThrow(() -> WigoModPolicy.validate(root, true, false));
    }

    @Test void executableFilesAreRejectedWithoutDeletingThem(){
        Fi root = new Fi(directory.toFile());
        for(String extension : new String[]{"JAR", "dex", "class", "so", "dll", "dylib", "js", "mjs", "cjs"}){
            Fi file = root.child("nested/payload." + extension);
            file.writeString("fixture");
            assertThrows(IllegalArgumentException.class, () -> WigoModPolicy.validate(file, true, false));
            assertTrue(file.exists());
        }
        assertThrows(IllegalArgumentException.class, () -> WigoModPolicy.validate(root, true, false));
    }

    @Test void scriptsAndJavaMetadataAreRejected(){
        Fi root = new Fi(directory.toFile());
        assertThrows(IllegalArgumentException.class, () -> WigoModPolicy.validate(root, true, true));
        root.child("scripts").mkdirs();
        assertThrows(IllegalArgumentException.class, () -> WigoModPolicy.validate(root, true, false));
    }

    @Test void desktopBehaviorIsUnchanged(){
        Fi root = new Fi(directory.toFile());
        root.child("scripts/main.js").writeString("fixture");
        assertDoesNotThrow(() -> WigoModPolicy.validate(root, false, true));
    }

    @Test void wrappedArchiveCannotHideExecutableCode() throws Exception{
        Path archive = directory.resolve("mod.zip");
        try(ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(archive))){
            out.putNextEntry(new ZipEntry("wrapper/content/block.hjson"));
            out.write("name: block".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            out.closeEntry();
            out.putNextEntry(new ZipEntry("outside/CLASSES.DEX"));
            out.write(new byte[]{1});
            out.closeEntry();
        }
        ZipFi root = new ZipFi(new Fi(archive.toFile()));
        try{
            assertThrows(IllegalArgumentException.class, () -> WigoModPolicy.validate(root, true, false));
        }finally{
            root.delete(); // ZipFi closes the archive; it does not delete the file.
        }
        assertTrue(Files.exists(archive));
    }
}
