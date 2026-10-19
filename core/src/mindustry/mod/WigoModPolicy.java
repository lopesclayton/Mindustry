package mindustry.mod;

import arc.files.Fi;
import java.util.Locale;

/** Android distribution accepts data mods only. Does not modify mod files. */
public final class WigoModPolicy{
    private WigoModPolicy(){}

    public static void validate(Fi root, boolean android, boolean javaMod){
        if(!android) return;
        if(javaMod) throw new IllegalArgumentException("Android: executable mods are not supported; use data-only mods.");
        inspect(root, 0);
    }

    private static void inspect(Fi file, int depth){
        if(depth > 64) throw new IllegalArgumentException("Mod directory nesting exceeds the safe limit.");
        String name = file.name().toLowerCase(Locale.ROOT);
        if(file.isDirectory()){
            if(name.equals("scripts")) reject();
            for(Fi child : file.list()) inspect(child, depth + 1);
        }else if(name.endsWith(".jar") || name.endsWith(".dex") || name.endsWith(".class") ||
                 name.endsWith(".so") || name.endsWith(".dll") || name.endsWith(".dylib") ||
                 name.endsWith(".js") || name.endsWith(".mjs") || name.endsWith(".cjs")){
            reject();
        }
    }

    private static void reject(){
        throw new IllegalArgumentException("Android: executable mods/scripts are not supported; use data-only mods.");
    }
}
