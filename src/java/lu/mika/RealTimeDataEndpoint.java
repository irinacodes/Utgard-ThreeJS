package lu.mika;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author ibranovic
 */


@ServerEndpoint("/realtime")
public class RealTimeDataEndpoint {
    private static final Logger logger = Logger.getLogger("RealTimeDataEndpoint");
   
    // Red za sve otvorene WebSocket sesije */
    static Queue<Session> queue = new ConcurrentLinkedQueue<>();
    
   // CoordinateBean poziva ovaj metod da bi dobijao ažurirane podatke
   public static void send(double coordinate) {
      String msg = String.format("%1$,.2f", coordinate);
      try {
         //šalje update svim otvorenim WebSocket sesijama
         for (Session session : queue) {
            session.getBasicRemote().sendText(msg);
         }
      } catch (IOException e) {
         logger.log(Level.INFO, e.toString());
      }
    }

    @OnOpen
   public void openConnection(Session session) {
      // registruje vezu u redu
      queue.add(session);
      logger.log(Level.INFO, "Connection opened.");
   }
   
   @OnClose
   public void closedConnection(Session session) {
      // uklanja vezu iz reda
      queue.remove(session);
      logger.log(Level.INFO, "Connection closed.");
   }
   
   @OnError
   public void error(Session session, Throwable t) {
      // uklanja vezu iz reda
      queue.remove(session);
      logger.log(Level.INFO, t.toString());
      logger.log(Level.INFO, "Connection error.");
   }
    
}
