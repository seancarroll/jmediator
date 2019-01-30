RequestDispatcher
 * 
 * could use isAssignableFrom to determine if pipeline behavior should be executed
 * Look to see how spring does application event as it should be very similar in that
 * you register application event listeners/handlers that can handle certain types
 * and spring routes them appropriately
 * ApplicationListener
 * https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/event/EventListener.html
 * https://github.com/spring-projects/spring-framework/blob/efce7902c40e55d907baaa10b2dc071322e7be11/spring-context/src/main/java/org/springframework/context/event/GenericApplicationListenerAdapter.java
// TODO: Axon SimpleCommandBus has a subscribe method to register handlers for requests.
// I should add that as the default with the others way to register via class scanning, etc.
// That should likely into a default RequestHandlerProvider

Best way to handle handling subscriptions/registering handlers across 
different frameworks?


TODO: 
* open issue for OutputCapture in junit-pioneering repo to see if its worth adding there  
//    @Test
//    public void t(OutputCapture outputCapture) {
//
//    }

* Blog about testing logging AppenderList vs mocking
```

//import org.slf4j.impl.Log4jLoggerAdapter;

    @BeforeEach
    public void setUp() {
        behavior = new LoggingPipelineBehavior();
        pipelineChain = mock(PipelineChain.class);

//        Logger logger = LoggerFactory.getLogger(LoggingPipelineBehavior.class);
//        Field loggerField = logger.getClass().getDeclaredField("logger");
//        ReflectionUtils.makeAccessible(loggerField);
//        mockLogger = mock( org.apache.logging.log4j.spi.ExtendedLogger.class);
//        loggerField.set(logger, mockLogger);

    }

    @Test
    public void canLogNullReturnValue() {


        // PipelineChain pipelineChain = mock(PipelineChain.class);
        when(pipelineChain.doBehavior()).thenReturn(null);

        // Logger logger = mock(Logger.class);
        // LoggingPipelineBehavior behavior = new LoggingPipelineBehavior(logger);
        // LoggingPipelineBehavior behavior = new LoggingPipelineBehavior();

        Ping request = new Ping();
        Object response = behavior.handle(request, pipelineChain);

        // verify(logger, times(1)).info(any(String.class), eq(request));
        // verify(logger, times(1)).info(any(String.class), eq(response));
        // verifyNoMoreInteractions(logger);



        verify(mockLogger, times(1)).info(any(String.class), eq(request));
        verify(mockLogger, times(1)).info(any(String.class), eq(response));

//        verify(mockLogger, atLeast(1)).isInfoEnabled();
//        verify(mockLogger, times(2)).log(any(String.class), any(Priority.class), contains("[StubMessage]"),
//            any());
//        verify(mockLogger).log(any(String.class), any(Priority.class), and(contains("[StubMessage]"),
//            contains("[null]")), any());
//        verifyNoMoreInteractions(mockLogger);


        verifyNoMoreInteractions(mockLogger);
    }
    
    @Test
    public void canLogCustomReturnValue() {
        Ping request = new Ping();

        PipelineChain pipelineChain = mock(PipelineChain.class);
        when(pipelineChain.doBehavior()).thenReturn(new Pong("Hello"));

        Logger logger = mock(Logger.class);
        LoggingPipelineBehavior behavior = new LoggingPipelineBehavior(logger);

        Object response = behavior.handle(request, pipelineChain);

        verify(logger, times(1)).info(any(String.class), eq(request));
        verify(logger, times(1)).info(any(String.class), eq(response));

        verifyNoMoreInteractions(logger);
    }
    
    @Test
    public void shouldNotLogReturnWhenExceptionOccurs() {
        Ping request = new Ping();

        PipelineChain pipelineChain = mock(PipelineChain.class);
        when(pipelineChain.doBehavior()).thenThrow(new RuntimeException());

        Logger logger = mock(Logger.class);
        LoggingPipelineBehavior behavior = new LoggingPipelineBehavior(logger);

        try {
            behavior.handle(request, pipelineChain);
            fail("exception should propagate to caller");
        } catch (Exception ex) {
            // expected
        }

        verify(logger, times(1)).info(any(String.class), eq(request));
        verifyNoMoreInteractions(logger);
    }
```
