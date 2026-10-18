package wigo;

import mindustry.Vars;
import mindustry.core.Platform;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests the platform default only, not the Android device-credential runtime. */
public class ParentalPlatformTests{
    @Test
    void androidWithoutCredentialImplementationDenies(){
        boolean previous = Vars.android;
        try{
            Vars.android = true;
            AtomicInteger calls = new AtomicInteger();
            new Platform(){}.withParentalApproval(calls::incrementAndGet);
            assertEquals(0, calls.get());
        }finally{
            Vars.android = previous;
        }
    }

    @Test
    void otherPlatformsRetainTheirExistingAction(){
        boolean previous = Vars.android;
        try{
            Vars.android = false;
            AtomicInteger calls = new AtomicInteger();
            new Platform(){}.withParentalApproval(calls::incrementAndGet);
            assertEquals(1, calls.get());
        }finally{
            Vars.android = previous;
        }
    }
}
