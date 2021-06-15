package lab3.server;


import com.beust.jcommander.JCommander;
import io.vertx.core.json.JsonObject;
import lab3.service.Coordinator;
import lab3.service.Partipant;

public class Main {

    public static void main(String[] args) {
        Args arg = new Args();
        JCommander.newBuilder()
                .addObject(arg).build().parse(args);
        Config config = new Config(arg.getPath());
        JsonObject conf = new JsonObject().put("config", arg.getPath());
        if (config.getMode().contentEquals("participant")) {
            new Partipant(config).start();
        } else if (config.getMode().contentEquals("coordinator")) {
            new Coordinator(config).start();
        }
    }
}