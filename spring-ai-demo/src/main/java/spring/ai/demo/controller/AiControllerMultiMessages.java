package spring.ai.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import spring.ai.demo.service.AiServiceMultiMessage;

@RestController
@RequestMapping("/ai")
public class AiControllerMultiMessages {
    @Autowired
    private AiServiceMultiMessage aiService;

    @PostMapping(value = "/multi-message", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String multiMessages(@RequestParam("question") String question, HttpSession session) {
        List<Message> chatMemory = (List<Message>) session.getAttribute("chatMemory");
        if (chatMemory == null) {
            chatMemory = new ArrayList<Message>();
            session.setAttribute("chatMemory", chatMemory);
        }

        String answer = this.aiService.multiMessage(question, chatMemory);

        return answer;
    }

}
