package com.yiyixing.config;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 配置 flexmark 解析器和渲染器为 Spring Bean，全局复用。
 */
@Configuration
public class MarkdownConfig {

    @Bean
    public Parser markdownParser() {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, List.of(
                TablesExtension.create(),
                StrikethroughExtension.create()
        ));
        return Parser.builder(options).build();
    }

    @Bean
    public HtmlRenderer markdownRenderer() {
        MutableDataSet options = new MutableDataSet();
        // Extensions are registered via Parser.EXTENSIONS for both parser and renderer
        options.set(Parser.EXTENSIONS, List.of(
                TablesExtension.create(),
                StrikethroughExtension.create()
        ));
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
        return HtmlRenderer.builder(options).build();
    }
}
