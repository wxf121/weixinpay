
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
@CrossOrigin
public class TestController {
    @RequestMapping("/show")
    public void testQRCode(HttpServletResponse response) throws IOException {
        final BufferedImage bufferedImage = ZxingUtil.createImage("hello, world", 400, 400);
        ImageIO.write(bufferedImage, "JPEG", response.getOutputStream());
    }

    @ResponseBody
    @RequestMapping("/order")
    public ResultData order(String id){
        return ResultData.createSuccessJsonResult(id);
    }
//
//    @RequestMapping("/successPage")
//    public String successPage(){
//        return  "/success";
//    }
//
    @ResponseBody
    @GetMapping("/pay")
    public String payOrder(String id){
//        WebSocket.sendMessage(id, "{\\\"code\\\":\\\"10000\\\", \\\"desc\\\":\\\"支付成功\\\", \\\"url\\\":\\\"successPage\\\"}", null);
        WebSocket.sendMessage(id,"{\"code\":10000, \"desc\":\"支付成功,五秒钟之后跳转页面\",\"url\":\"success\"}",null);
        return "ok";
    }
}
