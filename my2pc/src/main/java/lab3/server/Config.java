package lab3.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Config {
    private String mode;
    private List<SocketTuple> coordinator = new ArrayList<>();
    private List<SocketTuple> participant = new ArrayList<>();

    public static class SocketTuple {
        private final String host;
        private final Integer port;

        SocketTuple(String host, Integer port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public Integer getPort() {
            return port;
        }

        @Override
        public String toString() {
            return host + ":" + port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SocketTuple that = (SocketTuple) o;
            return Objects.equals(host, that.host) &&
                    Objects.equals(port, that.port);
        }

        @Override
        public int hashCode() {
            return Objects.hash(host, port);
        }
    }

    public Config(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("mode")) {
                    mode = splitToConfig(line);
                } else if (line.startsWith("coordinator_info")) {
                    String addr = splitToConfig(line);
                    String[] ip = addr.split(":");
                    if (ip.length == 2) {
                        coordinator.add(new SocketTuple(ip[0], Integer.parseInt(ip[1])));
                    }
                } else if (line.startsWith("participant_info")) {
                    String addr = splitToConfig(line);
                    String[] ip = addr.split(":");
                    if (ip.length == 2) {
                        participant.add(new SocketTuple(ip[0], Integer.parseInt(ip[1])));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String splitToConfig(String str) {
        String[] values = str.split(" ");
        if (values.length == 2) {
            return values[1];
        } else {
            return "";
        }
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<SocketTuple> getCoordinator() {
        return coordinator;
    }

    public List<SocketTuple> getParticipant() {
        return participant;
    }

    public void setCoordinator(List<SocketTuple> coordinator) {
        this.coordinator = coordinator;
    }

    public void removeParticipant(SocketTuple socket) {
        participant.removeIf(socketTuple -> socketTuple.equals(socket));
    }

    @Override
    public String toString() {
        return "Config{" + "mode='" + mode + "'" + ", coordinator=" + coordinator +
                ", participant=" + participant.toString() +
                '}';
    }
}
