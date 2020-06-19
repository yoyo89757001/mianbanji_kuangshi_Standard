/*
 * Copyright © 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package megvii.testfacepass.pa.tuisong_jg;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import java.util.concurrent.TimeUnit;

import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.beans.ConfigBean;
import megvii.testfacepass.pa.utils.FileUtil;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
public class CoreService extends Service {

    private Server mServer;
    private ConfigBean configBean= MyApplication.myApplication.getBaoCunBeanBox().get(123456);


    @Override
    public void onCreate() {
        mServer = AndServer.webServer(this)
            .port(configBean.getPort())
            .timeout(10, TimeUnit.SECONDS)
            .listener(new Server.ServerListener() {
                @Override
                public void onStarted() {
                    String address = FileUtil.getIPAddress(getApplicationContext())+":"+configBean.getPort();
                    ServerManager.onServerStart(CoreService.this, address);
                }

                @Override
                public void onStopped() {
                    ServerManager.onServerStop(CoreService.this);
                }

                @Override
                public void onException(Exception e) {
                    ServerManager.onServerError(CoreService.this, e.getMessage());
                }
            })
            .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    /**
     * Start server.
     */
    private void startServer() {
        mServer.startup();
    }

    /**
     * Stop server.
     */
    private void stopServer() {
        mServer.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}