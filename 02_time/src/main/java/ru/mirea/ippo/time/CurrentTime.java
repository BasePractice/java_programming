package ru.mirea.ippo.time;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * @link http://tf.nist.gov/tf-cgi/servers.cgi
 */
public final class CurrentTime {
    public static void main(String[] args) throws IOException {
        TimeTCPClient client = new TimeTCPClient();
        client.setCharset(Charset.forName("UTF-8"));
        client.setDefaultTimeout(1000);
        try {
            client.connect("nist1-macon.macon.ga.us");
            final Date date = client.getDate();
            System.out.println(String.format("Current time: %s", date));
        } finally {
            client.disconnect();
        }
    }
}
