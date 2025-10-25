package com.study.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@RestController
public class BoardController {

    private BoardService boardService;

    public BoardController(BoardService boardService){
        this.boardService = boardService;
    }

    @GetMapping("boards")
    public List<Board> getBoards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return boardService.getBoards(page, size);
    }

    @PostMapping("/chat/run")
    public ResponseEntity<ChatBotResponse> runChat(){
        ChatBotResponse.Timing timing = new ChatBotResponse.Timing(1, 2, 3);
        ChatBotResponse.ApiContext apiContext = new ChatBotResponse.ApiContext("serviceId", "endpointId", "serviceGeneration");
        ChatBotResponse.RefDict refDict = new ChatBotResponse.RefDict(1, "1", "1", "1", "1", "1", "1", "1", "1", 1.0, "1", "1", "1");
        ChatBotResponse.ApiResponseData apiResponseData = new ChatBotResponse.ApiResponseData("answer", Arrays.asList(refDict, refDict), Arrays.asList("ref_str", "aa"));
        ChatBotResponse response = new ChatBotResponse(apiResponseData, timing, apiContext);
        return ResponseEntity.ok(response);
    }

    public record ChatBotResponse(
            ApiResponseData response,
            Timing timing,
            ApiContext apiContext
    ) {

        public record ApiResponseData(
                String answer,

                List<RefDict> ref_dict,

                List<String> ref_str
        ) {
        }

        @Data
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Timing {
            Integer preProcessing;
            Integer execution;
            Integer functionInternal;
        }

        public record ApiContext(
                String serviceId,
                String endpointId,
                String serviceGeneration
        ) {
        }

        public record RefDict(
                Integer index,
                String content,
                String doc_name,
                String page_number,
                String equip_no,
                String group_name,
                String room_name,
                String room_number,
                String equip_name,
                Double score,
                String image_paths,
                String lvl2,
                String lvl3
        ) {
        }


    }
}
