package cn.richinfo.richinfowebview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * <pre>
 * @copyright  : Copyright ©2004-2018 版权所有　彩讯科技股份有限公司
 * @company    : 彩讯科技股份有限公司
 * @author     : OuyangJinfu
 * @e-mail     : ouyangjinfu@richinfo.cn
 * @createDate : 2017/6/14 0014
 * @modifyDate : 2017/6/14 0014
 * @version    : 1.0
 * @desc       : 通用网页加载控件
 * </pre>
 */

public class RichInfoWebView extends BaseWebView {

    public RichInfoWebView(Context context) {
        this(context, null);
    }

    public RichInfoWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichInfoWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RichInfoWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
