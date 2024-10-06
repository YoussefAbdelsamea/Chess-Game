package ChessCore;

public class Invoker {
    private Command command;
    
    public void setCommand(Command command) {
        this.command = command;
    }

    public boolean doComand() {
        return command.execute();
    }
    
}
