package cc.elvea.boot.ai;

import cc.elvea.boot.ai.tools.CommonTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AIApplication.class)
@Rollback(false)
public class OpenAiTests {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Autowired
    private OpenAiEmbeddingModel openAiEmbeddingModel;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private SessionMemoryAdvisor sessionMemoryAdvisor;

    @Autowired
    private CommonTools commonTools;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.openAiChatModel);

        ChatResponse response = ChatClient.builder(this.openAiChatModel).build().prompt()
            .user("你好")
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);
    }

    @Test
    public void sessionTest() {
        Assertions.assertNotNull(this.openAiChatModel);

        ChatClient chatClient = ChatClient.builder(this.openAiChatModel)
            .defaultAdvisors(this.sessionMemoryAdvisor)
            .defaultTools(this.commonTools)
            .build();

        ChatResponse response = chatClient
            .prompt()
            .user("查询系统时间")
            .advisors(a -> a.param(SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY, "session-abc"))
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);
    }

    @Test
    public void baseEmbeddingTest() {
        Assertions.assertNotNull(this.openAiEmbeddingModel);

        EmbeddingResponse response = this.openAiEmbeddingModel.embedForResponse(List.of(
            "Hello World",
            "World is big and salvation is near"
        ));
        Assertions.assertNotNull(response);
    }

    @Test
    public void baseVectorTest() {
        Assertions.assertNotNull(vectorStore);

        List<Document> documents = List.of(
            new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
            new Document("The World is Big and Salvation Lurks Around the Corner"),
            new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));
        vectorStore.add(documents);

        List<Document> result = vectorStore.similaritySearch(SearchRequest.builder()
            .query("Spring AI")
            .similarityThreshold(0.5)
            .topK(6)
            .filterExpression("author in ['john', 'jill'] && 'article_type' == 'blog'").build());
        Assertions.assertNotNull(result);
    }

    @Test
    public void baseRagTest() {
        Assertions.assertNotNull(this.openAiChatModel);

        ChatResponse response = ChatClient.builder(this.openAiChatModel)
            .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().build())
                .build()
            ).build()
            .prompt()
            .user("你好")
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);
    }

}
