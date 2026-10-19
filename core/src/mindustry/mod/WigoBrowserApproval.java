package mindustry.mod;

/** In-memory approval for one visible catalog; never persisted or shared with downloads. */
public final class WigoBrowserApproval{
    private long generation;
    private boolean open, approved;

    public synchronized long begin(){
        generation++;
        open = true;
        approved = false;
        return generation;
    }

    public synchronized boolean approve(long request){
        if(!open || request != generation) return false;
        approved = true;
        return true;
    }

    public synchronized void close(){
        open = approved = false;
        generation++;
    }

    public synchronized long token(){ return generation; }
    public synchronized boolean permits(long request){
        return open && approved && request == generation;
    }
}
