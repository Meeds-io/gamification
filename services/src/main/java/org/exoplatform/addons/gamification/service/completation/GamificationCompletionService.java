package org.exoplatform.addons.gamification.service.completation;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.util.concurrent.*;

public class GamificationCompletionService implements Startable {

    private static final Log LOG = ExoLogger.getLogger(GamificationCompletionService.class);

    private final String THREAD_NUMBER_KEY = "thread-pool-size";

    private final String ASYNC_EXECUTION_KEY = "async-execution";

    private final String KEEP_ALIVE_TIME = "keepAliveTime";

    private final int DEFAULT_THREAD_NUMBER = 1;

    private final boolean DEFAULT_ASYNC_EXECUTION = true;

    private int configThreadNumber;

    private int keepAliveTime;

    private boolean configAsyncExecution;

    private Executor executor;

    private ExecutorCompletionService<?> ecs;

    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();

    public GamificationCompletionService(InitParams params) {

        //
        ValueParam threadNumberValue = params.getValueParam(THREAD_NUMBER_KEY);
        ValueParam asyncExecution = params.getValueParam(ASYNC_EXECUTION_KEY);
        ValueParam aliveTime = params.getValueParam(KEEP_ALIVE_TIME);

        //
        try {
            configThreadNumber = Integer.parseInt(threadNumberValue.getValue());
        } catch (Exception e) {
            configThreadNumber = DEFAULT_THREAD_NUMBER;
        }

        //
        try {
            keepAliveTime = Integer.parseInt(aliveTime.getValue());
        } catch (Exception e) {
            keepAliveTime = 10;
        }

        //
        try {
            configAsyncExecution = Boolean.parseBoolean(asyncExecution.getValue());
        } catch (Exception e) {
            configAsyncExecution = DEFAULT_ASYNC_EXECUTION;
        }

        int threadNumber = configThreadNumber <= 0 ? configThreadNumber : Runtime.getRuntime().availableProcessors();

        ThreadFactory threadFactory = new ThreadFactory() {
            public Thread newThread(Runnable runable) {
                Thread t = new Thread(runable, "Gamification-Thread");
                t.setPriority(Thread.MIN_PRIORITY);
                return t;
            }
        };
        if (configAsyncExecution) {
            executor = new ThreadPoolExecutor(threadNumber, threadNumber, keepAliveTime,TimeUnit.SECONDS, workQueue, threadFactory);
            ((ThreadPoolExecutor) executor).allowCoreThreadTimeOut(true);
        } else {
            executor = new GamificationCompletionService.DirectExecutor();
        }
        ecs = new ExecutorCompletionService(executor);

    }
    public void addTask(Callable callable) {
        ecs.submit(callable);
    }

    private class DirectExecutor implements Executor {

        public void execute(final Runnable runnable) {
            if (Thread.interrupted()) throw new RuntimeException();

            runnable.run();
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        if(executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdown();
        }
    }
}
