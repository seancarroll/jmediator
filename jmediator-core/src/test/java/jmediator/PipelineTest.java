package jmediator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PipelineTest {

    @Test
    public void test() throws ClassNotFoundException {
        DefaultRequestHandlerProvider provider = new DefaultRequestHandlerProvider();
        provider.register(new ZingHandler());

        List<PipelineBehavior> behaviors = new ArrayList<>();
        behaviors.add(new LoggingPipelineBehavior());
        RequestDispatcher dispatcher = new RequestDispatcherImpl(provider, behaviors);

        Zing zing = new Zing();
        zing.message = "Hi there";

        Zong zong = dispatcher.send(zing);
        assertEquals("Hi there Zong", zong.message);
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
		public Zong handle(Zing request) {
			logger.info("handler");
			return new Zong(request.message + " Zong");
		}
	}
    
    private static class LoggingPipelineBehavior implements PipelineBehavior {
        private static final Logger _output = LoggerFactory.getLogger(LoggingPipelineBehavior.class);


		@Override
		public <T extends Request, R> R handle(T request, PipelineChain chain) {
			_output.info("logging before chain");
			R response = chain.doBehavior();
			_output.info("logging after chain");
			return response;
		}
        
    }

}
