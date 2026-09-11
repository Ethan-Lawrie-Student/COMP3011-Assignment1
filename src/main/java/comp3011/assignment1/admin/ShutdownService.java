package comp3011.assignment1.admin;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ShutdownService {
	private final ConfigurableApplicationContext applicationContext;
	private final AtomicBoolean alreadyShuttingDown = new AtomicBoolean(false);

	
	public ShutdownService(
            ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

	
	public boolean requestStop() {
		if (!alreadyShuttingDown.compareAndSet(false, true)){
			return false;
		}
		
		try {
            Thread.ofPlatform()
                    .name("administrative-shutdown")
                    .daemon(false)
                    .start(this::stopApp);
        } catch (RuntimeException exception) {
        	alreadyShuttingDown.set(false);
            throw exception;
        }

        return true;
	}
	
	
	
	public void stopApp() {
		try {
			Thread.sleep(1000); // to allow for time to send 
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();		
		}
		
		applicationContext.close();
	}
	
}
