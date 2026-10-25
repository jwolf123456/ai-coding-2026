package com.hiking.app.line.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hiking.app.common.result.R;
import com.hiking.app.line.entity.Line;
import com.hiking.app.line.service.LineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/line")
@RequiredArgsConstructor
public class LineController {

    private final LineService lineService;

    @PostMapping
    public R<Void> create(@RequestBody Line line) {
        lineService.save(line);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        lineService.removeById(id);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody Line line) {
        lineService.updateById(line);
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<Line> getById(@PathVariable Long id) {
        return R.ok(lineService.getById(id));
    }

    @GetMapping("/page")
    public R<Page<Line>> page(@RequestParam(defaultValue = "1") long current,
                              @RequestParam(defaultValue = "10") long size) {
        Page<Line> page = new Page<>(current, size);
        return R.ok(lineService.page(page));
    }
}