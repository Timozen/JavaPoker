package game.models;

/**
 * Created by Derpie on 27.10.2016.
 */
public enum PlayerState {
    PLAYING (0),
    ALLIN (1),
    FOLD (2),
    PAID (3);
    private int state;
    PlayerState (int state) {
        this.state = state;
    }
    public int GetState() { return state; }
}
