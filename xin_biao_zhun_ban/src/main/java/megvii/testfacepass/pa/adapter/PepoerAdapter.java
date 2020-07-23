package megvii.testfacepass.pa.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.Subject;


public class PepoerAdapter extends BaseQuickAdapter<Subject, BaseViewHolder> implements LoadMoreModule {
   private FacePassHandler facePassHandler;

    public PepoerAdapter(int layoutResId, @Nullable List<Subject> data,FacePassHandler facePassHandler) {
        super(layoutResId, data);
        this.facePassHandler=facePassHandler;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Subject taskBean) {
        try {
            baseViewHolder.setText(R.id.name,taskBean.getName());
            baseViewHolder.setImageResource(R.id.touxiang,R.drawable.mianbji209);
        }catch (Exception e){
            e.printStackTrace();
        }



//        try {
//            Glide.with(getContext())
//                    .load(new BitmapDrawable(getContext().getResources(),facePassHandler.getFaceImage(taskBean.getTeZhengMa().getBytes())))
//                    .apply(RequestOptions.bitmapTransform(new RoundedCorners( 5)))
//                    .into((ImageView) baseViewHolder.getView(R.id.touxiang));
//        } catch (FacePassException e) {
//            e.printStackTrace();
//        }


        //RequestOptions.bitmapTransform(new CircleCrop())//圆形
        //RequestOptions.bitmapTransform(new RoundedCorners( 5))//圆角
    }
}
