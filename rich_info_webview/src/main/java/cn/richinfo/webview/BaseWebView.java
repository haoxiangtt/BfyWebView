package cn.richinfo.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author OuyangJinfu.
 * @version 1.0
 * @Date 2015/8/17
 * @Email 20386450@qq.com | ouyjf@giiso.com
 * @Description --> WebView基类.
 */
public abstract class BaseWebView extends WebView {

	protected static final  String TAG = "BaseWebView";
    
    protected static final boolean DEBUG = BuildConfig.DEBUG;

	private static final int HANDLER_WHAT_CALL_JS = 0;
    private static final String APP_CACAHE_DIRNAME = "/rich_webcache";//文件缓存目录

    private static final String USER_AGENT_1 = "rich";          //标识是否在wentianji应用内
    private static final String USER_AGENT_DOWN_4_2 = "rich_native_down_4.2"; //标识是否带有Android原生接口;是：“”，否
    private static final String JAVASCRIPT_INTERFACE_NAME = "android_js";

    private boolean isDestory = false;

//    protected BaseWebChromeClient mBaseWebChromeClient;
//    protected BaseWebViewClient mBaseWebClient;
    
    protected Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case HANDLER_WHAT_CALL_JS:
				callJS(""+msg.obj);
				break;

			default:
				break;
			}
    	}
    };
    
    public BaseWebView(Context context) {
       this(context, null);
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
		initWebView(context);

    }
	public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		initWebView(context);
	}

	@TargetApi(21)
	public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
		super(context, attrs, defStyleAttr, defStyleRes);
		initWebView(context);

	}
    
	public void initWebView(Context context) {
    	setBackgroundColor(Color.TRANSPARENT);
		setSaveEnabled(true);
		if(Build.VERSION.SDK_INT >= 19) {
			try {
				setWebContentsDebuggingEnabled(true);
			}catch (NoSuchMethodError error){
				Log.e(TAG,"NoSuchMethodError_setWebContentsDebuggingEnabled");
			}
		}
		if(Build.VERSION.SDK_INT >= 11){
			//Android的 Accessibility 服务漏洞导致应用远程代码执行
			removeJavascriptInterface("searchBoxJavaBridge_");
			removeJavascriptInterface("accessibility");
			removeJavascriptInterface("accessibilityTraversal");
		}
		setHorizontalScrollBarEnabled(false);
		setVerticalScrollBarEnabled(false);
		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		WebSettings webSettings = getSettings();

        webSettings.setJavaScriptEnabled(true);//允许调用JavaScript
		webSettings.setDatabaseEnabled(true);//启用数据库
		if (Build.VERSION.SDK_INT < 19) {
			String dir = context.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
			webSettings.setDatabasePath(dir);
		}
		webSettings.setPluginState(WebSettings.PluginState.ON);

		/**
		 * 用WebView显示图片，可使用这个参数 设置网页布局类型：
		 * 1、LayoutAlgorithm.NARROW_COLUMNS ：适应内容大小（默认设置）
		 * 2、LayoutAlgorithm.SINGLE_COLUMN : 适应屏幕，内容将自动缩放
		 */
//		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);//设置内容自适应屏幕大小
        webSettings.setLoadWithOverviewMode(true);//设置webview加载的页面的模式
		if(Build.VERSION.SDK_INT < 18) {
			webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染的优先级
		}
        webSettings.setSupportZoom(false);//页面是否允许缩放
        webSettings.setBuiltInZoomControls(false);//是否使用内置的缩放机制
		webSettings.setDisplayZoomControls(false);//设置是否显示放缩按钮

        //存储H5
		webSettings.setDomStorageEnabled(true);//允许使用本地存储
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式
		webSettings.setSaveFormData(true);//是否保存表单数据，默认值true。
		String appCacheDir = context.getApplicationContext().getCacheDir().getPath() + APP_CACAHE_DIRNAME;
		webSettings.setAppCachePath(appCacheDir);//设置缓存路径
		webSettings.setAppCacheEnabled(true);// 设置开启缓存
		if(Build.VERSION.SDK_INT < 18) {
			webSettings.setAppCacheMaxSize(1024 * 1024 * 10);// 设置缓冲大小
		}

		//启用cookie
		if (Build.VERSION.SDK_INT >= 21) {
			webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
			CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
		} else {
			CookieManager.getInstance().setAcceptCookie(true);
		}

		webSettings.setAllowFileAccess(true);// 允许访问文件
        //支持跨域访问文件( >=Build.VERSION_CODES.JELLY_BEAN )
        if (Build.VERSION.SDK_INT >= 16) {
			webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        

        //设置浏览器标识
        webSettings.setUserAgentString(webSettings.getUserAgentString()+ ";"+USER_AGENT_1
        	+ (Build.VERSION.SDK_INT  < 17 ?  ";" + USER_AGENT_DOWN_4_2 : ""));
//        webSettings.setUserAgentString(webSettings.getUserAgentString()+ " " +
// 			PAConfig.getConfig(Constant.Config.WEBVIEW_USER_AGENT));
                
        //add by ouyangjinfu 设置是否自动加载图片
//        webSettings.setLoadsImagesAutomatically(false);
//		if(Build.VERSION.SDK_INT >= 19) {
//			webSettings.setLoadsImagesAutomatically(true);
//		} else {
//			webSettings.setLoadsImagesAutomatically(false);
//		}

		if (Build.VERSION.SDK_INT < 19) {
			DisplayMetrics metrics = new DisplayMetrics();
			((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int mDensity = metrics.densityDpi;
			if (mDensity == 240) {
				webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
			} else if (mDensity == 160) {
				webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
			} else if (mDensity == 120) {
				webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
			} else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
				webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
			} else if (mDensity == DisplayMetrics.DENSITY_TV) {
				webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
			} else {
				webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
			}
		}

		//设置客户端
		/*mBaseWebChromeClient = new BaseWebChromeClient(context,this);
		setWebChromeClient(mBaseWebChromeClient);
		setDownloadListener(mBaseWebChromeClient);

		mBaseWebClient = new BaseWebViewClient(context,this);
		setWebViewClient(mBaseWebClient);*/
    }
    
    @Override
    public void destroy() {
        super.destroy();
        isDestory = true;
    }


    @Override
    public void loadUrl(String url, Map<String, String> extraHeaders) {
        if(isDestory){
        	if(DEBUG){ Log.w(TAG, "webview已经销毁，无法执行该操作"); }
            return ;
        }
        super.loadUrl(url, extraHeaders);
    }
    
    @Override
    public void loadUrl(String url) {
    	if(isDestory){
    		if(DEBUG){ Log.w(TAG, "webview已经销毁，无法执行该操作");}
            return ;
        }
    	super.loadUrl(url);
    }
    
    /**
     * 添加JS接口安全模式.
     * @param object
     */
    /*public void addJavascriptInterfaceSafe(Object object) {
    	mBaseWebChromeClient.addJavascriptInterfaceSafe(object);
    }*/
    
    /**
     * '
     *  方法名称: isDestory
     *  功能描述: 返回当前webView是否被销毁. 
     *  修改记录:
     *  	修改者:   OUYANGXINGYU198 
     *  	修改日期: 2015-5-16
     *  	修改内容: 创建.
     *  @return
     */
	public boolean isDestory() {
		return isDestory;
	}
    
    
    /**
     * 调用js方法。
     * @param callback
     * @param data
     */
    public void callJS(String callback,String data){
    	callJS(callback,data,null);
    }
    
    /**
     * 调用js方法。
     * @param callback
     * @param data
     * @param errorCode
     */
	public void callJS(String callback,String data,String errorCode){
		if(TextUtils.isEmpty(callback)){
			if(DEBUG){ Log.w(TAG, "callJS callback is isEmpty！"+callback); }
    		return;
    	}
    	JSONObject json = null;
    	String callJSData = null;
    	if(DEBUG){ Log.d(TAG, "callJS: "+data); }
    	if(!TextUtils.isEmpty(data)){
    		try {
    			json = new JSONObject(data);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		if(json != null){
    			callJSData = "javascript:$$.platformAdapter.callback('" + callback + "',"+getErrorObject(errorCode)+"," + json + ")";
    		}else{
    			data = data.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\\\"").replaceAll("\\n", "\\\\n").replaceAll("\\r", "").replaceAll("\\\'", "\\\\\\\'");
    			callJSData = "javascript:$$.platformAdapter.callback('" + callback + "',"+getErrorObject(errorCode)+",'" + data + "')";
    		}
    	}else{
    		callJSData = "javascript:$$.platformAdapter.callback('" + callback + "',"+getErrorObject(errorCode)+",'" + data + "')";
    	}
    	if(DEBUG){ Log.i(TAG, "callJS:"+callJSData); }
    	Message msg = Message.obtain(mHandler);
    	msg.what = HANDLER_WHAT_CALL_JS;
    	msg.obj = callJSData;
    	msg.sendToTarget();
    }
    
	/**
	 * 直接调js的window.下面的方法
	 * @param callback
	 */
	public void callJSDirectly(String callback) {
		if(TextUtils.isEmpty(callback)){
			return;
		}
		String callJSData = "javascript:" + callback;
		if(DEBUG){ Log.i(TAG, "callJS:"+callJSData); }
		Message msg = Message.obtain(mHandler);
		msg.what = HANDLER_WHAT_CALL_JS;
		msg.obj = callJSData;
		msg.sendToTarget();
	}

	private void callJS(String javaScriptData){
		if(isDestory){
			if(DEBUG){ Log.e(TAG, "webview已经销毁，callJS 无法执行该操作 "+javaScriptData); }
			return;
        }
		super.loadUrl(javaScriptData);
	}

	
	/**
	 * 
	 * @param errorCode
	 * @return
	 */
	private JSONObject getErrorObject(String errorCode){
		if(TextUtils.isEmpty(errorCode)){
			return null;
		}
		JSONObject json = new JSONObject();
		try {
			json.put("code", errorCode);
			json.put("msg", errorCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
}
