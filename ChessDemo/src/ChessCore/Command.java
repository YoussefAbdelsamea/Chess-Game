package ChessCore;

public interface Command {
    boolean execute();
}

// Concrete Command 1
class makeMove implements Command {
    private moveReciver moveReciver;

    public makeMove(moveReciver moveReciver) {
        this.moveReciver = moveReciver;
    }

    @Override
    public boolean execute() {
        return moveReciver.makeMove();
    }
}

// Concrete Command 2
class undoMove implements Command {
    private moveReciver moveReciver;

    public undoMove(moveReciver moveReciver) {
        this.moveReciver = moveReciver;
    }

    @Override
    public boolean execute() {
        return moveReciver.undoMove();
    }
}
