package megvii.testfacepass.pa;


import android.app.Activity;
import android.content.Context;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.util.ArrayList;

import megvii.testfacepass.pa.utils.UnCeHandler;

public class SampleApplication extends TinkerApplication {


    public SampleApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "megvii.testfacepass.pa.MyApplication",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }


}