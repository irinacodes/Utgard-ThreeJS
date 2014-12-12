
package lu.mika;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;

/**
 *
 * @author branovic
 */
public class OPCClient {
    
    public void start() throws Exception {
        // create connection information
        final ConnectionInformation ci = new ConnectionInformation();
        InputStream input = OPCClient.class.getResourceAsStream("/opc.properties");
        Properties properties = new Properties();
        properties.load(input);     
        ci.setHost(properties.getProperty("opc.server.ip"));
        ci.setUser(properties.getProperty("opc.user"));
        ci.setPassword(properties.getProperty("opc.password"));
        ci.setClsid(properties.getProperty("opc.clsid"));
        final String itemId = properties.getProperty("opc.item");
     
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
         
        try {
           
            server.connect();
            // add sync access, poll every opc.client.timeout ms
            final AccessBase access = new SyncAccess(server, Integer.valueOf(properties.getProperty("opc.client.timeout")));
            access.addItem(itemId, new DataCallback() {
                @Override
                public void changed(Item item, ItemState state) {
                    try {
                        RealTimeDataEndpoint.send((double) state.getValue().getObjectAsFloat());
                    } catch (JIException e) {
                        System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
                    }
                }
            });
            // start reading
            access.bind();
            // run forever
            while (true){
                Thread.sleep(10000);
            }
            // stop reading
            //access.unbind();
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }
    
}
