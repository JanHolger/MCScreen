package eu.bebendorf.mcscreen.api.helper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public enum MouseButton {
    LEFT(0),
    RIGHT(1);
    int value;
    public static MouseButton byValue(int value){
        for(MouseButton b : values()){
            if(b.getValue() == value)
                return b;
        }
        return null;
    }
}
