package com.medusa.gruul.logistics.util.express.kuaidihelp;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.kuaidi100.sdk.api.LabelV2;
import com.kuaidi100.sdk.api.QueryTrack;
import com.kuaidi100.sdk.contant.ApiInfoConstant;
import com.kuaidi100.sdk.contant.PrintType;
import com.kuaidi100.sdk.core.IBaseClient;
import com.kuaidi100.sdk.pojo.HttpResult;
import com.kuaidi100.sdk.request.ManInfo;
import com.kuaidi100.sdk.request.PrintReq;
import com.kuaidi100.sdk.request.QueryTrackParam;
import com.kuaidi100.sdk.request.QueryTrackReq;
import com.kuaidi100.sdk.request.labelV2.OrderReq;
import com.kuaidi100.sdk.request.labelV2.RepeatPrintReq;
import com.kuaidi100.sdk.response.QueryTrackData;
import com.kuaidi100.sdk.utils.SignUtils;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.logistics.model.dto.manager.express.ExpressInfoDto;
import com.medusa.gruul.logistics.model.vo.LogisticsExpressAddressVo;
import com.medusa.gruul.order.api.entity.OrderSetting;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * <p>
 *  快递物流接口
 * </p>
 *
 * @author lcysike
 * @since 2024-03-12
 */
@Slf4j
public class ExpressHelp {

    public static Logger logger = LoggerFactory.getLogger(ExpressHelp.class);

    /**
     * 顺丰公司快递码
     */
    public static final String SFCODE = "sf";

    /**
     * 调用打印机生成电子面单
     *
     * @param expressInfo 订单传输对象
     * @param orderSetting 基础设置信息
     */
    public static JSONObject getExpressPrint(ExpressInfoDto expressInfo, LogisticsExpressAddressVo logisticsExpressAddressVo, OrderSetting orderSetting){
        try {
            ManInfo recManInfo  = new ManInfo();
            recManInfo.setName(expressInfo.getName());
//            recManInfo.setCompany(expressInfo.getReceiverCompany());
            recManInfo.setMobile(expressInfo.getMobile());
            recManInfo.setProvince(expressInfo.getProvince());
            recManInfo.setCity(expressInfo.getCity());
            recManInfo.setDistrict(expressInfo.getDistrict());
            recManInfo.setPrintAddr(expressInfo.getAddress());
            recManInfo.setAddr(expressInfo.getAddress());

            ManInfo sendManInfo = new ManInfo();
            sendManInfo.setName(logisticsExpressAddressVo.getReceiveName());
            sendManInfo.setMobile(logisticsExpressAddressVo.getReceivePhone());
//            sendManInfo.setMobile(StrUtil.hide(expressInfo.getSenderTel(),CommonConstants.NUMBER_THREE,CommonConstants.NUMBER_SEVEN));
            sendManInfo.setProvince(logisticsExpressAddressVo.getProvince());
            sendManInfo.setCity(logisticsExpressAddressVo.getCity());
            sendManInfo.setDistrict(logisticsExpressAddressVo.getCountry());
            sendManInfo.setPrintAddr(logisticsExpressAddressVo.getAddress());

            OrderReq orderReq = new OrderReq();
            //到付修改付款方式
//            if(CommonConstants.STRING_ONE.equals(expressInfo.getPaymentMode())){
//                orderReq.setPayType("CONSIGNEE");
//            }
            orderReq.setKuaidicom(logisticsExpressAddressVo.getExpressCode());
            orderReq.setCount(CommonConstants.NUMBER_ONE);
            orderReq.setSiid(expressInfo.getPrintCode());
//            orderReq.setTempId(sysBaseInfo.getKdHundredTempId());
            orderReq.setSendMan(sendManInfo);
            orderReq.setRecMan(recManInfo);
            orderReq.setPrintType(PrintType.CLOUD);
            orderReq.setKuaidinum(expressInfo.getOrderId());
            if(SFCODE.equals(expressInfo.getShipperType())){
                orderReq.setExpType("顺丰标快");
            }
            if(StringUtils.isBlank(orderSetting.getKdAppId())){
                throw new ServiceException("快递基础信息不完善，请联系技术人员!", SystemCode.FAILURE_CODE);
            }
            orderReq.setPartnerId(orderSetting.getKdAppId());
            orderReq.setPartnerKey(orderSetting.getKdHundredKey());
            orderReq.setNet(orderSetting.getKdHundredSecret());
            //TODO 暂时先从设置里面取 联调的时候再确认调整从打印机里面获取 或者单独弄一个模版库
            orderReq.setThirdTemplateURL(expressInfo.getKdHundredTempId());
            orderReq.setHeight("130");
            orderReq.setWidth("70");
            String param = new Gson().toJson(orderReq);
            String t = System.currentTimeMillis() + "";

            PrintReq printReq = new PrintReq();
            printReq.setT(t);
            printReq.setKey(orderSetting.getKdHundredKey());
            printReq.setSign(SignUtils.printSign(param,t,orderSetting.getKdHundredKey(),orderSetting.getKdHundredSecret()));
            printReq.setMethod(ApiInfoConstant.ELECTRONIC_ORDER_PRINT_METHOD);
            printReq.setParam(param);
            log.info(STR."打印请求接口参数：\{JSONUtil.toJsonStr(printReq)}");
            IBaseClient baseClient = new LabelV2();
            HttpResult httpResult = baseClient.execute(printReq);
            log.info(JSONUtil.toJsonStr(STR."打印接口返回值：\{httpResult}"));
            JSONObject jsonObject = JSONUtil.parseObj(httpResult.getBody());
            if (jsonObject.isEmpty() || !CommonConstants.STRING_SUCCESS_CODE.equals(jsonObject.getStr("code"))) {
                throw new ServiceException(String.valueOf(jsonObject.get("message")), SystemCode.FAILURE_CODE);
            }
            return jsonObject.getJSONObject("data");
        }catch (Exception e){
            throw new ServiceException(e.getMessage(), SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    /**
     * 云打印复打
     *
     * @throws Exception
     */
    public static void  expressPrintOld(ExpressInfoDto expressInfo, OrderSetting orderSetting) {
        try {
            RepeatPrintReq repeatPrintReq = new RepeatPrintReq();
            repeatPrintReq.setTaskId(expressInfo.getTaskId());
            String param = new Gson().toJson(repeatPrintReq);
            String t = STR."\{System.currentTimeMillis()}";
            PrintReq printReq = new PrintReq();
            printReq.setT(t);
            printReq.setKey(orderSetting.getKdHundredKey());
            printReq.setSign(SignUtils.printSign(param,t,orderSetting.getKdHundredKey(),orderSetting.getKdHundredSecret()));
            printReq.setMethod(ApiInfoConstant.CLOUD_PRINT_OLD_METHOD);
            printReq.setParam(param);

            IBaseClient baseClient = new LabelV2();
            HttpResult httpResult = baseClient.execute(printReq);
            JSONObject jsonObject = JSONUtil.parseObj(httpResult.getBody());
            if (jsonObject.isEmpty() || !CommonConstants.STRING_SUCCESS_CODE.equals(jsonObject.getStr("code"))) {
                throw new ServiceException(String.valueOf(jsonObject.get("message")), SystemCode.FAILURE_CODE);
            }
        }catch (Exception e){
            throw new ServiceException("打印接口调用失败！", SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    public static List<QueryTrackData> getExpressWayBill(ExpressInfoDto expressInfo, OrderSetting orderSetting){
        QueryTrackReq queryTrackReq = new QueryTrackReq();
        QueryTrackParam queryTrackParam = new QueryTrackParam();
        queryTrackParam.setCom(expressInfo.getShipperType());
        queryTrackParam.setNum(expressInfo.getDeliverySn());
        queryTrackParam.setPhone(expressInfo.getMobile());
        String param = JSONUtil.toJsonStr(queryTrackParam);

        queryTrackReq.setParam(param);
        queryTrackReq.setCustomer(orderSetting.getKdHundredCustomer());
        queryTrackReq.setSign(SignUtils.querySign(param , orderSetting.getKdHundredKey(), orderSetting.getKdHundredCustomer()));

        IBaseClient baseClient = new QueryTrack();
        HttpResult httpResult;
        try {
            httpResult = baseClient.execute(queryTrackReq);
        } catch (Exception e) {
            throw new ServiceException("物流信息查询接口调用失败！", SystemCode.DATA_ADD_FAILED.getCode());
        }
        JSONObject jsonObject = JSONUtil.parseObj(httpResult.getBody());
        if (jsonObject.isEmpty() || !CommonConstants.STRING_SUCCESS_CODE.equals(jsonObject.getStr("status"))) {
            throw new ServiceException(String.valueOf(jsonObject.get("message")), SystemCode.FAILURE_CODE);
        }
        return JSONUtil.toList(jsonObject.getStr("data"), QueryTrackData.class);
    }

}
