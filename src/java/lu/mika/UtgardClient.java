package lu.mika;

import java.util.concurrent.Executors;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;
 
//test konekcije sa opc serverom 
public class UtgardClient {
 
    public static void main(String[] args) throws Exception {
        
        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost("localhost");
        ci.setDomain("");
        ci.setUser("branovic");
        ci.setPassword("babaRoga");
        ci.setClsid("6E617103-FF2D-11D2-8087-00105AA8F840");
        final String itemId = "Channel_0_User_Defined.Ramp.Ramp_Float";
        
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
         
        try {
            server.connect();
            // add sync access, poll every 10 ms
            final AccessBase access = new SyncAccess(server, 10);
            access.addItem(itemId, new DataCallback() {
                @Override
                public void changed(Item item, ItemState state) {
                    try {
                        System.out.println(state.getValue().getObjectAsFloat());
                    } catch (JIException e) {
                        System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
                    }
                }
            });
            // start reading
            access.bind();
            // run forever
            final boolean running = true;
            while (running){
                Thread.sleep ( 10 * 1000 );
            }
            // stop reading
            //access.unbind();

        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }
}
