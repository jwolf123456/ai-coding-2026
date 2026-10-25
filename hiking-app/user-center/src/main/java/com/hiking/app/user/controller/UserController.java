package com.hiking.app.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hiking.app.common.result.R;
import com.hiking.app.user.entity.User;
import com.hiking.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public R<Void> create(@RequestBody User user) {
        userService.save(user);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody User user) {
        userService.updateById(user);
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<User> getById(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    @GetMapping("/page")
    public R<Page<User>> page(@RequestParam(defaultValue = "1") long current,
                              @RequestParam(defaultValue = "10") long size) {
        Page<User> page = new Page<>(current, size);
        return R.ok(userService.page(page));
    }
}