package com.gordeev.dbclient.ui;

import com.gordeev.dbclient.entity.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class HtmlCreator {
    private static final Logger LOG = LoggerFactory.getLogger(HtmlCreator.class);

    public void createHtml(Data data) {
        try {
            File file = new File("resultOfLastQuery.html");
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else { file.createNewFile();}
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

            writer.write("<!DOCTYPE html>");
            writer.write("<html lang=\"en\">");
            writer.write("<head>");
            writer.write("<meta charset=\"UTF-8\">");
            writer.write("<title>Welcome page</title>");
            writer.write("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css\" integrity=\"sha384-9gVQ4dYFwwWSjIDZnLEWnxCjeSWFphJiwGPXr1jddIhOegiu1FwO5qRGvFXOdJZ4\" crossorigin=\"anonymous\">");
            writer.write("</head>");
            writer.write("<body>");

            writer.write("<table  class=\"table table-striped table-dark\">");
            writer.write("<thead>");
            writer.write("<tr>");
            for (int i = 0; i < data.getColumnCount(); i++) {
                writer.write("<th  scope=\"col\">" + data.getColumnName(i) + "</th>");
            }
            writer.write("</tr>");
            writer.write("</thead>");
            writer.write("<tbody>");
            for (int i = 0; i < data.getRowCount(); i++) {
                writer.write("<tr  scope=\"row\">");
                for (int j = 0; j < data.getColumnCount(); j++) {
                    writer.write("<td>" + data.getValue(j, i) + "</td>");
                }
                writer.write("</tr>");
            }
            writer.write("</tbody>");
            writer.write("</table>");

            writer.write("<script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\"></script>");
            writer.write("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js\" integrity=\"sha384-cs/chFZiN24E4KMATLdqdvsezGxaGsi4hLGOzlXwp5UZB1LY//20VyM2taTB4QvJ\" crossorigin=\"anonymous\"></script>");
            writer.write("<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js\" integrity=\"sha384-uefMccjFJAIv6A+rW+L4AHf99KvxDjWSu1z9VI8SKNVmz4sk7buKt/6v9KI65qnm\" crossorigin=\"anonymous\"></script>");
            writer.write("</body>");
            writer.flush();
            writer.close();
            LOG.info("HtmlCreator: file \"resultOfLastQuery.html\" was created");
        } catch (IOException e) {
            LOG.info("HtmlCreator: file \"resultOfLastQuery.html\" was NOT created");
            //throw new RuntimeException("Cannot access to the file *.html");
        }
    }
}
