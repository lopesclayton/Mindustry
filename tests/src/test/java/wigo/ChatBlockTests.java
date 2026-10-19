package wigo;

import mindustry.net.WigoChatBlocks;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChatBlockTests{
    @Test void directionalAndIsolated(){
        var blocks = new WigoChatBlocks(new String[0]);
        assertTrue(blocks.block("alice", "bob"));
        assertTrue(blocks.blocks("alice", "bob"));
        assertFalse(blocks.blocks("bob", "alice"));
        assertFalse(blocks.blocks("carol", "bob"));
    }
    @Test void restartSnapshotDoesNotStoreRawIdentifiers(){
        var blocks = new WigoChatBlocks(new String[0]);
        blocks.block("alice", "bob");
        assertFalse(blocks.snapshot()[0].contains("alice"));
        assertFalse(blocks.snapshot()[0].contains("bob"));
        assertTrue(new WigoChatBlocks(blocks.snapshot()).blocks("alice", "bob"));
    }
    @Test void invalidAndSelfBlocksRejected(){
        var blocks = new WigoChatBlocks(new String[]{"corrupt", null});
        assertFalse(blocks.block("alice", "alice"));
        assertFalse(blocks.block(null, "bob"));
        assertFalse(blocks.block("alice", "[LOCAL]"));
        assertEquals(0, blocks.snapshot().length);
    }
    @Test void unblockAndClearOnlyAffectRequester(){
        var blocks = new WigoChatBlocks(new String[0]);
        blocks.block("alice", "bob");
        blocks.block("carol", "bob");
        assertTrue(blocks.unblock("alice", "bob"));
        blocks.block("alice", "dave");
        blocks.clearRecipient("alice");
        assertFalse(blocks.blocks("alice", "dave"));
        assertTrue(blocks.blocks("carol", "bob"));
    }
    @Test void boundedAndIdempotent(){
        var blocks = new WigoChatBlocks(new String[0]);
        for(int i = 0; i < 500; i++) assertTrue(blocks.block("alice", "sender-" + i));
        assertTrue(blocks.block("alice", "sender-0"));
        assertFalse(blocks.block("alice", "extra"));
    }
}
