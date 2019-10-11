package doc.controller;


import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.io.FileUtils.getFile;

@RestController
@CrossOrigin
@RequestMapping(path = "/file")
public class FileController {

    private final static String CHARACTER = "UTF-8";//编码

    private final static String FILE_PATH = "filePath";//文件路径

    // 上传
    @PostMapping(path = "/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest req) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String resultStr = "{}";
        String fileTypeName = req.getParameter("fileTypeName");

        // 文件类型保存的位置
        if(StringUtils.isBlank(fileTypeName)){
            fileTypeName = FILE_PATH;
        }

        try{
            PropertiesUtil prop = new PropertiesUtil();
            String dirPath = prop.getValue(fileTypeName, true);

            // 查看文件路径存不存在
            File f = new File( dirPath );
            if( !f.exists() ){
                result.put("success",false);
                result.put("msg","存放位置 [ "+ dirPath +" ] 不存在！");
                resultStr= JSONUtil.toJsonFromMap(result).toString();
                return resultStr;
            }
            String filePath = dirPath + file.getOriginalFilename();
            File uploadFile = getFile(filePath);
            FileOutputStream out = new FileOutputStream(uploadFile);
            out.write(file.getBytes());
            out.flush();
            out.close();

            result.put("success",true);
            result.put("msg",file.getOriginalFilename() + " 文件存放成功");
            result.put("path",dirPath);

        }catch (Exception e){
            e.printStackTrace();
            result.put("success",false);
            result.put("msg",e.getMessage());
        }finally {
            resultStr= JSONUtil.toJsonFromMap(result).toString();
        }
        return resultStr;
    }

    // 下载
    @GetMapping(path = "/downloadFile")
    public String downloadFile(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String path = req.getParameter("path");
        String fileName = req.getParameter("fileName");
        String fileTypeName = req.getParameter("fileTypeName");
        String flagMsg = req.getParameter("flagMsg");

        if( flagMsg != null && !flagMsg.equals("") ){
            flagMsg = "true";
        }

        String resultStr = "{}";
        Map<String, Object> map = new HashMap<>();

        if( StringUtils.isBlank(path) || StringUtils.isBlank(fileName) ){
            map.put("msg","path = [ "+ path +" ] 或者 fileName = [ "+ fileName +" ] 为空 ");
            map.put("success",false);
            resultStr= JSONUtil.toJsonFromMap(map).toString();
            return resultStr;
        }
        if(StringUtils.isBlank(fileTypeName)){
            fileTypeName = FILE_PATH;
        }

        PropertiesUtil prop = new PropertiesUtil();
        String dirPath = prop.getValue(fileTypeName, true);
        String filePath = dirPath+path;
        // 测试文件是否存在
        File f = new File( filePath );
        if( !f.exists() ){
            // 尝试创建文件夹
            if( flagMsg.equals("true") ){
                f.mkdirs();
                map.put("success",false);
                map.put("msg","创建 成功 位置：[ " + f.getAbsolutePath() + " ]");
                resultStr= JSONUtil.toJsonFromMap(map).toString();
                return resultStr;
            }
        }else {

            map.put("success",false);
            map.put("msg"," 寻找路径为 [ " + f.getAbsolutePath() + " ] 失败....");
            resultStr= JSONUtil.toJsonFromMap(map).toString();
            return resultStr;
        }

        if(StringUtils.isBlank(fileName)){
            fileName = filePath;
            if(filePath.lastIndexOf(File.separator)>0){
                fileName = filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.length());
            }else if(filePath.lastIndexOf("/")>0){
                fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
            }
        }
        OutUtil.outDownFile(filePath, fileName, resp, req);
        return null;
    }

    /**
     * 生成压缩图片
     */
    private void compressImg(String path, String compressPath) throws Exception {
        String type = path.substring(path.lastIndexOf(".")+1, path.length());
        File orig = new File(path);
        File compressFile = getFile(compressPath);
        FileOutputStream out = new FileOutputStream(compressFile);
        try {
            Image src = ImageIO.read(orig);
            int maxW=300,maxH=300;
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            if(width>height){
                height = maxW * height / width;
                width = maxW;
            }else{
                width = maxH * width / height;
                height = maxH;
            }
            BufferedImage tag= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            tag.getGraphics().drawImage(image, 0, 0,  null);
            tag.getGraphics().dispose();
            ImageIO.write(tag, type, out);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(out!=null){
                out.close();
            }
        }
    }


}
