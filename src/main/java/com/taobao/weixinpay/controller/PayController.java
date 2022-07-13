
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Controller
public class PayController {
    @CrossOrigin
    @ResponseBody
    @PostMapping("/pay")
    public ResultData pay(String name, Integer price) throws Exception {
        final long orderId = IdWorker.worker.nextId();
        String url = PayCommonUtil.weixin_pay("1", name, String.valueOf(orderId));//获取微信返回的二维码对应的短地址
        System.out.println("url" + url);
        Order order = new Order();
        order.setOrderId(String.valueOf(orderId));
        order.setUrl(url);
//        model.addAttribute("orderId", orderId);
//        model.addAttribute("url", url);
        return ResultData.createSuccessJsonResult(order);
//        return "/order";
    }


    @RequestMapping("/paySuccess")
    public void payOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("payOrder===方法执行了");
        String writeContent="默认支付失败";
//        String path = request.getServletContext().getRealPath("file");
//        File file = new File(path);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        FileOutputStream fileOutputStream = new FileOutputStream(path+"/result.txt", true);
        //读取参数
        InputStream inputStream ;
        StringBuffer sb = new StringBuffer();
        inputStream = request.getInputStream();
        String s ;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((s = in.readLine()) != null){
            sb.append(s);
        }
        in.close();
        inputStream.close();

        System.out.println("========" + sb.toString());

        //解析xml成map
        Map<String, String> m = new HashMap<String, String>();
        m = XMLUtil.doXMLParse(sb.toString());

        //过滤空 设置 TreeMap
        SortedMap<Object,Object> packageParams = new TreeMap<Object,Object>();
        Iterator it = m.keySet().iterator();
        while (it.hasNext()) {
            String parameter = (String) it.next();
            String parameterValue = m.get(parameter);

            String v = "";
            if(null != parameterValue) {
                v = parameterValue.trim();
            }
            packageParams.put(parameter, v);
        }

        // 账号信息
        String key = PayConfigUtil.API_KEY; // key

        System.err.println(packageParams);
        String out_trade_no = (String)packageParams.get("out_trade_no");
        //判断签名是否正确
        if(PayCommonUtil.isTenpaySign("UTF-8", packageParams,key)) {
            //------------------------------
            //处理业务开始
            //------------------------------
            String resXml = "";
            if("SUCCESS".equals((String)packageParams.get("result_code"))){
                // 这里是支付成功
                //////////执行自己的业务逻辑////////////////
                String mch_id = (String)packageParams.get("mch_id");
                String openid = (String)packageParams.get("openid");
                String is_subscribe = (String)packageParams.get("is_subscribe");
                // String out_trade_no = (String)packageParams.get("out_trade_no");

                String total_fee = (String)packageParams.get("total_fee");

//                System.err.println("mch_id:"+mch_id);
//                System.err.println("openid:"+openid);
//                System.err.println("is_subscribe:"+is_subscribe);
//                System.err.println("out_trade_no:"+out_trade_no);
//                System.err.println("total_fee:"+total_fee);

                //////////执行自己的业务逻辑////////////////

//                System.err.println("支付成功");
                writeContent = "订单:" + out_trade_no + "支付成功";
                //通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";


                String jsonString = "{\"code\":\"10000\", \"desc\":\""+writeContent+",5秒后跳转页面\", \"url\":\"success\"}";

                //实际开发中,应该返回json 数据,这样页面可以解析,可以根据结果来决定做什么,此处只返回支付成功
                WebSocket.sendMessage(out_trade_no,jsonString,null);

            } else {
                writeContent = "订单"+out_trade_no+"支付失败,错误信息：" + packageParams.get("err_code");
                System.err.println("订单"+out_trade_no+"支付失败,错误信息：" + packageParams.get("err_code"));
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }
            //------------------------------
            //处理业务完毕
            //------------------------------
            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        } else{
            writeContent = "订单"+out_trade_no+"通知签名验证失败,支付失败";
            System.err.println("通知签名验证失败");
        }
//        fileOutputStream.write(writeContent.getBytes());
//        fileOutputStream.close();
    }
}
