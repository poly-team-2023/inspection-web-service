package com.service.inspection.document;

import java.io.File;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.data.NumberingFormat;
import com.deepoove.poi.data.NumberingItemRenderData;
import com.deepoove.poi.data.NumberingRenderData;
import com.deepoove.poi.data.Numberings;
import com.deepoove.poi.data.Paragraphs;
import com.deepoove.poi.data.TextRenderData;
import com.google.common.collect.ImmutableList;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Text;

public class DocumentProcessor {

    private final File mainTemplateFile;
    private final ConfigureBuilder builder = Configure.builder();

    public DocumentProcessor() throws IOException {
        mainTemplateFile = new ClassPathResource("main.docx").getFile();

        builder.useSpringEL();
    }

    public void fillTemplate(String s) throws IOException {

        List<NumberingItemRenderData> numberingRenderData = new ArrayList<>();
        numberingRenderData.add(new NumberingItemRenderData(0, Paragraphs.of("text").create()));
        numberingRenderData.add(new NumberingItemRenderData(1, Paragraphs.of("text1").create()));
        numberingRenderData.add(new NumberingItemRenderData(1, Paragraphs.of("text2").create()));
        numberingRenderData.add(new NumberingItemRenderData(1, Paragraphs.of("text3").create()));
        numberingRenderData.add(new NumberingItemRenderData(0, Paragraphs.of("text4").create()));
        numberingRenderData.add(new NumberingItemRenderData(1, Paragraphs.of("text3").create()));


        NumberingRenderData n = new NumberingRenderData();
        n.setItems(numberingRenderData);
        n.setFormats(Collections.nCopies(numberingRenderData.size(), NumberingFormat.DECIMAL));



        XWPFTemplate.compile(mainTemplateFile, builder.build()).render(new HashMap<String, Object>() {{
            put("res", s);
            put("name", "Чешев");
            put("num", n);
        }}).writeToFile("main-after.docx");

    }
    static class NumberingRenderDataWithLevels extends NumberingRenderData {
    }
}
