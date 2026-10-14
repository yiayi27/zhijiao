package com.medusa.gruul.oss.ueditor.define;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 文件类型
 * </p>
 *
 * @author alan
 * @since 2020/3/31
 */
public class MIMEType {

	public static final Map<String, String> TYPES = new HashMap<String, String>(){{
		put( "image/gif", ".gif" );
		put( "image/jpeg", ".jpg" );
		put( "image/jpg", ".jpg" );
		put( "image/png", ".png" );
		put( "image/bmp", ".bmp" );
	}};
	
	public static String getSuffix ( String mime ) {
		return MIMEType.TYPES.get( mime );
	}
	
}
