package wigo;

import mindustry.mod.WigoBrowserApproval;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BrowserApprovalTests{
    @Test void startsDeniedAndRequiresApproval(){
        var gate = new WigoBrowserApproval();
        assertFalse(gate.permits(gate.token()));
        long request = gate.begin();
        assertFalse(gate.permits(request));
        assertTrue(gate.approve(request));
        assertTrue(gate.permits(request));
    }
    @Test void closingRejectsLateApprovalAndPendingImages(){
        var gate = new WigoBrowserApproval();
        long request = gate.begin();
        gate.approve(request);
        gate.close();
        assertFalse(gate.permits(request));
        assertFalse(gate.approve(request));
    }
    @Test void reopeningNeverReusesApprovalOrAnOldCallback(){
        var gate = new WigoBrowserApproval();
        long old = gate.begin();
        gate.approve(old);
        gate.close();
        long current = gate.begin();
        assertFalse(gate.approve(old));
        assertFalse(gate.permits(current));
        assertTrue(gate.approve(current));
        assertFalse(gate.permits(old));
        assertTrue(gate.permits(current));
    }
}
