package org.example.peermatch.script;

import com.alibaba.excel.EasyExcel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LinZeyuan
 * @description 导入Excel
 * @createDate 2025/10/30 11:09
 */
public class UserImport {
    public static void main(String[] args) {
        String fileName = "C:\\Users\\LinZeyuan\\Desktop\\peer-match\\peer-match-backend\\src\\main\\resources\\prodExcel.xlsx";
        List<ExcelUserInfo> userInfoList = EasyExcel.read(fileName).head(ExcelUserInfo.class).sheet().doReadSync();
        System.out.println(userInfoList);
        Map<String, List<ExcelUserInfo>> userInfoGroup = userInfoList.stream().collect(Collectors.groupingBy(ExcelUserInfo::getUserName));
        System.out.println(userInfoGroup.keySet().size());
    }
}
