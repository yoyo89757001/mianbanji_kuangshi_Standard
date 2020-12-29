package megvii.testfacepass.pa.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.adapter.UserListAdapter;
import megvii.testfacepass.pa.beans.BaoCunBean;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.utils.DBUtils;
import megvii.testfacepass.pa.utils.FacePassUtil;
import megvii.testfacepass.pa.utils.SettingVar;


public class UserListActivity extends Activity implements UserListAdapter.ItemDeleteButtonClickListener {


    private ListView listView;
    private UserListAdapter adapter;
   // private UserListAdapter2 adapter2;
    private final List<Subject> subjectList=new ArrayList<>();
    private TextView zongrenshu;
    private EditText editText;
    //private ZLoadingDialog zLoadingDialog;
   // private RealmResults<Subject> subjectRealmResults=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        EventBus.getDefault().register(this);
        ImageView fh=findViewById(R.id.fanhui);
        fh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        zongrenshu=findViewById(R.id.renshu);
        listView=findViewById(R.id.recyle);
        editText=findViewById(R.id.sousuo);

        FacePassHandler.initSDK(getApplicationContext());
        FacePassUtil util = new FacePassUtil();
        util.init(UserListActivity.this, getApplicationContext(), SettingVar.faceRotation, MMKV.defaultMMKV().decodeParcelable("saveBean", BaoCunBean.class));

//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String ss=s.toString();
//                if (!ss.equals("")){
//                    subjectLazyList=subjectBox.query().contains(Subject_.name,ss)
//                            .build().findLazy();
//                    if (subjectLazyList.size()>0) {
//                        adapter.notify();
//                        Log.d("UserListActivity", "subjectLazyList.size():" + subjectLazyList.size());
//                    }
//
//                }else {
//                    subjectLazyList= subjectBox.query().build().findLazy();
//                    Log.d("UserListActivity", "subjectList.size():" + subjectLazyList.size());
//                    adapter.notifyDataSetChanged();
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });








    }



    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {

        if (event.equals("mFacePassHandler")) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    List<Subject> huiFuBeanList = DBUtils.getSubjectDao().getAllSubject();
                    for (Subject subject : huiFuBeanList) {
                        Subject sb = new Subject();
                        sb.setName(subject.getName());
                        sb.setId(subject.getId());
                        sb.setTeZhengMa(subject.getTeZhengMa());
                        sb.setFaceIds1(subject.getFaceIds1());
                        sb.setFaceIds2(subject.getFaceIds2());
                        subjectList.add(sb);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter=new UserListAdapter(subjectList,UserListActivity.this,MyApplication.myApplication.getFacePassHandler());
                            adapter.setOnItemDeleteButtonClickListener(UserListActivity.this);
                            listView.setAdapter(adapter);
                            zongrenshu.setText("总人数:"+subjectList.size());
                        }
                    });
                }
            }).start();
        }



    }


    //如果输入法在窗口上已经显示，则隐藏，反之则显示
    private   void showOrHide(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public void OnItemDeleteButtonClickListener(final int position) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(UserListActivity.this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("UserListActivity", "deleteFace:" + MyApplication.myApplication.getFacePassHandler().deleteFace(subjectList.get(position).getTeZhengMa().getBytes()));
                            } catch (FacePassException e) {
                                Log.d("UserListActivity", e.getMessage()+"deleteFace异常");
                            }
                            DBUtils.getSubjectDao().delete(subjectList.get(position));
                            subjectList.remove(position);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    zongrenshu.setText("总人数:"+subjectList.size());
                                }
                            });
                        }
                    }).start();
                }catch (Exception e){
                    Log.d("UserListActivity", e.getMessage()+"删除异常");
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMessage("你确定要删除吗？");
        builder.setTitle("温馨提示");
        builder.show();

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (MyApplication.myApplication.getFacePassHandler()!=null)
            MyApplication.myApplication.getFacePassHandler().release();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        zongrenshu.setFocusableInTouchMode(true);//解决clearFocus无效
        editText.clearFocus();
    }

}
