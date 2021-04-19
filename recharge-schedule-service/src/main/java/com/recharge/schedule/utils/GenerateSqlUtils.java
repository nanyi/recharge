package com.recharge.schedule.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
public class GenerateSqlUtils {
    public static void main(String[] args) throws ParseException {
        SqlBean sqlbean = new SqlBean();
        sqlbean.setDbCount(2);
        sqlbean.setTaskInfoCount(2);
        List<String> monthList = getMonthBetween("2021_4", "2021_5");
        monthList.forEach(month -> {
            log.info("生成表-{}", month);
        });
        sqlbean.setTaskInfoLogsMonthList(monthList);
        Map<String, Object> paramMp = new HashMap<String, Object>();
        paramMp.put("sqlbean", sqlbean);
        generate("sql.ftl", paramMp, "db_month.sql");
    }


    /**
     * 获取minDate maxDate 之间月份
     *
     * @param minDate
     * @param maxDate
     * @return
     * @throws ParseException
     */
    private static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException {
        ArrayList<String> result = new ArrayList<String>();
        // 格式化为年月
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_M");
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        while (min.before(max)) {
            result.add(sdf.format(min.getTime()));
            min.add(Calendar.MONTH, 1);
        }

        return result;
    }


    /**
     * 输出到文件
     *
     * @param name
     * @param root
     * @param outFile
     */
    public static void generate(String name, Map<String, Object> root, String outFile) {
        FileWriter out = null;
        try {

            Configuration cfg = new Configuration(Configuration.getVersion());
            // 设定去哪里读取相应的ftl模板文件
            cfg.setClassForTemplateLoading(GenerateSqlUtils.class, "/ftl");
            // 在模板文件目录中找到名称为name的文件
            Template temp = cfg.getTemplate(name);

            // 通过一个文件输出流，就可以写到相应的文件中
            out = new FileWriter(new File(outFile));

            temp.process(root, out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
