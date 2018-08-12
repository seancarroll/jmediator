package jmediator;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PipelineTest {

    @Test
    public void test() {
        fail("Not yet implemented");
    }
    
    public class Ping implements Request {
        public String message;
    }

    public class Pong {
        public Pong(String message) {
            this.message = message;
        }
        public String message;
    }

    public class Zing implements Request {
        public String message;
    }

    public class Zong {
        public Zong(String message) {
            this.message = message;
        }
        public String message;
    }
    
    public class PingHandler implements RequestHandler<Ping, Pong> {
        private final Logger logger = LoggerFactory.getLogger(ZingHandler.class);

        @Override
        public Pong handle(Ping request) {
            logger.info("handler");
            return new Pong (request.message + " Pong");
        }

    }

    public class ZingHandler implements RequestHandler<Zing, Zong> {
        private final Logger logger = LoggerFactory.getLogger(ZingHandler.class);

        @Override
        public Zong handle(Zing request)
        {
            logger.info("handler");
            return new Zong(request.message + " Zong");
        }
    }
    
    private static class ConstrainedLogging implements IPreRequestHandler {
        private static final Logger _output = LoggerFactory.getLogger(ConstrainedLogging.class);
        
        @Override
        public void handle(Request request) {
            //_output.info("Constrained before");
            //var response = await next();
            //_output.info("Constrained after");

            //return response;
        }
        
    }

}
