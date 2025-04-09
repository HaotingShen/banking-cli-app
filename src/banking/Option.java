package banking;

import java.util.function.BooleanSupplier;

public class Option {
    private String optionName;
    private Runnable optionCall;
    private BooleanSupplier isVisible;

    public Option(String optionName, Runnable optionCall, BooleanSupplier isVisible) {
        this.optionName = optionName;
        this.optionCall = optionCall;
        this.isVisible = isVisible;
    }

    public Option(String optionName, Runnable optionCall) {
        this(optionName, optionCall, () -> true);
    }

    public boolean execute() {
        if (optionCall != null) {
            optionCall.run();
            return true;
        }
        return false;
    }

    public String getOptionName() {
        return optionName;
    }

    public boolean isVisible() {
        return isVisible.getAsBoolean();    
    }
}