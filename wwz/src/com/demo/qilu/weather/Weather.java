/**
 * @author 神奇4+1
 * @version 1.0.0 创建时间：2012-08-08
 * 该类为本项目的天气查询功能数据处理。
 */

package com.demo.qilu.weather;

import android.graphics.drawable.Drawable;

public class Weather {
	
	private String imageUrl;
	
	private String low_temperature;
	
	private String high_temperature;
	
	private String day_of_week;
	
	private String condition;
	
	private Drawable image;

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getLow_temperature() {
		return low_temperature;
	}

	public void setLow_temperature(String low_temperature) {
		this.low_temperature = low_temperature;
	}

	public String getHigh_temperature() {
		return high_temperature;
	}

	public void setHigh_temperature(String high_temperature) {
		this.high_temperature = high_temperature;
	}

	public String getDay_of_week() {
		return day_of_week;
	}

	public void setDay_of_week(String day_of_week) {
		this.day_of_week = day_of_week;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
}
