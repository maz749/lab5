//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.common.manager;

import java.io.BufferedReader;
import java.util.List;
import lab6.lab.common.models.MusicBand;

public class MusicBandManager {
    private final MusicBandCollection collection = new MusicBandCollection();
    private final FileStorage storage = new FileStorage(new MusicBandFactory());
    private final CommandExecutor executor;

    public MusicBandManager() {
        this.executor = new CommandExecutor(this.collection, this.storage);
    }

    public void loadFromFile(String fileName) {
        this.storage.loadFromFile(fileName, this.collection);
    }

    public void executeCommand(String commandLine) {
        this.executor.executeCommand(commandLine, (BufferedReader)null);
    }

    public List<MusicBand> getMusicBands() {
        return this.collection.getMusicBands();
    }

    public CommandExecutor getExecutor() {
        return this.executor;
    }
}
