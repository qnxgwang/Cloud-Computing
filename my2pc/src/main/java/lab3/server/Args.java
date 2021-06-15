package lab3.server;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = {"--config_path"}, required = true)
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
