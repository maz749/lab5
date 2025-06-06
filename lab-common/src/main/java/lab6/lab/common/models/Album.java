//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.common.models;

import java.io.Serializable;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Integer length;

    public Album(String name, Integer length) {
        if (name != null && !name.isEmpty()) {
            if (length != null && length > 0) {
                this.name = name;
                this.length = length;
            } else {
                throw new IllegalArgumentException("Длина альбома должна быть больше 0.");
            }
        } else {
            throw new IllegalArgumentException("Название альбома не может быть пустым или null.");
        }
    }

    public String getName() {
        return this.name;
    }

    public Integer getLength() {
        return this.length;
    }

    public String toString() {
        return "Album{name='" + this.name + "', length=" + this.length + "}";
    }
}
