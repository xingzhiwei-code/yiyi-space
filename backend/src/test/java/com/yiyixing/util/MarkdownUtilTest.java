package com.yiyixing.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarkdownUtilTest {

    private Parser parser;
    private HtmlRenderer renderer;
    private MarkdownUtil markdownUtil;

    @BeforeEach
    void setUp() {
        parser = mock(Parser.class);
        renderer = mock(HtmlRenderer.class);
        markdownUtil = new MarkdownUtil(parser, renderer);
    }

    @Test
    @DisplayName("null 输入 — 返回空字符串")
    void render_null_returnsEmpty() {
        String result = markdownUtil.render(null);
        assertEquals("", result);
    }

    @Test
    @DisplayName("空白输入 — 返回空字符串")
    void render_blank_returnsEmpty() {
        assertEquals("", markdownUtil.render("   "));
        assertEquals("", markdownUtil.render("\n"));
    }

    @Test
    @DisplayName("正常 Markdown — 调用 parser 和 renderer")
    void render_validMarkdown_callsParserAndRenderer() {
        Document doc = mock(Document.class);
        when(parser.parse("**bold**")).thenReturn(doc);
        when(renderer.render(doc)).thenReturn("<p><strong>bold</strong></p>");

        String result = markdownUtil.render("**bold**");

        assertEquals("<p><strong>bold</strong></p>", result);
        verify(parser).parse("**bold**");
        verify(renderer).render(doc);
    }
}
