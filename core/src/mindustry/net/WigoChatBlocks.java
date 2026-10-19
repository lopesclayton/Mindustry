package mindustry.net;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/** Server-owned, directional communication blocks. Never trusts display names or entity IDs. */
public final class WigoChatBlocks{
    private final Set<String> entries = new HashSet<>();
    private static final int maxPerRecipient = 500;

    public WigoChatBlocks(String[] saved){
        for(String entry : saved){
            if(entry != null && entry.matches("[0-9a-f]{64}:[0-9a-f]{64}")) entries.add(entry);
        }
    }

    private static boolean valid(String id){
        return id != null && !id.isEmpty() && !id.equals("[LOCAL]");
    }

    private static String digest(String id){
        try{
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(id.getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder(64);
            for(byte b : hash) out.append(String.format(Locale.ROOT, "%02x", b & 255));
            return out.toString();
        }catch(java.security.NoSuchAlgorithmException impossible){
            throw new IllegalStateException(impossible);
        }
    }

    public boolean blocks(String recipient, String sender){
        return valid(recipient) && valid(sender) && entries.contains(digest(recipient) + ":" + digest(sender));
    }

    public boolean block(String recipient, String sender){
        if(!valid(recipient) || !valid(sender) || recipient.equals(sender)) return false;
        String prefix = digest(recipient) + ":", key = prefix + digest(sender);
        if(entries.contains(key)) return true;
        long count = entries.stream().filter(e -> e.startsWith(prefix)).count();
        if(count >= maxPerRecipient) return false;
        return entries.add(key);
    }

    public boolean unblock(String recipient, String sender){
        return valid(recipient) && valid(sender) && entries.remove(digest(recipient) + ":" + digest(sender));
    }

    public void clearRecipient(String recipient){
        if(valid(recipient)){
            String prefix = digest(recipient) + ":";
            entries.removeIf(e -> e.startsWith(prefix));
        }
    }

    public String[] snapshot(){
        return entries.stream().sorted().toArray(String[]::new);
    }
}
