import java.util.EventObject;

public class Event extends EventObject {
    private String valueString;
    private int valueInt;
    private boolean valueBoolean;

    public Event(Object source) {
        super(source);
    }

    public String getValueString() {
        return valueString;
    }

    public int getValueInt() {
        return valueInt;
    }

    public boolean getValueBoolean() {
        return valueBoolean;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public void setValueInt(int valueInt) {
        this.valueInt = valueInt;
    }

    public void setValueBoolean(boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }
}
