package com.app;

import com.app.model.io.InputScanner;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ShoppingBasketMain {

    public final static Logger logger = Logger.getLogger(ShoppingBasketMain.class);

    public static void main(String[] args){
        Injector injector = Guice.createInjector(new ShoppingBasketAssemblyModule());
        ShoppingBasketMain main = injector.getInstance(ShoppingBasketMain.class);
        main.start();
    }

    private List<Runnable> runnableThreads;
    @Inject
    public ShoppingBasketMain(InputScanner inputScanner){
        runnableThreads = new ArrayList<>();
        runnableThreads.add(inputScanner);
    }

    public void start(){
        logger.info("Begin launching " + this.getClass().getSimpleName() + "...");

        //Start its runnable threads
        ExecutorService executorService = Executors.newFixedThreadPool(runnableThreads.size());
        for (Runnable r : runnableThreads)
            executorService.execute(r);

        //handle termination when nothing is running
        executorService.shutdown();
        try{
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch(InterruptedException e){
            logger.error("Failed termination", e);
        }
    }
}
