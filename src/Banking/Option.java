package banking;

public class Option {
    private String optionName;
    private Runnable optionCall;

    public Option(String optionName, Runnable optionCall) {
        this.optionName = optionName;
        this.optionCall = optionCall;
    }

    public void execute() {
        if (optionCall != null) {
            optionCall.run();
        }
    }

    public String getOptionName() {
        return optionName;
    }
}