//package com.lee.leeaitripagent.rag;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
//import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.ResourcePatternResolver;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@Slf4j
//public class TripAppDocumentLoader {
//    private final ResourcePatternResolver resourcePatternResolver;
//
//    TripAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
//        this.resourcePatternResolver = resourcePatternResolver;
//    }
//
//    public List<Document> loadMarkdowns() {
//        List<Document> allDocuments = new ArrayList<>();
//        try{
//            Resource[] resources = resourcePatternResolver.getResources("classpath:documents/*.md");
//            for (Resource resource : resources) {
//                String fileName = resource.getFilename();
//                String status = fileName.substring(fileName.length()-8, fileName.length() -3);
//                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
//                        .withHorizontalRuleCreateDocument(true)
//                        .withIncludeCodeBlock(false)
//                        .withIncludeBlockquote(false)
//                        .withAdditionalMetadata("filename", fileName)
//                        .withAdditionalMetadata("status", status)
//                        .build();
//                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
//                allDocuments.addAll(reader.get());
//            }
//        }
//        catch (Exception e){
//            log.error("Markdown 文档加载失败", e);
//        }
//        return allDocuments;
//    }
//}
