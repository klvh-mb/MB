package common.schedule;

import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import play.libs.Akka;
import play.libs.Time;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * Date: 19/10/14
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class JobScheduler {
    private static play.api.Logger logger = play.api.Logger.apply(JobScheduler.class);

    // Singleton
    private static JobScheduler instance_ = new JobScheduler();

    public static JobScheduler getInstance() {
        return instance_;
    }

    /**
     * @param schedulerId
     * @param cronExpression
     * @param jobTask
     */
    public void schedule(String schedulerId, String cronExpression, Runnable jobTask) {
        ActorSystem actorSystem = Akka.system();
        try {
            Time.CronExpression e = new Time.CronExpression(cronExpression);
            Date nextValidTimeAfter = e.getNextValidTimeAfter(new Date());
            FiniteDuration d = Duration.create(
                    nextValidTimeAfter.getTime() - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS);

            logger.underlyingLogger().info("Scheduling to run[" + schedulerId + "] at: " + nextValidTimeAfter);

            actorSystem.scheduler().scheduleOnce(
                    d,
                    new JobSchedulerRunnable(schedulerId, cronExpression, jobTask),
                    actorSystem.dispatcher());

        } catch (Exception e) {
            logger.underlyingLogger().error("Error in schedule", e);
        }
    }

    private class JobSchedulerRunnable implements Runnable {
        private String schedulerId;
        private String cronExpression;
        private Runnable jobTask;

        public JobSchedulerRunnable(String schedulerId, String cronExpression, Runnable jobTask) {
            this.schedulerId = schedulerId;
            this.cronExpression = cronExpression;
            this.jobTask = jobTask;
        }

        @Override
        public void run() {
            logger.underlyingLogger().info("JobScheduler - Running "+schedulerId);

            try {
                jobTask.run();
            } catch (Exception e) {
                logger.underlyingLogger().error("Error in "+schedulerId, e);
            }

            //Schedule for next time
            schedule(schedulerId, cronExpression, jobTask);
        }
    }

}