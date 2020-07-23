package megvii.testfacepass.pa.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import megvii.testfacepass.pa.R;


/**
 * The type Commom dialog.
 */
public class CommomDialog extends Dialog implements View.OnClickListener{
    private TextView contentTxt;
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;

    private Context mContext;
    private String content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;

    /**
     * Instantiates a new Commom dialog.
     *
     * @param context the context
     */
    public CommomDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    /**
     * Instantiates a new Commom dialog.
     *
     * @param context    the context
     * @param themeResId the theme res id
     * @param content    the content
     */
    public CommomDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    /**
     * Instantiates a new Commom dialog.
     *
     * @param context    the context
     * @param themeResId the theme res id
     * @param content    the content
     * @param listener   the listener
     */
    public CommomDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }



    /**
     * Instantiates a new Commom dialog.
     *
     * @param context        the context
     * @param cancelable     the cancelable
     * @param cancelListener the cancel listener
     */
    protected CommomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    /**
     * Set title commom dialog.
     *
     * @param title the title
     * @return the commom dialog
     */
    public CommomDialog setTitle(String title){
        this.title = title;
        return this;
    }

    /**
     * Set positive button commom dialog.
     *
     * @param name the name
     * @return the commom dialog
     */
    public CommomDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    /**
     * Set negative button commom dialog.
     *
     * @param name the name
     * @return the commom dialog
     */
    public CommomDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_help);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        assert window != null;
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
        // window.setWindowAnimations(R.style.mystyle);  //添加动画
        initView();
    }

    private void initView(){
        contentTxt = (TextView)findViewById(R.id.failure_content);
        titleTxt = (TextView)findViewById(R.id.failure_title);
        submitTxt = (TextView)findViewById(R.id.failure_ok);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView)findViewById(R.id.failure_back);
        cancelTxt.setOnClickListener(this);

        contentTxt.setText(content);
        if(!TextUtils.isEmpty(positiveName)){
            submitTxt.setText(positiveName);
        }

        if(!TextUtils.isEmpty(negativeName)){
            cancelTxt.setText(negativeName);
        }

        if(!TextUtils.isEmpty(title)){
            titleTxt.setText(title);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.failure_back:
                if(listener != null){
                    listener.onClick(this, false);
                }
                this.dismiss();
                break;
            case R.id.failure_ok:
                if(listener != null){
                    listener.onClick(this, true);
                }
                break;
        }
    }

    /**
     * The interface On close listener.
     */
    public interface OnCloseListener{
        /**
         * On click.
         *
         * @param dialog  the dialog
         * @param confirm the confirm
         */
        void onClick(Dialog dialog, boolean confirm);
    }
}
