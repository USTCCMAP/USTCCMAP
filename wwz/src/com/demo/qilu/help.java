
	/**
	 * @author wwz
	 * @version 1.0.0 创建时间：2014
	 * 该类为本项目的“帮助”功能。
	 * 主要帮助用户，如何使用该地图软件
	 */
	package com.demo.qilu;

	import com.demo.wwz.R;

	import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

	public class help extends Activity {

			/**
			 * 关于我们
			 */
	        @Override
	        public void onCreate(Bundle savedInstanceState) {
	            super.onCreate(savedInstanceState);
	            setContentView(R.layout.help);
	            initview();
	       }
	        
	        void initview(){
	        	TextView txttitle = (TextView) findViewById(R.id.help_title);
	        	TextView txttext = (TextView) findViewById(R.id.help_text);
	        	Button btn = (Button)findViewById(R.id.btn_help_exit);
	        	txttitle.setText("使用说明");
	        	SpannableString txt = new SpannableString("   在菜单或\n   长按地图\n   显示气泡快捷窗口\n   中选择相应功能\n   按提示操作即可。\n" +
	        			"   如果你有好的意见\n   欢迎致电:\n   1215599354@qq.com");
				txt.setSpan(new ForegroundColorSpan(Color.RED), txt.length()-17,
						txt.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				txttext.setText(txt);
        	
	        	btn.setOnClickListener(new View.OnClickListener() {
	    			@Override
	    			public void onClick(View v) {
	    				finish();
	    			}
	    		});
	        }
	        
	}
