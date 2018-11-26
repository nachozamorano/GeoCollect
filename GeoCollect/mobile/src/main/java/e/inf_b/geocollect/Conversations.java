package e.inf_b.geocollect;

import java.util.concurrent.ThreadLocalRandom;

public class Conversations {
    /**
     * Set of strings used as messages by the sample.
     */
    private static final String[] MESSAGES = new String[]{
    "Click en la notificacion del auto"
    };

    /**
     * Senders of all the messages.
     */
    public static final String SENDER_NAME = "GeoCollect";

    /**
     * A static conversation Id for all our messages.
     */
    public static final int CONVERSATION_ID = 13;

    private Conversations() {
    }

    public static String getUnreadMessage() {
        return MESSAGES[ThreadLocalRandom.current().nextInt(0, MESSAGES.length)];
    }
}
