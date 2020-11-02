package megvii.testfacepass.pa.adapter;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


import megvii.testfacepass.pa.R;



/**
 * Created by xingchaolei on 2017/12/5.
 */

public class UserListAdapter2 extends BaseAdapter {

    private List<String> mGroupNames;
    private LayoutInflater mLayoutInflater;
    private ItemDeleteButtonClickListener mItemDeleteButtonClickListener;

   // private Context context;
  //  private RequestOptions myOptions2 =null;


    public UserListAdapter2(List<String> data, Context context) {
        mGroupNames=data;
//        this.context=context;
//        myOptions2 = new RequestOptions()
//                .fitCenter()
//                .error(R.drawable.erroy_bg)
//                //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
//                .transform(new GlideRoundTransform(context, 20));
    }

    public List<String> getData() {

        return mGroupNames;
    }



    public void setOnItemDeleteButtonClickListener(ItemDeleteButtonClickListener listener) {
        mItemDeleteButtonClickListener = listener;
    }

    @Override
    public int getCount() {
        return mGroupNames == null ? 0 : mGroupNames.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroupNames == null ? null : mGroupNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_item_group_nameuser2, parent, false);
            holder = new ViewHolder();
            holder.groupNameTv =  convertView.findViewById(R.id.tv_group_name);
            holder.deleteGroupIv =  convertView.findViewById(R.id.iv_delete_group);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.deleteGroupIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemDeleteButtonClickListener != null) {
                    mItemDeleteButtonClickListener.OnItemDeleteButtonClickListener(position);
                }
            }
        });
        holder.groupNameTv.setText(mGroupNames.get(position));


        return convertView;
    }


    public static class ViewHolder {
        TextView groupNameTv;
        ImageView deleteGroupIv;
    }


    public interface ItemDeleteButtonClickListener {

        void OnItemDeleteButtonClickListener(int position);

    }
}
