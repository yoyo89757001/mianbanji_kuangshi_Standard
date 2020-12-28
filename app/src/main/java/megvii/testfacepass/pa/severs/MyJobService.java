//package megvii.testfacepass.pa.severs;
//
//import android.app.job.JobInfo;
//import android.app.job.JobParameters;
//import android.app.job.JobScheduler;
//import android.app.job.JobService;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Message;
//import android.os.Messenger;
//import android.os.RemoteException;
//import android.os.SystemClock;
//import android.util.Log;
//
//import java.util.LinkedList;
//
//import megvii.testfacepass.pa.ui.MianBanJiActivity3;
//
//public class MyJobService extends JobService {
//    private static final String TAG = "SyncService";
//    private int kJobId=0;
//    private ComponentName mServiceComponent;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.i(TAG, "Service created");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.i(TAG, "Service destroyed");
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Messenger callback = intent.getParcelableExtra("messenger");
//        Message m = Message.obtain();
//        m.what = MianBanJiActivity3.MSG_SERVICE_OBJ;
//        m.obj = this;
//        try {
//            callback.send(m);
//        } catch (RemoteException e) {
//            Log.e(TAG, "Error passing service object back to activity.");
//        }
//        return START_NOT_STICKY;
//    }
//
//    @Override
//    public boolean onStartJob(JobParameters params) {
//        // We don't do any real 'work' in this sample app. All we'll
//        // do is track which jobs have landed on our service, and
//        // update the UI accordingly.
//        jobParamsMap.add(params);
//        if (mActivity != null) {
//            mActivity.onReceivedStartJob(params);
//        }
//        Log.i(TAG, "on start job: " + params.getJobId());
//        return true;
//    }
//
//    @Override
//    public boolean onStopJob(JobParameters params) {
//        // Stop tracking these job parameters, as we've 'finished' executing.
//        jobParamsMap.remove(params);
//        if (mActivity != null) {
//            mActivity.onReceivedStopJob();
//        }
//        Log.i(TAG, "on stop job: " + params.getJobId());
//        if (kJobId>=655360){
//            kJobId=0;
//        }
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SystemClock.sleep(2000);
//                JobInfo.Builder builder = new JobInfo.Builder(kJobId++, mServiceComponent);
//                builder.setMinimumLatency(5000);
//                builder.setOverrideDeadline(10000);
//                scheduleJob(builder.build());
//            }
//        }).start();
//
//        return callJobFinished();//任务完成，调用他通知系统已经完成
//       // return true;
//    }
//
//    MianBanJiActivity3 mActivity;
//    private final LinkedList<JobParameters> jobParamsMap = new LinkedList<JobParameters>();
//
//    public void setUiCallback(MianBanJiActivity3 activity,ComponentName mServiceComponent) {
//        mActivity = activity;
//        this.mServiceComponent=mServiceComponent;
//    }
//
//    /** Send job to the JobScheduler. */
//    public void scheduleJob(JobInfo t) {
//        Log.d(TAG, "安排工作");
//        JobScheduler tm =
//                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        if(tm.schedule(t)<=0){
//            Log.i(TAG, "啊啊啊啊有问题");
//        }
//    }
//
//
//    /**
//     * Not currently used, but as an exercise you can hook this
//     * up to a button in the UI to finish a job that has landed
//     * in onStartJob().
//     */
//    public boolean callJobFinished() {
//        JobParameters params = jobParamsMap.poll();
//        if (params == null) {
//            return false;
//        } else {
//            jobFinished(params, false);
//            return true;
//        }
//    }
//}