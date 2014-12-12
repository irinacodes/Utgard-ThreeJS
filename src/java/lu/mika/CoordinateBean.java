package lu.mika;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;

/**
 *
 * @author ibranovic
 */

@Startup
@Singleton
public class CoordinateBean {
   @Resource TimerService tservice;
 
   @PostConstruct
   public void init() {
       tservice.createSingleActionTimer(1000, new TimerConfig());     
   }
   
   @Timeout
   public void timeout() {
       OPCClient client = new OPCClient();
       try {
           client.start();
       } catch (Exception ex) {
           Logger.getLogger(CoordinateBean.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
    
}
