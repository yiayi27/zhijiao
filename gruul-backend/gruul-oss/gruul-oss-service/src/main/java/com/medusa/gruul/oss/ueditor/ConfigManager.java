package com.medusa.gruul.oss.ueditor;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.oss.ueditor.define.ActionMap;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理器
 *
 * @author hancong03@baidu.com
 */
public final class ConfigManager {

    private static final String CONFIG_FILE_NAME = "config.json";
    private final static String SCRAWL_FILE_NAME = "scrawl";
    private final static String REMOTE_FILE_NAME = "remote";
    private final String rootPath;
    private final String originalPath;
    private String contextPath = null;
    private String parentPath = null;
    private JSONObject jsonConfig = null;

    /**
     * 通过一个给定的路径构建一个配置管理器， 该管理器要求地址路径所在目录下必须存在config.properties文件
     */
    private ConfigManager(String rootPath, String contextPath, String uri) throws IOException {

        rootPath = rootPath.replace("\\", "/");

        this.rootPath = rootPath;
        this.contextPath = contextPath;

        if (StringUtils.isNotBlank(contextPath)) {
            this.originalPath = this.rootPath + uri.substring(contextPath.length());
        } else {
            this.originalPath = this.rootPath + uri;
        }

        this.initEnv();
    }

    /**
     * 配置管理器构造工厂
     *
     * @param rootPath    服务器根路径
     * @param contextPath 服务器所在项目路径
     * @param uri         当前访问的uri
     * @return 配置管理器实例或者null
     */
    public static ConfigManager getInstance(String rootPath, String contextPath, String uri) {

        try {
            return new ConfigManager(rootPath, contextPath, uri);
        } catch (Exception e) {
            return null;
        }

    }

    public boolean valid() {
        return this.jsonConfig != null;
    }

    public JSONObject getAllConfig() {

        return this.jsonConfig;

    }

    public Map<String, Object> getConfig(int type) {

        Map<String, Object> conf = new HashMap<>(CommonConstants.NUMBER_TEN);
        String savePath = null;

        switch (type) {

            case ActionMap.UPLOAD_FILE:
                conf.put("isBase64", "false");
                conf.put("maxSize", this.jsonConfig.getLong("fileMaxSize"));
                conf.put("allowFiles", this.getArray("fileAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getStr("fileFieldName"));
                savePath = this.jsonConfig.getStr("filePathFormat");
                break;

            case ActionMap.UPLOAD_IMAGE:
                conf.put("isBase64", "false");
                conf.put("maxSize", this.jsonConfig.getLong("imageMaxSize"));
                conf.put("allowFiles", this.getArray("imageAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getStr("imageFieldName"));
                savePath = this.jsonConfig.getStr("imagePathFormat");
                break;

            case ActionMap.UPLOAD_VIDEO:
                conf.put("maxSize", this.jsonConfig.getLong("videoMaxSize"));
                conf.put("allowFiles", this.getArray("videoAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getStr("videoFieldName"));
                savePath = this.jsonConfig.getStr("videoPathFormat");
                break;

            case ActionMap.UPLOAD_SCRAWL:
                conf.put("filename", ConfigManager.SCRAWL_FILE_NAME);
                conf.put("maxSize", this.jsonConfig.getLong("scrawlMaxSize"));
                conf.put("fieldName", this.jsonConfig.getStr("scrawlFieldName"));
                conf.put("isBase64", "true");
                savePath = this.jsonConfig.getStr("scrawlPathFormat");
                break;

            case ActionMap.CATCH_IMAGE:
                conf.put("filename", ConfigManager.REMOTE_FILE_NAME);
                conf.put("filter", this.getArray("catcherLocalDomain"));
                conf.put("maxSize", this.jsonConfig.getLong("catcherMaxSize"));
                conf.put("allowFiles", this.getArray("catcherAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getStr("catcherFieldName") + "[]");
                savePath = this.jsonConfig.getStr("catcherPathFormat");
                break;

            case ActionMap.LIST_IMAGE:
                conf.put("allowFiles", this.getArray("imageManagerAllowFiles"));
                conf.put("dir", this.jsonConfig.getStr("imageManagerListPath"));
                conf.put("count", this.jsonConfig.getInt("imageManagerListSize"));
                break;

            case ActionMap.LIST_FILE:
                conf.put("allowFiles", this.getArray("fileManagerAllowFiles"));
                conf.put("dir", this.jsonConfig.getStr("fileManagerListPath"));
                conf.put("count", this.jsonConfig.getInt("fileManagerListSize"));
                break;
            default:
                savePath = "";

        }

        conf.put("savePath", savePath);
        conf.put("rootPath", this.rootPath);

        return conf;

    }

    private void initEnv() throws IOException {

        File file = new File(this.originalPath);

        if (!file.isAbsolute()) {
            file = new File(file.getAbsolutePath());
        }

        this.parentPath = file.getParent();

        String configContent = this.readFile(this.getConfigPath());

        try {
            JSONObject jsonConfig = new JSONObject(configContent);
            this.jsonConfig = jsonConfig;
        } catch (Exception e) {
            this.jsonConfig = null;
        }

    }

    private String getConfigPath() {
        try {
            //获取classpath下的config.json路径
            return this.getClass().getClassLoader().getResource("config.json").toURI().getPath();
        } catch (URISyntaxException e) {
            return null;
        }
    }


    private String[] getArray(String key) {
        JSONArray jsonArray = this.jsonConfig.getJSONArray(key);
        String[] result = new String[jsonArray.size()];
        for (int i = 0, len = jsonArray.size(); i < len; i++) {
            result[i] = jsonArray.getStr(i);
        }

        return result;

    }

    private String readFile(String path) throws IOException {

        StringBuilder builder = new StringBuilder();

        try {

            InputStreamReader reader = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
            BufferedReader bfReader = new BufferedReader(reader);

            String tmpContent = null;

            while ((tmpContent = bfReader.readLine()) != null) {
                builder.append(tmpContent);
            }

            bfReader.close();

        } catch (UnsupportedEncodingException e) {
            // 忽略
        }

        return this.filter(builder.toString());

    }

    private String filter(String input) {
        return input.replaceAll("/\\*[\\s\\S]*?\\*/", "");

    }

}
