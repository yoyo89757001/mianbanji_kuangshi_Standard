package megvii.testfacepass.pa.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NV21ToBitmap {

    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    // private Type.Builder yuvType;
    // private Allocation in, out;
    public NV21ToBitmap(Context context) {
        rs = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }

    public Bitmap nv21ToBitmap(byte[] nv21, int width, int height){
        //  if (yuvType == null){
        Type.Builder  yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        //   }
        in.copyFrom(nv21);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bmpout);

        //  Allocation.createFromBitmap(rs,bmpout);
        //  in.destroy();
        //   out.destroy();
        return bmpout;
    }

    /** 保存方法 */
    public boolean saveBitmap(Bitmap bm,String path,String picName) {
      //  Log.e(TAG, "保存图片");
        File f = new File(path, picName);
        if (f.exists()) {
            Log.d(TAG, "f.delete():" + f.delete());
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 70, out);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
