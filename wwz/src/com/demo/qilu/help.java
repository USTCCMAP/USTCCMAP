
	/**
	 * @author wwz
	 * @version 1.0.0 ����ʱ�䣺2014
	 * ����Ϊ����Ŀ�ġ����������ܡ�
	 * ��Ҫ�����û������ʹ�øõ�ͼ���
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
			 * ��������
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
	        	txttitle.setText("ʹ��˵��");
	        	SpannableString txt = new SpannableString("   �ڲ˵���\n   ������ͼ\n   ��ʾ���ݿ�ݴ���\n   ��ѡ����Ӧ����\n   ����ʾ�������ɡ�\n" +
	        			"   ������кõ����\n   ��ӭ�µ�:\n   1215599354@qq.com");
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
