package com.hjc.scriptutil.tools;

import android.util.Log;
import com.hjc.util.Constants;
import java.io.File;
import java.io.IOException;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by hujiachun684 on 16/5/9.
 */
public class ExcelUtil {
    private WritableWorkbook wwb;
    private File file;
    private int row;


    public ExcelUtil(File file) {
        this.file = file;
    }

    // 创建excel表.
    public void createExcel(String appVersion) {

        WritableSheet ws = null;
        try {
                wwb = Workbook.createWorkbook(file);
                ws = wwb.createSheet("report", 0);

                // 在指定单元格插入数据
                Label lbl_moblie = new Label(0, 0, "系统版本");
                Label lbl_sdk = new Label(1, 0, android.os.Build.VERSION.RELEASE);
                Label lbl_package = new Label(0, 1, "APP版本");
                Label lbl_version = new Label(1, 1, appVersion);
                Label lbl_case = new Label(0, 2, "测试用例");
                Label lbl_step = new Label(1, 2, "测试步骤");
                Label lbl_expectation = new Label(2, 2, "预期结果");
                Label bll_result = new Label(3, 2, "测试结果");

                ws.addCell(lbl_moblie);
                ws.addCell(lbl_sdk);
                ws.addCell(lbl_package);
                ws.addCell(lbl_version);
                ws.addCell(lbl_case);
                ws.addCell(lbl_step);
                ws.addCell(lbl_expectation);
                ws.addCell(bll_result);

                wwb.write();
                wwb.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToExcel(String testcase, String result) {

        try {
            Workbook oldWwb = Workbook.getWorkbook(file);
            wwb = Workbook.createWorkbook(file,
                    oldWwb);
            WritableSheet ws = wwb.getSheet(0);
            // 当前行数
            row = ws.getRows();
            Label lbl1 = new Label(0, row, testcase);
            Label bll2 = new Label(1, row, result);
            row++;

            ws.addCell(lbl1);
            ws.addCell(bll2);


            wwb.write();
            wwb.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 更新指定xls
     * @param file xls文件
     * @param testcase 索引
     * @param result
     * @throws WriteException
     * @throws IOException
     * @throws BiffException
     */
    public static void updateExcel(File file, String testcase, String step, String expectation, String result) throws WriteException, IOException, BiffException {
        Workbook oldWwb = Workbook.getWorkbook(file);
        WritableWorkbook updateWWB = Workbook.createWorkbook(file,
                oldWwb);
        WritableSheet updateSHEET = updateWWB.getSheet("report");
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.RED);

        WritableCellFormat cellFormat = new WritableCellFormat(font);


        int updateLine = 0;
        for(int i = 1; i < updateSHEET.getRows(); i++){//遍历
            if(updateSHEET.getRow(i)[0].getContents().equals(testcase)){
                updateLine = i;
            }
        }
        if(updateLine != 0){
            Label bll = null;
            if(result.equals("false")){
                bll  = new Label(3, updateLine, result, cellFormat);
            }
            else{
                bll  = new Label(3, updateLine, result);
            }

            updateSHEET.addCell(bll);
            updateWWB.write();
            updateWWB.close();

        }
        else {

            try {

                // 当前行数
                int updateROW = updateSHEET.getRows();
                Log.e(Constants.TAG, "未索引到测试用例 新用例插入在第" + updateROW + "行");
                Label lbl1 = new Label(0, updateROW, testcase);

                Label lbl3 = new Label(1, updateROW, step);
                Label lbl4 = new Label(2, updateROW, expectation);

                Label bll2 = null;
                if(result.equals("false")){
                    bll2 = new Label(3, updateROW, result, cellFormat);
                }
                else {
                    bll2 = new Label(3, updateROW, result);
                }

                updateSHEET.addCell(lbl1);
                updateSHEET.addCell(bll2);
                updateSHEET.addCell(lbl3);
                updateSHEET.addCell(lbl4);


                updateWWB.write();
                updateWWB.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
