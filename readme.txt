webview控件说明：
	1、此控件已将WebSettings配置到最佳状态；
	2、集成方式：可以直接源码集成，或者使用项目中gradle任务生成一个jar再导入；
	3、使用方式如下：
	<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
		xmlns:app="http://schemas.android.com/apk/res-auto"
		xmlns:tools="http://schemas.android.com/tools"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		tools:context="cn.richinfo.demo.MainActivity">

		<cn.richinfo.webview.RichInfoWebView
			android:id="@+id/webview"
			android:layout_width="match_parent"
			android:layout_height="match_parent"/>

	</RelativeLayout>
	和使用原生的WebView无差别。
	
作者：ouyangjinfu