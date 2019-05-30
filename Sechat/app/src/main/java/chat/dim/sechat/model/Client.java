package chat.dim.sechat.model;

import java.util.HashMap;
import java.util.Map;

import chat.dim.client.Station;
import chat.dim.client.Terminal;
import chat.dim.core.Barrack;
import chat.dim.mkm.User;
import chat.dim.mkm.entity.ID;

public class Client extends Terminal {
    private static final Client ourInstance = new Client();

    public static Client getInstance() {
        return ourInstance;
    }

    private Client() {
    }

    public String getDisplayName() {
        return "DIM!";
    }

    static {
        // test
        Barrack barrack = Barrack.getInstance();

        Client client = Client.getInstance();

        {
            Map<String, Object> dictioanry = new HashMap<>();
            dictioanry.put("ID", "gsp-s001@x5Zh9ixt8ECr59XLye1y5WWfaX4fcoaaSC");
            dictioanry.put("host", "134.175.87.98");
            dictioanry.put("port", 9527);

            client.currentStation = new Station(dictioanry);
        }
        {
            ID identifier = ID.getInstance("moki@4WDfe3zZ4T7opFSi3iDAKiuTnUHjxmXekk");
            User user = barrack.getUser(identifier);
            client.setCurrentUser(user);
        }
    }
}
