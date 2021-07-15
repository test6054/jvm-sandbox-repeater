package com.alibaba.repeater.console.start.controller.page;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ReplayBO;
import com.alibaba.repeater.console.common.params.ReplayBatchParams;
import com.alibaba.repeater.console.common.params.ReplayParams;
import com.alibaba.repeater.console.service.ReplayService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * {@link ReplayController}
 * <p>
 *
 * @author zhaoyb1990
 */
@Controller
@RequestMapping("/replay")
public class ReplayController {

    @Resource
    private ReplayService replayService;

    @RequestMapping("detail.htm")
    public String detail(@ModelAttribute("requestParams") ReplayParams params, Model model) {
        RepeaterResult<ReplayBO> result = replayService.query(params);
        if (!result.isSuccess()) {
            return "error/404";
        }
        model.addAttribute("replay", result.getData());
        model.addAttribute("record", result.getData().getRecord());
        return "replay/detail";
    }

    @RequestMapping("execute.json")
    @ResponseBody
    public RepeaterResult<String> replay(@ModelAttribute("requestParams") ReplayParams params) {
        return replayService.replay(params);
    }

    @RequestMapping("execute")
    @ResponseBody
    public RepeaterResult<String> replayExecute(@RequestBody ReplayParams params) {
        return replayService.replay(params);
    }


    @RequestMapping("execute-list.json")
    @ResponseBody
    public RepeaterResult<String> replayList(@RequestBody List<ReplayParams> params) {
        return replayService.replayList(params);
    }


    @PostMapping("execute-replay-batch.json")
    @ResponseBody
    public RepeaterResult<String> replayBatch(@RequestBody ReplayBatchParams params) {
        return replayService.replayBatch(params);
    }

}
