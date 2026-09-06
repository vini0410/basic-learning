package com.study.NullObjectApp.controller

import com.study.NullObjectApp.dto.AccessCheckResponse
import com.study.NullObjectApp.dto.CheckAccessRequest
import com.study.NullObjectApp.dto.UserProfileDto
import com.study.NullObjectApp.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/profile")
    fun getUserProfile(@RequestParam(required = false) userId: String?): ResponseEntity<UserProfileDto> {
        val profile = userService.getUserProfile(userId)
        return ResponseEntity.ok(profile)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<UserProfileDto> {
        val profile = userService.getUserProfile(id)
        return ResponseEntity.ok(profile)
    }

    @PostMapping("/check-access")
    fun checkAccess(@RequestBody request: CheckAccessRequest): ResponseEntity<AccessCheckResponse> {
        val response = userService.checkUserAccess(request)
        return ResponseEntity.ok(response)
    }
}
